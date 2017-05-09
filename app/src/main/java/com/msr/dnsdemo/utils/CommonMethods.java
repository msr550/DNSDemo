package com.msr.dnsdemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.Window;

import com.msr.dnsdemo.R;

import static com.msr.dnsdemo.network.NetInfo.getIpFromIntSigned;

/**
 * Created by SANDEEP on 06-05-2017.
 */

public class CommonMethods {
    /**
     * to create and return the dialog
     *
     * @param context context as parameter
     * @return dialog object
     */
    public static Dialog progressDialog(Context context) {
        Dialog dialog = null;
        try {
            dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.view_progress_dialog);
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return null;
        }

        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        Logger.getInfo("SSID::" + ssid);
        return ssid;
    }

    public static String getCurrentSsid1(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        Logger.getInfo("SSID1::" + ssid);
        return ssid;
    }

    public static boolean getWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            // Set wifi variables
            int speed = info.getLinkSpeed();
            String ssid = info.getSSID();
            String bssid = info.getBSSID();
            String macAddress = info.getMacAddress();
            String gatewayIp = getIpFromIntSigned(wifi.getDhcpInfo().gateway);
            // broadcastIp = getIpFromIntSigned((dhcp.ipAddress & dhcp.netmask)
            // | ~dhcp.netmask);
            String netmaskIp = getIpFromIntSigned(wifi.getDhcpInfo().netmask);
            return true;
        }
        return false;
    }
}
