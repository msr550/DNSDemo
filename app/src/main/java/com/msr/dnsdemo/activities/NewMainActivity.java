package com.msr.dnsdemo.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.msr.dnsdemo.R;
import com.msr.dnsdemo.adapters.ConnectedDevicesAdapters;
import com.msr.dnsdemo.model.DeviceInfo;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;

public class NewMainActivity extends AppCompatActivity {
    ConnectedDevicesAdapters adapter;
    ArrayList<DeviceInfo> deviceInfoArrayList = new ArrayList();
    // private LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    StringBuilder sb;
    private boolean isStopped;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        this.recyclerView = (RecyclerView) findViewById(R.id.list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //     this.recyclerView.setNestedScrollingEnabled(false);
        this.adapter = new ConnectedDevicesAdapters(this.deviceInfoArrayList, getApplicationContext());
        this.recyclerView.setAdapter(this.adapter);
      /*  this.fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NewMainActivity.this.deviceInfoArrayList = new ArrayList();
                NewMainActivity.this.adapter = new ConnectedDevicesAdapters(NewMainActivity.this.deviceInfoArrayList, NewMainActivity.this.getApplicationContext());
                NewMainActivity.this.recyclerView.setAdapter(NewMainActivity.this.adapter);
                NewMainActivity.this.adapter.notifyDataSetChanged();
                new NetworkSniffTask(MainActivity.this).execute(new Void[0]);
            }
        });*/
        new NetworkSniffTask(this).execute(new Void[0]);
        ImageView refreshIV = (ImageView) findViewById(R.id.refreshIV);
        refreshIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStopped = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new NetworkSniffTask(NewMainActivity.this).execute(new Void[0]);
            }
        });
    }

    @Override
    protected void onDestroy() {
        isStopped = true;
        super.onDestroy();
    }

   /* String deviceName(String macAddress) {
        String responseStr = BuildConfig.FLAVOR;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL("http://api.macvendors.com/" + macAddress).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.flush();
            wr.close();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while (true) {
                String line = rd.readLine();
                if (line == null) {
                    break;
                }
                response.append(line);
                response.append('\r');
            }
            rd.close();
            responseStr = response.toString();
            Log.d("Server response", responseStr);
            if (connection != null) {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseStr;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseStr;
    }

    public static String getMacAddr() {
        try {
            for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return BuildConfig.FLAVOR;
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(Integer.toHexString(b & 255) + ":");
                    }
                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }
        } catch (Exception e) {
        }
        return "02:00:00:00:00:00";
    }

    public String getMacAddress(String ip) {
        String s = null;
        try {
            String newStr = "nmap -sP " + ip;
            Log.e("URL STR", newStr);
            Process proc = Runtime.getRuntime().exec(newStr);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while (true) {
                s = stdInput.readLine();
                if (s == null) {
                    break;
                }
                System.out.println(s);
                while (true) {
                    s = stdError.readLine();
                    if (s != null) {
                        System.err.println(s);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return s;
    }

    private String getMacAddressByUseArp(String ip) throws IOException {
        Scanner s = new Scanner(Runtime.getRuntime().exec("arp -a " + ip).getInputStream());
        String str = null;
        Pattern pattern = Pattern.compile("(([0-9A-Fa-f]{2}[-:]){5}[0-9A-Fa-f]{2})|(([0-9A-Fa-f]{4}\\.){2}[0-9A-Fa-f]{4})");
        while (s.hasNext()) {
            try {
                str = s.next();
                if (pattern.matcher(str).matches()) {
                    break;
                }
                str = null;
            } catch (Throwable th) {
                s.close();
            }
        }
        s.close();
        if (str != null) {
            return str.toUpperCase();
        }
        return null;
    }

    void getAddress(InetAddress ip) {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            System.out.print("MAC address : ");
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < mac.length) {
                String str = "%02X%s";
                Object[] objArr = new Object[2];
                objArr[0] = Byte.valueOf(mac[i]);
                objArr[1] = i < mac.length + -1 ? "-" : BuildConfig.FLAVOR;
                sb.append(String.format(str, objArr));
                i++;
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public class NetworkSniffTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> mContextRef;

        public NetworkSniffTask(Context context) {
            this.mContextRef = new WeakReference(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isStopped = false;
            NewMainActivity.this.deviceInfoArrayList.clear();
            Log.i("===OnPre", "===onPreExecuted");
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NewMainActivity.this.adapter.notifyDataSetChanged();
            Log.i("===OnPost", "===onPostExecuted");
        }

        protected Void doInBackground(Void... voids) {
            Log.d("TAG", "Let's sniff the network");
            try {
                Context context = (Context) this.mContextRef.get();
                if (context != null) {

                    //NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
                    NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                    //String ipString = Formatter.formatIpAddress(((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getIpAddress());
                    String ipString = Formatter.formatIpAddress(((WifiManager) context.getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress());
                    Log.d("TAG", "activeNetwork: " + String.valueOf(activeNetwork));
                    Log.d("TAG", "ipString: " + String.valueOf(ipString));
                    String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                    Log.d("TAG", "prefix: " + prefix);
                    for (int i = 0; i < 255; i++) {
                        if (!isStopped) {
                            Log.i("===I Value", "===i Value:" + i);
                            String testIp = prefix + String.valueOf(i);
                            InetAddress address = InetAddress.getByName(testIp);
                            boolean reachable = address.isReachable(1000);
                            if (reachable) {
                                String hostName = address.getCanonicalHostName();
                                String deviceName = address.getHostName();
                                DeviceInfo deviceInfo = new DeviceInfo();
                                deviceInfo.setName(hostName);
                                deviceInfo.setIP(testIp);
                                if (reachable) {
                                    Log.e("HostName", InetAddress.getByName(testIp).getHostName());
                                    NewMainActivity.this.deviceInfoArrayList.add(deviceInfo);
                                    Log.i("TAG", "Host: " + String.valueOf(deviceName) + "(" + String.valueOf(testIp) + ") is reachable!");
                                    NewMainActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            NewMainActivity.this.adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        } else {
                            return null;
                        }
                    }
                }
            } catch (Throwable t) {
                Log.e("TAG", "Well that's not good.", t);
            }
            return null;
        }
    }
}
