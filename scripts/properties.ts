import { updateProperties } from "./util.ts";

async function main() {
  const args = Deno.args;
  if (!args.length) {
    console.log("not found args");
    return;
  }
  const key = args[0];
  const value = args[1];
  if (!key || !value) return;

  await updateProperties(key, value);
}

main();
