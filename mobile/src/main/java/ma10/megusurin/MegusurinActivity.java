package ma10.megusurin;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.util.List;

import ma10.megusurin.lib.view.EventManager;
import ma10.megusurin.lib.view.MagicViewFragment;
import ma10.megusurin.lib.web.YodaAPIAccesser;
import ma10.megusurin.lib.web.MegusurinEventChecker;

public class MegusurinActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener, EventManager.EventManagerListener,
        MegusurinEventChecker.IMegusurinEventListener,
        SensorEventListener {

    public static final String INTENT_KEY_MODE = "mode";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_TEST = 1;

    public static final String INTENT_BATTLE_START = "ma10.megusurin.event.start.battle";

    private static final String TAG = "Megusurin";
    private static final String PATH_FIRE = "/fire";
    private static final String PATH_THUNDER = "/thunder";
    private static final String PATH_ICE = "/ice";
    private static final String PATH_START_APP = "/start_app";
    private static final String PATH_START_CHARGE = "/start_charge";
    private static final String PATH_STOP_APP = "/stop_app";
    private static final String PATH_SET_PARKING = "/set_parking";
    private static final String PATH_START_BATTLE = "/start_battle";

    private static final String EVENT_FRAGMENT_TAG = "EVENT_MG";
    private static final String CAMERA_FRAGMENT_TAG = "CAMERA_VIEW";

    private int mMode;

    private GoogleApiClient mGoogleApiClient;

    private ToggleButton mTogglePreview;

    private boolean mPreviewMode;

    private ViewGroup mBackGround;

    private EventManager mEventManager;

    private MegusurinEventChecker mEventChecker;

    private SensorManager mSensorManager;

    private boolean mEnableMagicAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_megusurin);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mBackGround = (ViewGroup) findViewById(R.id.main_back);
        mBackGround.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        mTogglePreview = (ToggleButton) findViewById(R.id.preview_toggle);
        mTogglePreview.setOnCheckedChangeListener(mOnPreviewToggleChangedListener);

        if (getIntent() != null) {
            mMode = getIntent().getIntExtra(INTENT_KEY_MODE, MODE_NORMAL);
        }

        mEventChecker = new MegusurinEventChecker(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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
        mEventChecker.startEventCheck();

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensors) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }
            else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!mOccuredEncountEvnet) {
            stopWearApp();
            mEventChecker.stopEventCheck();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");

        addWearListener();
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
                } else if (PATH_ICE.equals(path)) {
                    magicType = MagicViewFragment.MAGIC_TYPE_ICE;
                } else if (PATH_START_BATTLE.equals(path)) {
                    try {
                        startBattleEvent(mEnemyType);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "Unknown path: " + path);
                }

                if (magicType != -1) {
                    mEventManager.doMagicEvent(magicType);

                    new PostDoMagicTask().execute(new Integer[]{magicType});
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
                addTargetFragment(new CameraViewFragment(), R.id.camera_view, CAMERA_FRAGMENT_TAG, false);
            } else {
                mBackGround.setBackgroundColor(Color.BLACK);
                removeTargetFragment(CAMERA_FRAGMENT_TAG);
            }
        }
    };

    @Override
    public void addTargetFragment(Fragment f, int containerId, String tag, boolean isAnimation) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (isAnimation) {
            ft.setCustomAnimations(R.animator.fade_in, 0);
        }

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

    @Override
    public void onWaitMagic() {
        mEnableMagicAction = true;
    }

    @Override
    public void onPendingMagic() {
        mEnableMagicAction = false;
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

    private void startChargeDialog() {
        new Task(PATH_START_CHARGE).execute();
    }

    private void startBattleCharge() {
        new Task(PATH_SET_PARKING).execute();
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
    public void onGameStart() {
    }

    @Override
    public void onEnemyEncount() {
        mEnemyType = 0;
        mOccuredEncountEvnet = true;
        EventNotify.sendBattleEventNotify(this);
    }

    @Override
    public void onWaitBattlePrepare() {
        dispatchBattleEvent();
    }

    @Override
    public void onBattlePrepared() {
//        startBattleEvent(mEnemyType);
    }

    private static final double RAD2DEG = 180/Math.PI;

    float[] mRotationMatrix = new float[9];
    float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];
    float[] mAttitude = new float[3];

    int mAzimuth;
    int mPitch;
    int mRoll;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = sensorEvent.values.clone();
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = sensorEvent.values.clone();
        }

        if ((mGeomagnetic != null) && (mGravity != null)) {
            SensorManager.getRotationMatrix(
                    mRotationMatrix,
                    null,
                    mGravity,
                    mGeomagnetic);

            SensorManager.getOrientation(
                    mRotationMatrix,
                    mAttitude);

            mAzimuth = (int) (mAttitude[0] * RAD2DEG);
            mPitch = (int) (mAttitude[1] * RAD2DEG);
            mRoll = (int) (mAttitude[2] * RAD2DEG);

            StringBuilder sb = new StringBuilder();
            sb.append("Azi : " + mAzimuth + ", Pitch : " + mPitch + " , Roll : " + mRoll);
            Log.d(TAG, sb.toString());

            if (mEnableMagicAction) {
                if (mPitch < -80) {
                    Log.d(TAG, "目薬 ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    mEventManager.doMagicEvent(MagicViewFragment.MAGIC_TYPE_CURE);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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

    private int mEnemyType;

    private boolean mOccuredEncountEvnet = false;

    private void dispatchBattleEvent() {
        startChargeDialog();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (action.equals(INTENT_BATTLE_START)) {

            intent.setAction(Intent.ACTION_VIEW);

            EventNotify.cancelBattleEventNotify(this);

            try {
                startBattleEvent(mEnemyType);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void startBattleEvent(int enemyType) throws RemoteException {
        mEventManager.encounterEnemy(enemyType);
        mOccuredEncountEvnet = false;

        new PostOccurEncountTask().execute();
    }

    private class PostOccurEncountTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            YodaAPIAccesser yodaAPIAccesser = new YodaAPIAccesser(true);
            yodaAPIAccesser.postOccurEncount(mEnemyType);

            return null;
        }
    };

    private class PostDoMagicTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            int magicType = params[0];
            YodaAPIAccesser yodaAPIAccesser = new YodaAPIAccesser(false);
            yodaAPIAccesser.postMagicType(magicType);

            return null;
        }
    }
}
