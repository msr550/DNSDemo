package com.msr.dnsdemo.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.msr.dnsdemo.BuildConfig;
import com.msr.dnsdemo.R;
import com.msr.dnsdemo.adapters.SecondAdapter;
import com.msr.dnsdemo.second.ClientScanResult;
import com.msr.dnsdemo.second.IOnDeviceDetected;
import com.msr.dnsdemo.second.WifiApManager;
import com.msr.dnsdemo.utils.CommonMethods;
import com.msr.dnsdemo.utils.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener, IOnDeviceDetected {
    ArrayList<ClientScanResult> deviceInfoArrayList = new ArrayList();
    private TextView textView1;
    private SecondAdapter adapter = null;
    private ProgressBar pBar;

    public static Future<Boolean> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    Log.i("===Port", "==Open::" + port);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView1 = (TextView) findViewById(R.id.textview);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //     this.recyclerView.setNestedScrollingEnabled(false);
        this.adapter = new SecondAdapter(this.deviceInfoArrayList, getApplicationContext());
        recyclerView.setAdapter(this.adapter);
        CommonMethods.getCurrentSsid(this);
        CommonMethods.getCurrentSsid1(this);
        CommonMethods.getWifiInfo(this);
        test();
        // scan();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.searchBtn:
                new TestTask().execute();
                break;
            case R.id.nextBtn:
                startActivity(new Intent(this, NewMainActivity.class));
                break;
            case R.id.serverBtn:
                startActivity(new Intent(this, ServerActivity.class));
                break;
            case R.id.clientBtn:
                startActivity(new Intent(this, ClientActivity.class));
                break;
        }
    }

    public void test() {

        final ExecutorService es = Executors.newFixedThreadPool(20);
        final String ip = "192.168.1.14";
        final int timeout = 200;
        final List<Future<Boolean>> futures = new ArrayList<>();
        for (int port = 1; port <= 65535; port++) {
            futures.add(portIsOpen(es, ip, port, timeout));
        }
        es.shutdown();
        int openPorts = 0;
        for (final Future<Boolean> f : futures) {
            try {
                if (f.get()) {
                    openPorts++;
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Log.i("===IP:::", "====Ports" + openPorts);
        System.out.println("There are " + openPorts + " open ports on host " + ip + " (probed with a timeout of " + timeout + "ms)");
    }

    @Override
    public void onDeviceDetected(final ClientScanResult clientScanResult) {
        //deviceName(clientScanResult.getHWAddr());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               /* String hostName =  SecondActivity.this.clientScanResult.getCanonicalHostName();
                String deviceName =  SecondActivity.this.clientScanResult.getHostName();
                clientScanResult.setName(hostName);*/
                Logger.getError("DeviceName:" + clientScanResult.getDeviceName());
                Logger.getError("Name::" + clientScanResult.getName());
                Logger.getError("Mac::" + clientScanResult.getHWAddr());
                Logger.getError("Device::" + clientScanResult.getDevice());
                deviceInfoArrayList.add(clientScanResult);
                Collections.sort(deviceInfoArrayList);
                adapter.notifyDataSetChanged();
            }
        });

    }

    String deviceName(String macAddress) {
        String responseStr = BuildConfig.FLAVOR;
        HttpURLConnection connection = null;
        Logger.getInfo("Get Request");
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
        Logger.getInfo("Server Response" + responseStr);
        return responseStr;
    }

    private class TestTask extends AsyncTask<Void, Void, ArrayList<ClientScanResult>> {
        private Dialog dialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   dialog = CommonMethods.progressDialog(SecondActivity.this);
            pBar.setVisibility(View.VISIBLE);
            deviceInfoArrayList.clear();
        }

        @Override
        protected ArrayList<ClientScanResult> doInBackground(Void... voids) {
            ArrayList<ClientScanResult> clients = new WifiApManager(SecondActivity.this).getClientList(true);
            for (ClientScanResult clientScanResult : clients) {
                String testIp = clientScanResult.getIpAddr().replace("/", "");
                Log.i("TAG", "TestIP: " + testIp);
                InetAddress address = null;
                try {
                    address = InetAddress.getByName(testIp);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
              /*  boolean reachable = false;
                try {
                    // reachable = address.isReachable(1000);
                    // if (reachable) {
                    String hostName = address.getCanonicalHostName();
                    String deviceName = address.getHostName();
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setName(hostName);
                    deviceInfo.setIP(testIp);
                    clientScanResult.setName(hostName);
                    deviceInfoArrayList.add(clientScanResult);
                    if (reachable) {
                        Log.e("HostName", InetAddress.getByName(testIp).getHostName());
                        //   NewMainActivity.this.deviceInfoArrayList.add(deviceInfo);
                        Log.i("TAG", "Host: " + String.valueOf(deviceName) + "(" + String.valueOf(testIp) + ") is reachable!");
                          *//*  NewMainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    NewMainActivity.this.adapter.notifyDataSetChanged();
                                }
                            });*//*
                        //    }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
            return clients;
        }

        @Override
        protected void onPostExecute(ArrayList<ClientScanResult> clients) {
            super.onPostExecute(clients);
            textView1.append("Clients: \n");
            adapter.notifyDataSetChanged();
            Logger.getInfo("Count::" + clients.size());
            for (ClientScanResult clientScanResult : clients) {
                textView1.append("####################\n");
                textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                textView1.append("Device: " + clientScanResult.getDevice() + "\n");
                textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                textView1.append("isReachable: " + clientScanResult.isReachable() + "\n");
            }
           /* if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }*/
            pBar.setVisibility(View.GONE);
        }
    }

}
