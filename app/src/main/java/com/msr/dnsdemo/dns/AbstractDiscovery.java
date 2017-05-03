package com.msr.dnsdemo.dns;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.msr.dnsdemo.R;
import com.msr.dnsdemo.activities.MainActivity;
import com.msr.dnsdemo.network.HostBean;
import com.msr.dnsdemo.utils.Prefs;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;


public abstract class AbstractDiscovery extends AsyncTask<Void, HostBean, Void> {

    //private final String TAG = "AbstractDiscovery";

    final protected WeakReference<MainActivity> mDiscover;
    protected int hosts_done = 0;
    protected long ip;
    protected long start = 0;
    protected long end = 0;
    protected long size = 0;

    public AbstractDiscovery(MainActivity discover) {
        mDiscover = new WeakReference<MainActivity>(discover);
    }

    public void setNetwork(long ip, long start, long end) {
        this.ip = ip;
        this.start = start;
        this.end = end;
    }

    abstract protected Void doInBackground(Void... params);

    @Override
    protected void onPreExecute() {
        size = (int) (end - start + 1);
        if (mDiscover != null) {
            final MainActivity discover = mDiscover.get();
            if (discover != null) {
                discover.setProgress(0);
            }
        }
    }

    @Override
    protected void onProgressUpdate(HostBean... host) {
        if (mDiscover != null) {
            final MainActivity discover = mDiscover.get();
            if (discover != null) {
                if (!isCancelled()) {
                    if (host[0] != null) {
                        Toast.makeText(mDiscover.get(), "Found::" + host[0].ipAddress, Toast.LENGTH_SHORT).show();
                        InetAddress address = null;
                        try {
                            Log.i("===Rest", "===" + host[0].ipAddress);
                            address = InetAddress.getByName(host[0].ipAddress);
                            //  Log.i("===Address","==="+address);
                           /* try {
                                boolean reachable = address.isReachable(1000);
                                String hostName = address.getCanonicalHostName();
                                Toast.makeText(mDiscover.get(),"Name:"+hostName,Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/

                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }

                        discover.addHost(host[0]);
                    }
                    if (size > 0) {
                        discover.setProgress((int) (hosts_done * 10000 / size));
                    }
                }

            }
        }
    }

    @Override
    protected void onPostExecute(Void unused) {
        if (mDiscover != null) {
            final MainActivity discover = mDiscover.get();
            if (discover != null) {
                if (discover.prefs.getBoolean(Prefs.KEY_VIBRATE_FINISH,
                        Prefs.DEFAULT_VIBRATE_FINISH) == true) {
                    Vibrator v = (Vibrator) discover.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(MainActivity.VIBRATE);
                }
                discover.makeToast(R.string.discover_finished);
                discover.stopDiscovering();
            }
        }
    }

    @Override
    protected void onCancelled() {
        if (mDiscover != null) {
            final MainActivity discover = mDiscover.get();
            if (discover != null) {
                discover.makeToast(R.string.discover_canceled);
                discover.stopDiscovering();
            }
        }
        super.onCancelled();
    }
}
