package ma10.megusurin.lib.view.moverio.moverio;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.ViewGroup;
import android.view.WindowManager;

import ma10.megusurin.lib.view.EventManager;
import ma10.megusurin.lib.web.YodaAPIAccesser;

public class MegusurinActivity extends Activity
        implements EventManager.EventManagerListener {

    private static final String TAG = MegusurinActivity.class.getSimpleName();

    private static final String EVENT_FRAGMENT_TAG = "EVENT_MG";

    private ViewGroup mBackGround;

    private EventManager mEventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= 0x80000000;

        setContentView(R.layout.activity_megusurin);

        setupEventManager();
    }

    private void setupEventManager() {
        mEventManager = new EventManager();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mEventManager, EVENT_FRAGMENT_TAG);
        ft.commit();

        new EncountCheckTask().execute();
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

    private boolean mPollingFlag = true;

    private class EncountCheckTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... mVoids) {

            YodaAPIAccesser yodaAccesser = new YodaAPIAccesser(true);

            boolean occured = false;
            while ((occured == false) && mPollingFlag) {
                occured = yodaAccesser.isOccurEncount();

                try {
                    Thread.sleep(100);
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
                magicType = yodaAPIAccesser.getMagicType();

                try {
                    Thread.sleep(100);
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
