// Deno 环境
// 获取命令行参数 默认第一个参数为 version
const version = Deno.args[0];
// 下载资源并解压到 ../fenix/app/src/main/assets/ts
const downloadUrl = `https://addons.mozilla.org/firefox/downloads/file/4221309/immersive_translate_beta-${version}.xpi`;
const downloadPath = `../fenix/app/src/main/assets/ts/immersive_translate_beta-${version}.xpi`;
const unzipPath = `../fenix/app/src/main/assets/ts/immersive_translate_beta-${version}`;
const configFile = "../fenix/app/immersive.properties";

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

// 主要逻辑
async function main() {
  try {
    // 删除 ../fenix/app/src/main/assets/ts/* 下所有内容
    const removeDir = "../fenix/app/src/main/assets/ts/";
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
