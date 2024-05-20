update:
	deno run -A ./scripts/update-extension.ts
google:
	deno run -A ./scripts/properties.ts channel google
store:
	deno run -A ./scripts/properties.ts channel mainlandStore
official:
	deno run -A ./scripts/properties.ts channel official