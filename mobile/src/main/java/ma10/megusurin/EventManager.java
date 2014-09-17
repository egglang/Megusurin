package ma10.megusurin;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class EventManager extends Fragment {

    private static final String TAG = EventManager.class.getSimpleName();

    public interface EventManagerListener {
        void addTargetFragment(Fragment f, int containerId, String tag);

        void removeTargetFragment(String tag);

        Fragment getTargetFragment(String tag);
    }

    private EventManagerListener mEventListener;

    public static final int EVENT_INIT = 0;

    public static final int EVENT_WAIT_MAGIC = 1;

    public static final int EVENT_DO_MAGIC = 2;

    public static final int EVENT_DAMAGED_ENEMY = 3;

    public static final int EVENT_ATTACK_ENEMY = 4;

    public static final int EVENT_DAMAGED_MINE = 5;

    public static final int EVENT_WEAK_ENEMY = 6;

    public static final int EVENT_DIED_ENEMY = 7;

    public static final int EVENT_FINISHED = 99;

    private static final String TAG_MESSAGE_VIEW = "message_view";

    private static final String TAG_ENEMY_VIEW = "enemy_view";

    private static final String TAG_MAGIC_VIEW = "magic_view";

    private static final int REQUEST_MAGIC = 1;

    private static final int REQUEST_ENEMY = 2;

    private static final int REQUEST_MESSAGE = 3;

    private MessageViewFragment mMessageView;

    private EnemyViewFragment mEnemyView;

    private boolean mAttackMode = false;

    private Handler mHandler;

    public interface IEventListener {
        void doEvent(final int eventId);
    }

    private List<IEventListener> mListenerList = new ArrayList<IEventListener>();

    public void registerEventListener(IEventListener l) {
        if (!mListenerList.contains(l)) {
            mListenerList.add(l);
        }
    }

    public void unregisterEventListener(IEventListener l) {
        if (mListenerList.contains(l)) {
            mListenerList.remove(l);
        }
    }

    private int mMagicCount;

    private int mCurrentEventId;

    private boolean mEnemyAttackAble = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof EventManagerListener) {
            mEventListener = (EventManagerListener) activity;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initEvent();
        mCurrentEventId = EVENT_INIT;
    }

    @Override
    public void onStart() {
        super.onStart();

        mCurrentEventId = EVENT_WAIT_MAGIC;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEventListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_MAGIC: {
                    int event = data.getIntExtra(MagicViewFragment.INTENT_DATA_EVENT, 0);
                    String magicText = data.getStringExtra(MagicViewFragment.INTENT_DATA_MAGIC_TEXT);

                    switch (event) {
                        case MagicViewFragment.EVENT_START_MAGIC:
                            showDoMagicMessage(magicText);
                            break;

                        case MagicViewFragment.EVENT_FINISH_MAGIC:
                            mEventListener.removeTargetFragment(TAG_MAGIC_VIEW);

                            if (mAttackMode) {
                                dispatchNextEvent();
                                break;
                            }

                            if (mMagicCount < 2) {
                                mEnemyAttackAble = false;
                                onFinishedEvent();
                            }
                            else if (mMagicCount == 2) {
                                mEnemyAttackAble = true;
                                onFinishedEvent();
                            }
                            else if (mMagicCount < 3) {
                                mEnemyAttackAble = false;
                                onFinishedEvent();
                            }
                            else {
                                mCurrentEventId = EVENT_WEAK_ENEMY;
                                dispatchNextEvent();
                            }
                            break;

                        case MagicViewFragment.EVENT_FINISH_SPECIAL_MAGIC:
                            mEventListener.removeTargetFragment(TAG_MAGIC_VIEW);

                            mCurrentEventId = EVENT_DIED_ENEMY;
                            dispatchNextEvent();
                            break;
                    }
                    break;

                }

                case REQUEST_ENEMY: {
                    int event = data.getIntExtra(EnemyViewFragment.INTENT_DATA_EVENT, 0);
                    String enemyName = data.getStringExtra(EnemyViewFragment.INTENT_DATA_ENEMY_NAME);

                    switch (event) {
                        case EnemyViewFragment.EVENT_ENCOUNTED:
                            showEnemyEncountMessage(enemyName);
                            break;

                        case EnemyViewFragment.EVENT_DAMAGED:
                            showEnemyDamagedMessage();
                            if (mEnemyAttackAble) {
                                onFinishedEvent();
                            } else {
                                mCurrentEventId = EVENT_WAIT_MAGIC;
                                dispatchNextEvent();
                            }
                            break;

                        case EnemyViewFragment.EVENT_PREPAREATTACK:
                            showEnemyAttackMessage(enemyName);
                            break;

                        case EnemyViewFragment.EVENT_ATTACKED:
                            onFinishedEvent();
                            break;

                        case EnemyViewFragment.EVENT_WEAK:
                            showEnemyWeakedMessage();
                            doSpecialMagicEvent();
                            break;

                        case EnemyViewFragment.EVENT_DIED:
                            onFinishedEvent();
                            break;
                    }

                }
                    break;

                case REQUEST_MESSAGE:
                    showMagicWaitMessage();
                    onFinishedEvent();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onFinishedEvent() {

        switch (mCurrentEventId) {
            case EVENT_INIT:
                mCurrentEventId = EVENT_WAIT_MAGIC;
                break;
            case EVENT_WAIT_MAGIC:
                mCurrentEventId = EVENT_DO_MAGIC;
                break;
            case EVENT_DO_MAGIC:
                mCurrentEventId = EVENT_DAMAGED_ENEMY;
                break;
            case EVENT_DAMAGED_ENEMY:
                mCurrentEventId = EVENT_ATTACK_ENEMY;
                break;
            case EVENT_ATTACK_ENEMY:
                mCurrentEventId = EVENT_DAMAGED_MINE;
                break;
            case EVENT_DAMAGED_MINE:
                mCurrentEventId = EVENT_DO_MAGIC;
                break;
            case EVENT_WEAK_ENEMY:
                mCurrentEventId = EVENT_DIED_ENEMY;
                break;
            case EVENT_DIED_ENEMY:
                mCurrentEventId = EVENT_FINISHED;
                showEventFinishMessage();
                break;
        }

        dispatchNextEvent();
    }

    private void dispatchNextEvent() {
        for (IEventListener listener : mListenerList) {
            listener.doEvent(mCurrentEventId);
        }
    }

    public void initEvent() {
        mCurrentEventId = EVENT_INIT;

        if (mEventListener.getTargetFragment(TAG_MESSAGE_VIEW) == null) {
            mMessageView = MessageViewFragment.newInstance();
            mMessageView.setTargetFragment(this, REQUEST_MESSAGE);
            mEventListener.addTargetFragment(mMessageView, R.id.message_view_holder, TAG_MESSAGE_VIEW);
            registerEventListener(mMessageView);
        }

        if (mEventListener.getTargetFragment(TAG_ENEMY_VIEW) == null) {
            mEnemyView = EnemyViewFragment.newInstance(0, true);
            mEnemyView.setTargetFragment(this, REQUEST_ENEMY);
            mEventListener.addTargetFragment(mEnemyView, R.id.enemy_view_holder, TAG_ENEMY_VIEW);
            registerEventListener(mEnemyView);
        }
    }

    public void doMagicEvent(final int magicType) {
        if (mEventListener.getTargetFragment(TAG_MAGIC_VIEW) != null) {
            Log.d(TAG, "Magic is already doing.");
            return;
        }

        mCurrentEventId = EVENT_DO_MAGIC;

        if (magicType != MagicViewFragment.MAGIC_TYPE_CARE) {
            mMagicCount++;
        }

        MagicViewFragment f = MagicViewFragment.newInstance(magicType, true);
        f.setTargetFragment(this, REQUEST_MAGIC);
        mEventListener.addTargetFragment(f, R.id.content_holder, TAG_MAGIC_VIEW);
    }

    private void doSpecialMagicEvent() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MagicViewFragment f = MagicViewFragment.newInstance(MagicViewFragment.MAGIC_TYPE_SPECIAL, true);
                f.setTargetFragment(EventManager.this, REQUEST_MAGIC);
                mEventListener.addTargetFragment(f, R.id.content_holder, TAG_MAGIC_VIEW);
            }
        }, 3000);
    }

    private void showMagicWaitMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wearから 魔法を使って 攻撃だ！");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
    }

    private void showEnemyEncountMessage(String enemyName) {
        StringBuilder sb = new StringBuilder();
        sb.append(enemyName + " が あらわれた！");
        sb.append("\n");
        sb.append("Wearから 魔法を使って 攻撃だ！");
        mMessageView.setMessage(sb.toString());
    }

    private void showDoMagicMessage(String magicText) {
        StringBuilder sb = new StringBuilder();
        sb.append("魔法を唱えた！");
        sb.append("\n");
        sb.append("<< " + magicText + " >>");
        mMessageView.setMessage(sb.toString());
    }

    private void showEnemyDamagedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("こうかは ばつぐんだ");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
    }

    private void showEnemyAttackMessage(String enemyName) {
        StringBuilder sb = new StringBuilder();
        sb.append(enemyName + " の 攻撃！");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
    }

    private void showEnemyWeakedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("今ならつかまえられそうだ!!");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
    }

    private void showEventFinishMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("You Win!!");
        sb.append("\n");
        mMessageView.setMessage(sb.toString());
    }
}
