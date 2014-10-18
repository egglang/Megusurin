package ma10.megusurin;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

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

public class ChargeActivity extends Activity
        implements DelayedConfirmationView.DelayedConfirmationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener{

    private static final String PATH_SET_PARKING = "/set_parking";
    private static final String PATH_START_BATTLE = "/start_battle";

    private static final String TAG = ChargeActivity.class.getSimpleName();

    private TextView mTextView;

    private DelayedConfirmationView mDelayedConfirmationView;

    private ViewGroup mViewRoot;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_charge);

        mDelayedConfirmationView = (DelayedConfirmationView) findViewById(R.id.chage_confirmation);
        mDelayedConfirmationView.setTotalTimeMs(5000);

        mViewRoot = (ViewGroup) findViewById(R.id.charge_view_root);
        mViewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick Delayed View");

                mDelayedConfirmationView.start();
                mDelayedConfirmationView.setListener(ChargeActivity.this);
            }
        });

        mTextView = (TextView) findViewById(R.id.charge_text);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onTimerFinished(View mView) {
        startBattleEvent();
    }

    @Override
    public void onTimerSelected(View mView) {

    }

    @Override
    public void onConnected(Bundle mBundle) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mGoogleApiClient.connect();

        vibrate();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer mDataEvents) {

    }

    @Override
    public void onMessageReceived(final MessageEvent event) {
        Log.d(TAG, "onMessageReceived: " + event);

        String path = event.getPath();
        if (path.equals(PATH_SET_PARKING)) {
            Log.d(TAG, "Set Parking!: " + event);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(R.string.charge_text_charge);
                    mDelayedConfirmationView.start();
                    mDelayedConfirmationView.setListener(ChargeActivity.this);
                }
            });
        }
    }

    @Override
    public void onPeerConnected(Node mNode) {

    }

    @Override
    public void onPeerDisconnected(Node mNode) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult mConnectionResult) {

    }

    public void startBattleEvent() {new Task(PATH_START_BATTLE).execute(); }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
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

            startBattleActivity();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {500, 500, 500, 500, 500, 500};
        vibrator.vibrate(pattern, -1);
    }

    private void startBattleActivity() {
        Intent intent = new Intent(this, WearActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }
}
