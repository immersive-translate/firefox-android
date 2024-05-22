const configFilePath = "./app/immersive.properties";

export async function getProperties() {
  const decoder = new TextDecoder("utf-8");
  const data = await Deno.readFile(configFilePath);
  const content = decoder.decode(data);
  const obj = {};
  content.split("\n").forEach((item) => {
    const values = item.split("=");
    obj[values[0]] = values[1];
  });
  return obj;
}

export async function saveProperties(obj) {
  const result = Object.entries(obj).map(([key, value]) => `${key}=${value}`)
    .join("\n");
  Deno.writeTextFile(configFilePath, result);
}

export async function updateProperties(key,value) {
  const obj = await getProperties();
  obj[key] = value;
  await saveProperties(obj);
}