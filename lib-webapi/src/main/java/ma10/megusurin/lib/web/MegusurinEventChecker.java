package ma10.megusurin.lib.web;

import android.os.RemoteException;
import android.util.Log;

public class MegusurinEventChecker {

    private static final String TAG = MegusurinEventChecker.class.getSimpleName();

    public interface IMegusurinEventListener {

        void onGameStart();

        void onEnemyEncount();

        void onWaitBattlePrepare();

        void onBattlePrepared();
    }

    private IMegusurinEventListener mListener;

    public MegusurinEventChecker(IMegusurinEventListener l) {
        setListener(l);
    }

    public void setListener(IMegusurinEventListener l) {
        mListener = l;
    }

    public void startEventCheck() {
        mPollingFlag = true;
        Thread checkTread = new Thread(mEventChecker);
        checkTread.start();
    }

    public void stopEventCheck() {
        mPollingFlag = false;
    }

    private boolean mPollingFlag = false;

    private Runnable mEventChecker = new Runnable() {
        @Override
        public void run() {

            CarInfoGetter carInfoGetter = new CarInfoGetter();

            int oldSysPower = CarInfo.SYS_POWER_OFF;

            boolean encountedEnemy = false;

            boolean waitForBattlePrepare = false;

            while (mPollingFlag) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                CarInfo info = carInfoGetter.getCarInfo();
                if (info != null) {
                    Log.d(TAG, "Succeeded get car info.");
                    Log.d(TAG, info.toString());

                    // check engine started
                    if (info.getSysPowerSts() != CarInfo.SYS_POWER_ON) {
                        Log.d(TAG, "Car system power is off...");
                        oldSysPower = info.getSysPowerSts();
                        continue;
                    }

                    // Game start when car engine started.
                    if (oldSysPower != CarInfo.SYS_POWER_ON) {
                        Log.d(TAG, "Car engine is start!!");
                        oldSysPower = info.getSysPowerSts();

                        if (mListener != null) {
                            mListener.onGameStart();
                        }
                        continue;
                    }

                    // judge speed(encount enemy)
                    if ((info.getSpeed() >= 10 && (encountedEnemy == false))) {
                        Log.d(TAG, "Encount Enemy!");
                        encountedEnemy = true;

                        if (mListener != null) {
                            mListener.onEnemyEncount();
                        }
                        continue;
                    }

                    // judge shift position(wait for battle prepare)
                    if (info.getGearPos().equals("P") && (encountedEnemy == true) && (waitForBattlePrepare == false)) {
                        Log.d(TAG, "Wait for battle prepare!");
                        waitForBattlePrepare = true;

                        if (mListener != null) {
                            mListener.onWaitBattlePrepare();
                        }
                        continue;
                    }

                    // judge parking break event(battle prepared)
                    if (info.isParkingBrkOn() && (encountedEnemy == true) && (waitForBattlePrepare == true)) {
                        Log.d(TAG, "Prepared Battle!");
                        mPollingFlag = false;

                        if (mListener != null) {
                            mListener.onBattlePrepared();
                        }
                    }
                }
            }
        }
    };
}
