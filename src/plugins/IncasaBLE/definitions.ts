import type { PluginListenerHandle } from "@capacitor/core";

export interface DeviceInfo {
    deviceName: String;
}

export interface IncasaBLEPlugin {
    addListener(
        eventName: "measureChange",
        listenerFunc: (measurement: Number) => void
    ): Promise<PluginListenerHandle> & PluginListenerHandle;
    addListener(
        eventName: "pairingChange",
        listenerFunc: (paring: boolean) => void
    ): Promise<PluginListenerHandle> & PluginListenerHandle;
    verifyRequiredPermissions(): Promise<{ verified: boolean }>;
    startDevice(deviceName: DeviceInfo): Promise<void>;
    removeAllListeners(): Promise<void>;
}
