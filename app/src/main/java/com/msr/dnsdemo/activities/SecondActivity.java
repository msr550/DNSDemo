package com.msr.dnsdemo.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.msr.dnsdemo.R;
import com.msr.dnsdemo.second.ClientScanResult;
import com.msr.dnsdemo.second.WifiApManager;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView1 = (TextView) findViewById(R.id.textview);
        // scan();
        new TestTask().execute();
    }

    private class TestTask extends AsyncTask<Void, Void, ArrayList<ClientScanResult>> {

        @Override
        protected ArrayList<ClientScanResult> doInBackground(Void... voids) {
            ArrayList<ClientScanResult> clients = new WifiApManager(SecondActivity.this).getClientList(false);
            return clients;
        }

        @Override
        protected void onPostExecute(ArrayList<ClientScanResult> clients) {
            super.onPostExecute(clients);
            textView1.append("Clients: \n");
            for (ClientScanResult clientScanResult : clients) {
                textView1.append("####################\n");
                textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                textView1.append("Device: " + clientScanResult.getDevice() + "\n");
                textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                textView1.append("isReachable: " + clientScanResult.isReachable() + "\n");
            }
        }
    }

}
