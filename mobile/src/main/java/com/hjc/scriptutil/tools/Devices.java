package com.hjc.scriptutil.tools;

/**
 * Created by hujiachun on 16/10/24.
 */

public class Devices {
    private String devicesName;
    private String AndroidVersion;
    private String DevicesMemory;
    private String DevicesCpu;

    public String getDevicesName() {
        return devicesName;
    }

    public void setDevicesName(String devicesName) {
        this.devicesName = devicesName;
    }

    public String getAndroidVersion() {
        return AndroidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        AndroidVersion = androidVersion;
    }

    public String getDevicesMemory() {
        return DevicesMemory;
    }

    public void setDevicesMemory(String devicesMemory) {
        DevicesMemory = devicesMemory;
    }

    public String getDevicesCpu() {
        return DevicesCpu;
    }

    public void setDevicesCpu(String devicesCpu) {
        DevicesCpu = devicesCpu;
    }
}
