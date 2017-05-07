package com.msr.dnsdemo.second;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by SANDEEP on 02-05-2017.
 */

public class WifiApManager {
    private final WifiManager mWifiManager;
    private IOnDeviceDetected iOnDeviceDetected = null;

    public WifiApManager(Context context) {
        iOnDeviceDetected = (IOnDeviceDetected) context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    /**
     * Gets a list of the clients connected to the Hotspot, reachable timeout is 300
     *
     * @param onlyReachables {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @return ArrayList of {@link ClientScanResult}
     */
    public ArrayList<ClientScanResult> getClientList(boolean onlyReachables) {
        return getClientList(onlyReachables, 1000);
    }

    /**
     * Gets a list of the clients connected to the Hotspot
     *
     * @param onlyReachables   {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param reachableTimeout Reachable Timout in miliseconds
     * @return ArrayList of {@link ClientScanResult}
     */
    public ArrayList<ClientScanResult> getClientList(boolean onlyReachables, int reachableTimeout) {
        BufferedReader br = null;
        ArrayList<ClientScanResult> result = null;

        try {
            result = new ArrayList<ClientScanResult>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");

                if ((splitted != null) && (splitted.length >= 4)) {
                    // Basic sanity check
                    String mac = splitted[3];

                    if (mac.matches("..:..:..:..:..:..")) {
                        InetAddress inetAddress = InetAddress.getByName(splitted[0]);

                        boolean isReachable = false;
                        try {
                            if (inetAddress != null) {
                                isReachable = inetAddress.isReachable(reachableTimeout);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

/*                        if (!onlyReachables || isReachable) {
                            result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));
                        }*/
                        if (isReachable) {
                            ClientScanResult clientScanResult = new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable);
                            Log.i("===name::", "===" + inetAddress.getHostAddress());
                            String hostName = inetAddress.getCanonicalHostName();
                            String deviceName = inetAddress.getHostName();
                            clientScanResult.setDeviceName(deviceName);
                            clientScanResult.setName(hostName);
                            result.add(clientScanResult);
                            iOnDeviceDetected.onDeviceDetected(clientScanResult);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().toString(), e.getMessage());
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), e.getMessage());
            }
        }

        return result;
    }
}
