package com.msr.dnsdemo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.msr.dnsdemo.R;
import com.msr.dnsdemo.dns.AbstractDiscovery;
import com.msr.dnsdemo.dns.DefaultDiscovery;
import com.msr.dnsdemo.dns.DnsDiscovery;
import com.msr.dnsdemo.network.HostBean;
import com.msr.dnsdemo.network.NetInfo;
import com.msr.dnsdemo.utils.Db;
import com.msr.dnsdemo.utils.DbUpdate;
import com.msr.dnsdemo.utils.Prefs;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String PKG = "com.msr.dnsdemo";
    public final static long VIBRATE = (long) 250;
    public final static int SCAN_PORT_RESULT = 1;
    public static final int MENU_SCAN_SINGLE = 0;
    public static final int MENU_OPTIONS = 1;
    public static final int MENU_HELP = 2;
    private static final int MENU_EXPORT = 3;
    private static LayoutInflater mInflater;
    public final String TAG = "ActivityDiscovery";
    private int currentNetwork = 0;
    private long network_ip = 0;
    private long network_start = 0;
    private long network_end = 0;
    private List<HostBean> hosts = null;
    //private HostsAdapter adapter;
    private AbstractDiscovery mDiscoveryTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phase2(ctxt);
    }

    public void makeToast(int msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Discover hosts
     */
    private void startDiscovering() {
        int method = 0;
        try {
            method = Integer.parseInt(prefs.getString(Prefs.KEY_METHOD_DISCOVER,
                    Prefs.DEFAULT_METHOD_DISCOVER));
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        switch (method) {
            case 1:
                mDiscoveryTask = new DnsDiscovery(MainActivity.this);
                break;
            case 2:
                // Root
                break;
            case 0:
            default:
                mDiscoveryTask = new DefaultDiscovery(MainActivity.this);
        }
        mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
        mDiscoveryTask.execute();
       /* btn_discover.setText(R.string.btn_discover_cancel);
        setButton(btn_discover, R.drawable.cancel, false);*/

        makeToast(R.string.discover_start);
        //  setProgressBarVisibility(true);
        // setProgressBarIndeterminateVisibility(true);
        initList();
    }

    public void stopDiscovering() {
        Log.e(TAG, "stopDiscovering()");
        mDiscoveryTask = null;
        // setButtonOn(btn_discover, R.drawable.discover);

        //btn_discover.setText(R.string.btn_discover);
    }

    private void initList() {
        // setSelectedHosts(false);
        //  adapter.clear();
        hosts = new ArrayList<HostBean>();
    }

    public void addHost(HostBean host) {
        host.position = hosts.size();
        hosts.add(host);
        //   adapter.add(null);
    }

    protected void setInfo() {
       /* // Info
        ((TextView) findViewById(R.id.info_ip)).setText(info_ip_str);
        ((TextView) findViewById(R.id.info_in)).setText(info_in_str);
        ((TextView) findViewById(R.id.info_mo)).setText(info_mo_str);*/

        // Scan button state
        if (mDiscoveryTask != null) {
            //  setButton(btn_discover, R.drawable.cancel, false);
            //btn_discover.setText(R.string.btn_discover_cancel);
          /*  btn_discover.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cancelTasks();
                }
            });*/
        }

        if (currentNetwork != net.hashCode()) {
            Log.i(TAG, "Network info has changed");
            currentNetwork = net.hashCode();

            // Cancel running tasks
            cancelTasks();
        } else {
            return;
        }

        // Get ip information
        network_ip = NetInfo.getUnsignedLongFromIp(net.ip);
        if (prefs.getBoolean(Prefs.KEY_IP_CUSTOM, Prefs.DEFAULT_IP_CUSTOM)) {
            // Custom IP
            network_start = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_START,
                    Prefs.DEFAULT_IP_START));
            network_end = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_END,
                    Prefs.DEFAULT_IP_END));
        } else {
            // Custom CIDR
            if (prefs.getBoolean(Prefs.KEY_CIDR_CUSTOM, Prefs.DEFAULT_CIDR_CUSTOM)) {
                net.cidr = Integer.parseInt(prefs.getString(Prefs.KEY_CIDR, Prefs.DEFAULT_CIDR));
            }
            // Detected IP
            int shift = (32 - net.cidr);
            if (net.cidr < 31) {
                network_start = (network_ip >> shift << shift) + 1;
                network_end = (network_start | ((1 << shift) - 1)) - 1;
            } else {
                network_start = (network_ip >> shift << shift);
                network_end = (network_start | ((1 << shift) - 1));
            }
            // Reset ip start-end (is it really convenient ?)
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(Prefs.KEY_IP_START, NetInfo.getIpFromLongUnsigned(network_start));
            edit.putString(Prefs.KEY_IP_END, NetInfo.getIpFromLongUnsigned(network_end));
            edit.commit();
        }
    }

    protected void setButtons(boolean disable) {
        /*if (disable) {
            setButtonOff(btn_discover, R.drawable.disabled);
        } else {
            setButtonOn(btn_discover, R.drawable.discover);
        }*/
    }

    protected void cancelTasks() {
        if (mDiscoveryTask != null) {
            mDiscoveryTask.cancel(true);
            mDiscoveryTask = null;
        }
    }

    @Override
    public void onClick(View view) {
        startDiscovering();
        // startActivity(new Intent(this, SecondActivity.class));
        //   startActivity(new Intent(this, ThirdActivity.class));
    }


    private void phase2(final Context ctxt) {

        class DbUpdateProbes extends DbUpdate {
            public DbUpdateProbes() {
                super(MainActivity.this, Db.DB_PROBES, "probes", "regex", 298);
            }

            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                final Activity d = mActivity.get();
                //  getToast(MainActivity.this, "Phase2");
                phase3(d);
            }

            protected void onCancelled() {
                super.onCancelled();
                final Activity d = mActivity.get();
                //     getToast(MainActivity.this, "Phase3:2");
                phase3(d);
            }
        }

        class DbUpdateNic extends DbUpdate {
            public DbUpdateNic() {
                super(MainActivity.this, Db.DB_NIC, "oui", "mac", 253);
            }

            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                // getToast(MainActivity.this, "DBUpdateNic");
                final Activity d = mActivity.get();
                new DbUpdateProbes();
            }

            protected void onCancelled() {
                super.onCancelled();
                final Activity d = mActivity.get();
                new DbUpdateProbes();
            }
        }

        // CheckNicDb
        try {
            if (prefs.getInt(Prefs.KEY_RESET_NICDB, Prefs.DEFAULT_RESET_NICDB) != getPackageManager()
                    .getPackageInfo(PKG, 0).versionCode) {
                new DbUpdateNic();
            } else {
                // There is a NIC Db installed
                phase3(ctxt);
            }
        } catch (PackageManager.NameNotFoundException e) {
            phase3(ctxt);
        } catch (ClassCastException e) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(Prefs.KEY_RESET_NICDB, 1);
            edit.commit();
            phase3(ctxt);
        }
    }

    private void phase3(final Context ctxt) {
        // Install Services DB
        // getToast(MainActivity.this, "Phase3");
        //     getToast(MainActivity.this, "V:" + prefs.getInt(Prefs.KEY_RESET_SERVICESDB, Prefs.DEFAULT_RESET_SERVICESDB));
      /*  try {
            getToast(MainActivity.this, "V::" + getPackageManager().getPackageInfo(PKG, 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
        try {
            if (prefs.getInt(Prefs.KEY_RESET_SERVICESDB, Prefs.DEFAULT_RESET_SERVICESDB) != getPackageManager()
                    .getPackageInfo(PKG, 0).versionCode) {
                new CreateServicesDb(MainActivity.this).execute();
            }
        } catch (PackageManager.NameNotFoundException e) {
            // startDiscoverActivity(ctxt);
        }
    }

    class CreateServicesDb extends AsyncTask<Void, String, Void> {
        private WeakReference<Activity> mActivity;
        private ProgressDialog progress;

        public CreateServicesDb(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        protected void onPreExecute() {
            final Activity d = mActivity.get();
            if (d != null) {
                try {
                    progress = ProgressDialog.show(d, "", d.getString(R.string.task_services));
                } catch (Exception e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Activity d = mActivity.get();
            if (d != null) {
                Db db = new Db(d.getApplicationContext());
                try {
                    // db.copyDbToDevice(R.raw.probes, Db.DB_PROBES);
                    db.copyDbToDevice(R.raw.services, Db.DB_SERVICES);
                    db.copyDbToDevice(R.raw.saves, Db.DB_SAVES);
                    // Save this device in db
                    NetInfo net = new NetInfo(d.getApplicationContext());
                    ContentValues values = new ContentValues();
                    values.put("_id", 0);
                    if (net.macAddress == null) {
                        net.macAddress = NetInfo.NOMAC;
                    }
                    values.put("mac", net.macAddress.replace(":", "").toUpperCase());
                    values.put("name", "MyPhone");
                    SQLiteDatabase data = Db.openDb(Db.DB_SAVES);
                    data.insert("nic", null, values);
                    data.close();
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    if (e != null) {
                        if (e.getMessage() != null) {
                            Log.e(TAG, e.getMessage());
                        } else {
                            Log.e(TAG, "Unknown IOException");
                        }
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            final MainActivity d = (MainActivity) mActivity.get();
            if (d != null) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                try {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putInt(Prefs.KEY_RESET_SERVICESDB, d.getPackageManager().getPackageInfo(
                            PKG, 0).versionCode);
                    edit.commit();
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    // d.startDiscoverActivity(d);
                }
            }
        }
    }
}
