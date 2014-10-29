package ma10.megusurin.lib.view;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


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

    private static  final int MAX_DAMAGE_ALPHA = 160;

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

    private TextView mTextMessage;

    private ProgressBar mProgressHp;

    private ImageView mDamageEffectView;

    private int mHp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message_view, container, false);

        mViewRoot = (ViewGroup) v.findViewById(R.id.message_root);

        mTextHp = (TextView) v.findViewById(R.id.message_text_hp);
        mTextHp.setText(String.valueOf(MAX_HITPOINT));

        mProgressHp = (ProgressBar) v.findViewById(R.id.message_progress_hp);
        mProgressHp.setMax(MAX_HITPOINT);

        mTextMessage = (TextView) v.findViewById(R.id.message_text_msg);

        mDamageEffectView = (ImageView) v.findViewById(R.id.message_damage_view);

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
        mDamageEffectView.setAlpha((float) MAX_DAMAGE_ALPHA / 255f);
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
                    mProgressHp.setProgress(mHp);
                }
            });
        }
    };

    private void showCuredEffect() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 2000);
        translateAnimation.setDuration(1500);

        float currentAlpha = (float) MAX_DAMAGE_ALPHA / 255f;
        AlphaAnimation alphaAnimation = new AlphaAnimation(currentAlpha, 0.0f);
        alphaAnimation.setDuration(1500);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDamageEffectView.setAlpha(0.0f);
                mHandler.postDelayed(mIncremintHitPoint, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mDamageEffectView.startAnimation(animationSet);
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
                    mProgressHp.setProgress(mHp);
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
        else if (eventId == EventManager.EVENT_DRIVING) {
            showDrivingMessage();
            changeToDriveMode();
        }
        else if (eventId == EventManager.EVENT_ENCOUNTER_ENEMY) {
            changeToBattleMode();
        }
    }

    private void showDrivingMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("索敵中...");
        sb.append("\n");
        setMessage(sb.toString());
    }

    private void showWaitMagicMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wearから 魔法を使って 攻撃だ！");
        sb.append("\n");
        setMessage(sb.toString());
    }

    private void changeToBattleMode() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AnimationSet animationSet = new AnimationSet(false);

                final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                final TranslateAnimation translateAnimation = new TranslateAnimation(0, getView().getWidth() + 10, 0, 0);

                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mTextMessage.setBackgroundResource(R.drawable.msg_meg_30);

                        AnimationSet animationSet1 = new AnimationSet(false);

                        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
                        TranslateAnimation translateAnimation1 = new TranslateAnimation(-getView().getWidth(), 0, 0, 0);

                        animationSet1.setFillAfter(true);
                        animationSet1.addAnimation(alphaAnimation1);
                        animationSet1.addAnimation(translateAnimation1);
                        animationSet1.setDuration(250);

                        mTextMessage.startAnimation(animationSet1);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(250);
                animationSet.setFillAfter(true);
                mTextMessage.startAnimation(animationSet);
            }
        });
    }

    private void changeToDriveMode() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextMessage.setBackgroundResource(R.drawable.msg_drive_30);
            }
        });
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
