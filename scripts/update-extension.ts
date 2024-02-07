const configFile = "./fenix/app/immersive.properties";

// 检查文件是否存在
async function checkFileExists(file) {
  try {
    await Deno.stat(file);
    return true;
  } catch (error) {
    if (error instanceof Deno.errors.NotFound) {
      return false;
    } else {
      throw error;
    }
  }
}

// 删除文件或目录（如果存在）
async function removeIfExists(path, options = {}) {
  if (await checkFileExists(path)) {
    await Deno.remove(path, options);
    console.log(`Removed: ${path}`);
  } else {
    console.log(`File not found, skipping remove: ${path}`);
  }
}

// 从 https://addons.mozilla.org/zh-CN/firefox/addon/immersive-translate-beta/ 这个页面中提取版本号 <dd class="Definition-dd AddonMoreInfo-version">1.1.5</dd>
async function extractVersion() {
  // 请求这个网页内容 
  const res = await fetch("https://addons.mozilla.org/zh-CN/firefox/addon/immersive-translate-beta/");
  const html = await res.text();

  const reg = /<dd class="Definition-dd AddonMoreInfo-version">(.*?)<\/dd>/;
  const match = html.match(reg);
  if (match) {
    return match[1];
  }
  return "";
}

// 主要逻辑
async function main() {
  try {
    const version = await extractVersion();
    const downloadUrl = `https://addons.mozilla.org/firefox/downloads/file/4221309/immersive_translate_beta-${version}.xpi`;
    const downloadPath = `./fenix/app/src/main/assets/ts/immersive_translate_beta-${version}.xpi`;
    const unzipPath = `./fenix/app/src/main/assets/ts/immersive_translate_beta-${version}`;

    const removeDir = "./fenix/app/src/main/assets/ts/";
    const removeDirExists = await checkFileExists(removeDir);
    if (removeDirExists) {
      const removeDirFiles = await Deno.readDir(removeDir);
      for await (const file of removeDirFiles) {
        await removeIfExists(`${removeDir}${file.name}`, { recursive: true });
      }
    }


    await Deno.mkdir(unzipPath, { recursive: true });
    const downloadResponse = await fetch(downloadUrl);
    const downloadBuffer = await downloadResponse.arrayBuffer();
    await Deno.writeFile(downloadPath, new Uint8Array(downloadBuffer));
    console.log(`Downloaded: ${downloadPath}`);

    // 解压资源
    const unzip = Deno.run({
      cmd: ["unzip", downloadPath, "-d", unzipPath],
    });
    await unzip.status();
    unzip.close();

    await Deno.writeTextFile(configFile, `extension=${version}`);
  } catch (error) {
    console.error(`Error: ${error.message}`);
  }
}

// 执行主要逻辑
main();
