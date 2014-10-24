package ma10.megusurin.lib.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Enemy View Fragment
 */
public class EnemyViewFragment extends Fragment implements EventManager.IEventListener {

    public static final String INTENT_DATA_ENEMY_NAME = "enemy_name";

    public static final String INTENT_DATA_EVENT = "event_type";

    public static final int EVENT_ENCOUNTED = 0;

    public static final int EVENT_DAMAGED = 1;

    public static final int EVENT_PREPAREATTACK = 2;

    public static final int EVENT_ATTACKED = 3;

    public static final int EVENT_WEAK = 4;

    public static final int EVENT_DIED = 10;

    /** Enemy Type */
    private static final String ARG_ENEMY_TYPE = "enemy_type";

    /** Preview Mode */
    private static final String ARG_PREVIEW_MOVE = "preview_mode";

    private static final int MAX_DAMAGE_COUNT = 3;

    private int mEnemyType;

    private boolean mPreviewMode;

    private String mEnemyName;

    private int mDamagedCount;

    public static EnemyViewFragment newInstance(int enemyType, boolean previewMode) {
        EnemyViewFragment fragment = new EnemyViewFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_ENEMY_TYPE, enemyType);
        args.putBoolean(ARG_PREVIEW_MOVE, previewMode);
        fragment.setArguments(args);

        return fragment;
    }

    public EnemyViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mEnemyType = getArguments().getInt(ARG_ENEMY_TYPE);
            mPreviewMode = getArguments().getBoolean(ARG_PREVIEW_MOVE);
        }

        mHandler = new Handler();
    }

    private Handler mHandler;

    private ImageView mImageEnemy;

    private TextView mTextEnemyName;

    private ImageView mImageEnemyAttack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_enemy_view, container, false);

        mImageEnemy = (ImageView) v.findViewById(R.id.enemy_image);

        mTextEnemyName = (TextView) v.findViewById(R.id.enemy_text_name);

        mImageEnemyAttack = (ImageView) v.findViewById(R.id.enemy_image_attack);

        setupEnemy();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDamagedCount = 0;
        dispatchEnemyViewEventFinish(EVENT_ENCOUNTED);
    }

    private void setupEnemy() {
        // TODO: check enemy type
        mImageEnemy.setImageResource(R.drawable.enemy1);

        mEnemyName = "Enemy01";
        mTextEnemyName.setText(mEnemyName);
    }

    private void dispatchEnemyViewEventFinish(final int event) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent data = new Intent();
            data.putExtra(INTENT_DATA_EVENT, event);
            data.putExtra(INTENT_DATA_ENEMY_NAME, mEnemyName);
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }


    public void onEnemyDamaged() {
        mDamagedCount++;
        mHandler.post(mDamagedEffect);
    }

    public void onEnemyAttack() {
        mHandler.postDelayed(mEnemyAttackEffect, 1000);
    }

    private void showDamagedEffect() {
        mImageEnemy.setColorFilter(Color.WHITE);
    }

    private void clearDamagedEffect() {
        mImageEnemy.clearColorFilter();
    }

    private final Runnable mDamagedEffect = new Runnable() {
        @Override
        public void run() {
            int delay = 200;
            for (int i=0; i < 4; i++) {
                if ((i % 2) == 0) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showDamagedEffect();
                        }
                    }, delay);
                } else {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clearDamagedEffect();
                        }
                    }, delay);
                }
                delay += 200;
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dispatchEnemyViewEventFinish(EVENT_DAMAGED);
                }
            }, delay);
        }
    };

    private void onEnemyWeak() {
        mHandler.post(mWeakEffect);
    }

    private final Runnable mWeakEffect = new Runnable() {
        @Override
        public void run() {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
            alphaAnimation.setDuration(750);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mImageEnemy.setImageResource(R.drawable.enemy2);
                    dispatchEnemyViewEventFinish(EVENT_WEAK);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mImageEnemy.startAnimation(alphaAnimation);
        }
    };

    private void onEnemyDied() {
        mHandler.post(mDiedEffect);
    }

    private final Runnable mDiedEffect = new Runnable() {
        @Override
        public void run() {

            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
            alphaAnimation.setDuration(1000);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mImageEnemy.setImageResource(R.drawable.enemy3);
                    dispatchEnemyViewEventFinish(EVENT_DIED);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mImageEnemy.startAnimation(alphaAnimation);
        }
    };

    private final Runnable mEnemyAttackEffect = new Runnable() {
        @Override
        public void run() {
            showEnemyAttackMotion();
        }
    };

    private void showEnemyAttackMotion() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 20, 0);
        translateAnimation.setDuration(200);
        translateAnimation.setRepeatCount(2);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dispatchEnemyViewEventFinish(EVENT_PREPAREATTACK);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showEnemyAttackEffect();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mImageEnemy.startAnimation(translateAnimation);
    }

    private void showEnemyAttackEffect() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.05f, 2.0f, 0.05f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(750);
        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mImageEnemyAttack.setImageResource(R.drawable.kougeki_1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageEnemyAttack.setImageBitmap(null);
                dispatchEnemyViewEventFinish(EVENT_ATTACKED);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mImageEnemyAttack.startAnimation(scaleAnimation);
    }

    @Override
    public void doEvent(int eventId) {
        switch (eventId) {
            case EventManager.EVENT_DAMAGED_ENEMY:
                onEnemyDamaged();
                break;

            case EventManager.EVENT_ATTACK_ENEMY:
                onEnemyAttack();
                break;

            case EventManager.EVENT_WEAK_ENEMY:
                onEnemyWeak();
                break;

            case EventManager.EVENT_DIED_ENEMY:
                onEnemyDied();
                break;
        }
    }
}
