package ma10.megusurin;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;


/**
 * Message and Status View Fragment
 *
 */
public class MessageViewFragment extends Fragment implements EventManager.IEventListener{

    public static final String INTENT_DATA_EVENT = "message_event";

    public static final int EVENT_DAMAGED = 0;

    public static final int EVENT_CURED = 1;

    private static final int MAX_HITPOINT = 387;

    private static final int MIN_HITPOINT = 76;

    private static final int LV = 32;

    public static MessageViewFragment newInstance() {
        MessageViewFragment f = new MessageViewFragment();

        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    public MessageViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        mHp = MAX_HITPOINT;
    }

    private Handler mHandler;

    private ViewGroup mViewRoot;

    private TextView mTextHp;

    private TextView mTextLv;

    private ImageView mImageChar;

    private TextView mTextMessage;

    private int mHp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message_view, container, false);

        mViewRoot = (ViewGroup) v.findViewById(R.id.message_root);

        mImageChar = (ImageView) v.findViewById(R.id.message_image_char);
        mImageChar.setImageResource(R.drawable.char_claudia);

        mTextHp = (TextView) v.findViewById(R.id.message_text_hp);
        mTextHp.setText(String.valueOf(MAX_HITPOINT));

        mTextLv = (TextView) v.findViewById(R.id.message_text_lv);
        mTextLv.setText(String.valueOf(LV));

        mTextMessage = (TextView) v.findViewById(R.id.message_text_msg);

        return v;
    }

    public void setMessage(String message) {
        mTextMessage.setText(message);
    }

    public void showDamageEffect() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 30, 0, 0);
        translateAnimation.setDuration(100);
        translateAnimation.setRepeatCount(4);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.post(mDecrimentHitPoint);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mViewRoot.startAnimation(translateAnimation);
    }

    private Runnable mDecrimentHitPoint = new Runnable() {
        @Override
        public void run() {
            if ((mHp - 3) > MIN_HITPOINT) {
                mHp -= 3;
                mHandler.postDelayed(mDecrimentHitPoint, 20);
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dispatchEventFinished(EVENT_DAMAGED);
                    }
                }, 1000);
            }

            final String newHP = String.valueOf(mHp);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextHp.setText(newHP);
                }
            });
        }
    };

    private void showCuredEffect() {
        mHandler.post(mIncremintHitPoint);
    }

    private Runnable mIncremintHitPoint = new Runnable() {
        @Override
        public void run() {
            if ((mHp + 3) <= MAX_HITPOINT) {
                mHp += 3;
                mHandler.postDelayed(mIncremintHitPoint, 20);
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dispatchEventFinished(EVENT_CURED);
                    }
                }, 1000);
            }

            final String newHP = String.valueOf(mHp);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextHp.setText(newHP);
                }
            });
        }
    };

    @Override
    public void doEvent(int eventId) {
        if (eventId == EventManager.EVENT_DAMAGED_MINE) {
            showDamageEffect();
        }
        else if (eventId == EventManager.EVENT_WAIT_MAGIC) {
            showWaitMagicMessage();
        }
        else if (eventId == EventManager.EVENT_CURED_MINE) {
            showCuredEffect();
        }
    }

    private void showWaitMagicMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wearから 魔法を使って 攻撃だ！");
        sb.append("\n");
        setMessage(sb.toString());
    }

    private void dispatchEventFinished(final int event) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent intent = new Intent();
            intent.putExtra(INTENT_DATA_EVENT, event);
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
    }
}
