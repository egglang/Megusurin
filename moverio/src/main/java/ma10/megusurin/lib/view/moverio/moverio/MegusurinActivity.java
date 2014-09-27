package ma10.megusurin.lib.view.moverio.moverio;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.List;

import jp.epson.moverio.bt200.SensorControl;
import ma10.megusurin.lib.view.EventManager;
import ma10.megusurin.lib.view.MagicViewFragment;
import ma10.megusurin.lib.web.YodaAPIAccesser;

public class MegusurinActivity extends Activity
        implements EventManager.EventManagerListener, SensorEventListener {

    public static final String KEY_MODE_TRAINING = "mode_training";

    private static final String TAG = MegusurinActivity.class.getSimpleName();

    private static final String EVENT_FRAGMENT_TAG = "EVENT_MG";

    private boolean mTrainingMode;

    private ViewGroup mBackGround;

    private EventManager mEventManager;

    private SensorManager mSensorManager;

    private boolean mEnableMagicAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= 0x80000000;

        setContentView(R.layout.activity_megusurin);

        // Change to sensor of handset
        SensorControl sensorControl = new SensorControl(this);
        sensorControl.setMode(SensorControl.SENSOR_MODE_HEADSET);

        if (getIntent() != null) {
            mTrainingMode = getIntent().getBooleanExtra(KEY_MODE_TRAINING, false);
        }

        setupEventManager();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    private void setupEventManager() {
        mEventManager = EventManager.newInstance(mTrainingMode);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mEventManager, EVENT_FRAGMENT_TAG);
        ft.commit();

        new EncountCheckTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        mSensorManager.unregisterListener(this);
    }

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
        Log.d(TAG, "onWaitMagic()");
        mEnableMagicAction = true;
    }

    @Override
    public void onPendingMagic() {
        Log.d(TAG, "onPendingMagic()");
        mEnableMagicAction = false;
    }

    private boolean mPollingFlag = true;

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

//            StringBuilder sb = new StringBuilder();
//            sb.append("Azi : " + mAzimuth + ", Pitch : " + mPitch + " , Roll : " + mRoll);
//            Log.d(TAG, sb.toString());

            if (mEnableMagicAction) {
                if ((mPitch > -40) && (mPitch < 15)) {
                    Log.d(TAG, "目薬 ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    mEventManager.doMagicEvent(MagicViewFragment.MAGIC_TYPE_CURE);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor mSensor, int i) {
    }

    private class EncountCheckTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... mVoids) {

            YodaAPIAccesser yodaAccesser = new YodaAPIAccesser(true);

            boolean occured = false;
            while ((occured == false) && mPollingFlag) {
                occured = yodaAccesser.isOccurEncount();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return occured;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    startBattleEvent(0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startBattleEvent(int enemyType) throws RemoteException {
        mEventManager.encounterEnemy(enemyType);
        new MagicCheckTask().execute();
    }

    private class MagicCheckTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... mVoids) {

            YodaAPIAccesser yodaAPIAccesser = new YodaAPIAccesser(false);

            int magicType = -1;

            while ((magicType == -1) && mPollingFlag) {
                if (mEnableMagicAction) {
                    magicType = yodaAPIAccesser.getMagicType();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return magicType;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != -1) {
                doMagic(result);
            }
        }
    }

    private void doMagic(final int magicType) {
        mEventManager.doMagicEvent(magicType);
        new MagicCheckTask().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mPollingFlag = false;
    }
}
