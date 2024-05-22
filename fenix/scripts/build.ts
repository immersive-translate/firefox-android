import updateExtension from "./update-extension.ts";
import { getProperties, saveProperties } from "./util.ts";

const channels = ["google", "mainlandStore", "official"];
async function main() {
  // await updateExtension();
  const meta = await getProperties() as any;

  await removeFilesWithRegex("./app/apks",new RegExp(meta.appVersion));
  for (const channel of channels) {
    meta.channel = channel;
    await saveProperties(meta);

    const assembleArgs = channel == "google" ? "bundleRelease" : "assembleRelease";
    const process = Deno.run({
      cmd: ["./gradlew.bat", assembleArgs, "-b", "./app/build.gradle"],
    });
    await process.status(); // 等待进程完成
    process.close(); // 关闭进程资源
  }
}

async function removeFilesWithRegex(dir: string, keyword: RegExp) {
    try {
      const entries = Deno.readDir(dir); // 读取目录内容
      for await (const entry of entries) {
          const fullPath = `${dir}/${entry.name}`;
          if (entry.isDirectory) {
              // 如果是目录，则递归调用此函数
              await removeFilesWithRegex(fullPath, keyword);
          } else if (entry.isFile && keyword.test(entry.name)) {
              // 如果是文件且文件名包含关键词，则打印文件路径
              await Deno.remove(fullPath);
              console.log(`Deleted: ${fullPath}`); // 可选：打印已删除的文件路径
          }
      }
    } catch (err) {
      console.error(err);
    }
}

main();
