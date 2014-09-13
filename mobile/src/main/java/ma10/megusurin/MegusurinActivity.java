package ma10.megusurin;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MegusurinActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener, MagicViewFragment.OnMagicEffectListener {


    private static final String TAG = "Megusurin";
    private static final String PATH_FIRE = "/fire";
    private static final String PATH_THUNDER = "/thunder";
    private static final String MAGIC_FRAGMENT_TAG = "MAGIC_VIEW";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_megusurin);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.megusurin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    @Override
    public void onMessageReceived(final MessageEvent event) {
        Log.d(TAG, "onMessageReceived: " + event);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String path = event.getPath();
                int magicType = 0;
                if (PATH_FIRE.equals(path)) {
                    Toast.makeText(getApplicationContext(), "Fire event received.", Toast.LENGTH_SHORT).show();
                    magicType = MagicViewFragment.MAGIC_TYPE_FIRE;
                } else if (PATH_THUNDER.equals(path)) {
                    Toast.makeText(getApplicationContext(), "Thunder event received", Toast.LENGTH_SHORT).show();
                    magicType = MagicViewFragment.MAGIC_TYPE_THUNDER;
                } else {
                    Log.d(TAG, "Unknown path: " + path);
                }

                Fragment f = MagicViewFragment.newInstance(magicType);
                showTargetFragment(f);
            }
        });
    }

    private void showTargetFragment(Fragment f) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment oldFragment = fm.findFragmentByTag(MAGIC_FRAGMENT_TAG);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.add(R.id.content_holder, f, MAGIC_FRAGMENT_TAG);
        ft.commit();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);
    }
    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "Node Connected:" + node.getId());
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "Node Disconnected:" + node.getId());
    }

    @Override
    public void onFinished() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment oldFragment = fm.findFragmentByTag(MAGIC_FRAGMENT_TAG);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.commit();
    }
}
