package ma10.megusurin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;

/**
 * Enemy View Fragment
 */
public class EnemyViewFragment extends Fragment {

    public interface OnEnemyEventListener {
        void onEnemyEncounted(final String enemyName);

        void onEnemyDied();

        void onEnemyDamaged();

        void onEnemyPrepareAttack(final String enemyName);

        void onEnemyAttacked();
    }

    private OnEnemyEventListener mListener;

    /** Enemy Type */
    private static final String ARG_ENEMY_TYPE = "enemy_type";

    /** Preview Mode */
    private static final String ARG_PREVIEW_MOVE = "preview_mode";

    private int mEnemyType;

    private boolean mPreviewMode;

    private String mEnemyName;

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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEnemyEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEnemyEventListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private Handler mHandler;

    private ImageView mImageEnemy;

    private TextView mTextEnemyName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_enemy_view, container, false);

        mImageEnemy = (ImageView) v.findViewById(R.id.enemy_image);
        mTextEnemyName = (TextView) v.findViewById(R.id.enemy_text_name);

        setupEnemy();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mListener != null) {
            mListener.onEnemyEncounted(mEnemyName);
        }
    }

    private void setupEnemy() {
        // TODO: check enemy type
        mImageEnemy.setImageResource(R.drawable.enemy);
        mEnemyName = "Enemy01";
        mTextEnemyName.setText(mEnemyName);
    }

    public void onEnemyDamaged() {
        mHandler.post(mDamagedEffect);
    }

    public void onEnemyAttack() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 20, 0);
                translateAnimation.setDuration(200);
                translateAnimation.setRepeatCount(2);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (mListener != null) {
                            mListener.onEnemyPrepareAttack(mEnemyName);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mListener != null) {
                            mListener.onEnemyAttacked();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mImageEnemy.startAnimation(translateAnimation);
            }
        }, 1000);
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
                    if (mListener != null) {
                        mListener.onEnemyDamaged();
                    }
                }
            }, delay);
        }
    };
}
