import { registerPlugin } from "@capacitor/core";

import type { IncasaBLEPlugin } from "./definitions";

const IncasaBLE = registerPlugin<IncasaBLEPlugin>("IncasaBLE");

export * from "./definitions";

export { IncasaBLE };
