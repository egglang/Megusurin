package ma10.megusurin;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.app.Fragment;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

public class MegusurinActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener, EventManager.EventManagerListener {


    private static final String TAG = "Megusurin";
    private static final String PATH_FIRE = "/fire";
    private static final String PATH_THUNDER = "/thunder";
    private static final String PATH_START_APP = "/start_app";
    private static final String PATH_STOP_APP = "/stop_app";

    private static final String EVENT_FRAGMENT_TAG = "EVENT_MG";
    private static final String CAMERA_FRAGMENT_TAG = "CAMERA_VIEW";

    private GoogleApiClient mGoogleApiClient;

    private ToggleButton mTogglePreview;

    private boolean mPreviewMode;

    private ViewGroup mBackGround;

    private EventManager mEventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_megusurin);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mBackGround = (ViewGroup) findViewById(R.id.main_back);

        mTogglePreview = (ToggleButton) findViewById(R.id.preview_toggle);
        mTogglePreview.setOnCheckedChangeListener(mOnPreviewToggleChangedListener);

        setupEventManager();
    }

    private void setupEventManager() {
        mEventManager = new EventManager();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mEventManager, EVENT_FRAGMENT_TAG);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopWearApp();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");

        addWearListener();
        startWearApp();
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
                int magicType = -1;
                if (PATH_FIRE.equals(path)) {
                    magicType = MagicViewFragment.MAGIC_TYPE_FIRE;
                } else if (PATH_THUNDER.equals(path)) {
                    magicType = MagicViewFragment.MAGIC_TYPE_THUNDER;
                } else {
                    Log.d(TAG, "Unknown path: " + path);
                }

                if (magicType != -1) {
                    mEventManager.doMagicEvent(magicType);
                }
            }
        });
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

    private CompoundButton.OnCheckedChangeListener mOnPreviewToggleChangedListener
            = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mPreviewMode = isChecked;
            if (mPreviewMode) {
                mBackGround.setBackgroundColor(Color.TRANSPARENT);
                addTargetFragment(new CameraViewFragment(), R.id.camera_view, CAMERA_FRAGMENT_TAG);
            } else {
                mBackGround.setBackgroundColor(Color.BLACK);
                removeTargetFragment(CAMERA_FRAGMENT_TAG);
            }
        }
    };

    @Override
    public void addTargetFragment(Fragment f, int containerId, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(containerId, f, tag);
        ft.commit();
    }

    @Override
    public void removeTargetFragment(String tag) {
        FragmentManager fm = getFragmentManager();
        Fragment targetFragment = fm.findFragmentByTag(tag);
        if (targetFragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(targetFragment);
            ft.commit();
        }
    }

    @Override
    public Fragment getTargetFragment(String tag) {
        FragmentManager fm = getFragmentManager();
        Fragment targetFragment = fm.findFragmentByTag(tag);
        return targetFragment;
    }

    private void addWearListener() {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    private void removeWearListener() {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    private void startWearApp() {
        new Task(PATH_START_APP).execute();
    }

    private void stopWearApp() {
        new Task(PATH_STOP_APP).execute();
    }

    private void sendMessage(String node, String path) {
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, path, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private class Task extends AsyncTask<Void, Void, Void> {

        private final String path;
        private Task(String path) {
            this.path = path;
        }
        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendMessage(node, path);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (path.equals(PATH_STOP_APP)) {
                removeWearListener();
            }
        }
    }

}
