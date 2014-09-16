package ma10.megusurin;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        NodeApi.NodeListener, MagicViewFragment.OnMagicEffectListener,
        EnemyViewFragment.OnEnemyEventListener, MessageViewFragment.OnMessageListener {


    private static final String TAG = "Megusurin";
    private static final String PATH_FIRE = "/fire";
    private static final String PATH_THUNDER = "/thunder";
    private static final String PATH_START_APP = "/start_app";
    private static final String PATH_STOP_APP = "/stop_app";

    private static final String MAGIC_FRAGMENT_TAG = "MAGIC_VIEW";
    private static final String CAMERA_FRAGMENT_TAG = "CAMERA_VIEW";
    private static final String ENEMY_FRAGMENT_TAG = "ENEMY_VIEW";
    private static final String MESSAGE_FRAGMENT_TAG = "MESSAGE_VIEW";

    private GoogleApiClient mGoogleApiClient;

    private ToggleButton mTogglePreview;

    private boolean mPreviewMode;

    private ViewGroup mBackGround;

    private EnemyViewFragment mEnemyView;

    private MessageViewFragment mMessageView;

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

        showMessageView();
        showEnemyView();
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
                    removeTargetFragment(MAGIC_FRAGMENT_TAG);
                    Fragment f = MagicViewFragment.newInstance(magicType, true);
                    showTargetFragment(f, R.id.content_holder, MAGIC_FRAGMENT_TAG);

                    mTogglePreview.setVisibility(View.INVISIBLE);
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

    @Override
    public void onStartMagic(String magic) {
        StringBuilder sb = new StringBuilder();
        sb.append("魔法をとなえた!!");
        sb.append("\n");
        sb.append("<<" + magic + ">>");
        mMessageView.setMessage(sb.toString());
    }

    @Override
    public void onFinishedMagic() {
        mTogglePreview.setVisibility(View.VISIBLE);
        removeTargetFragment(MAGIC_FRAGMENT_TAG);

        mEnemyView.onEnemyDamaged();
    }

    private void showMessageView() {
        FragmentManager fm = getFragmentManager();
        Fragment f = fm.findFragmentByTag(MESSAGE_FRAGMENT_TAG);
        if (f == null) {
            mMessageView = new MessageViewFragment();
            showTargetFragment(mMessageView, R.id.message_view_holder, MESSAGE_FRAGMENT_TAG);
        }
    }

    private void showEnemyView() {
        FragmentManager fm = getFragmentManager();
        Fragment f = fm.findFragmentByTag(ENEMY_FRAGMENT_TAG);
        if (f == null) {
            mEnemyView = EnemyViewFragment.newInstance(0, true);
            showTargetFragment(mEnemyView, R.id.enemy_view_holder, ENEMY_FRAGMENT_TAG);
        }
    }

    private CompoundButton.OnCheckedChangeListener mOnPreviewToggleChangedListener
            = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mPreviewMode = isChecked;
            if (mPreviewMode) {
                mBackGround.setBackgroundColor(Color.TRANSPARENT);
                showTargetFragment(new CameraViewFragment(), R.id.camera_view, CAMERA_FRAGMENT_TAG);
            } else {
                mBackGround.setBackgroundColor(Color.BLACK);
                removeTargetFragment(CAMERA_FRAGMENT_TAG);
            }
        }
    };

    private void showTargetFragment(Fragment f, int holderId, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(holderId, f, tag);
        ft.commit();
    }

    private void removeTargetFragment(String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment oldFragment = fm.findFragmentByTag(tag);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.commit();
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

    @Override
    public void onEnemyEncounted(String enemyName) {
        StringBuilder sb = new StringBuilder();
        sb.append(enemyName + " が あらわれた！");
        sb.append("\n");
        sb.append("Wearから 魔法を使って 攻撃だ！");
        mMessageView.setMessage(sb.toString());

    }

    @Override
    public void onEnemyDied() {

    }

    @Override
    public void onEnemyDamaged() {
        StringBuilder sb = new StringBuilder();
        sb.append("こうかは ばつぐんだ");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
        mEnemyView.onEnemyAttack();
    }

    @Override
    public void onEnemyPrepareAttack(final String enemyName) {
        StringBuilder sb = new StringBuilder();
        sb.append(enemyName + " の 攻撃！");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
    }

    @Override
    public void onEnemyAttacked() {
        mMessageView.showDamageEffect();
    }

    @Override
    public void onFinishDamageEffect() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wearから 魔法を使って 攻撃だ！");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
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
