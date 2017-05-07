package com.msr.dnsdemo.second;

import android.support.annotation.NonNull;

/**
 * Created by SANDEEP on 02-05-2017.
 */

public class ClientScanResult implements Comparable<ClientScanResult> {

    private String IpAddr;

    private String HWAddr;

    private String Device;

    private boolean isReachable;
    private String name;
    private String deviceName;

    public ClientScanResult(String ipAddr, String hWAddr, String device, boolean isReachable) {
        super();
        IpAddr = ipAddr;
        HWAddr = hWAddr;
        Device = device;
        this.setReachable(isReachable);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    public String getHWAddr() {
        return HWAddr;
    }

    public void setHWAddr(String hWAddr) {
        HWAddr = hWAddr;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean isReachable) {
        this.isReachable = isReachable;
    }

    @Override
    public int compareTo(@NonNull ClientScanResult clientScanResult) {
        return this.IpAddr.compareTo(clientScanResult.getIpAddr());
    }
}