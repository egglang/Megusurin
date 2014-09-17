package ma10.megusurin;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.os.Handler;

import java.util.Random;


/**
 *
 */
public class MagicViewFragment extends Fragment implements EventManager.IEventListener {

    public static final String INTENT_DATA_MAGIC_TEXT = "magic_text";

    public static final String INTENT_DATA_EVENT = "magic_event";

    public static final int EVENT_START_MAGIC = 0;

    public static final int EVENT_FINISH_MAGIC = 1;

    public static final int EVENT_FINISH_SPECIAL_MAGIC = 2;

    private static final int MAGIC_EFFECT_TIME = 5000;

    public static final int MAGIC_TYPE_FIRE = 0;

    public static final int MAGIC_TYPE_THUNDER = 1;

    public static final int MAGIC_TYPE_CARE = 2;

    public static final int MAGIC_TYPE_SPECIAL = 100;

    /** Magic Type */
    private static final String ARG_MAGIC_TYPE = "magic_type";

    /** Preview Mode */
    private static final String ARG_PREVIEW_MOVE = "preview_mode";

    private int mMagicType;

    private boolean mPreviewMode;

    private Handler mHandler;

    public static MagicViewFragment newInstance(int magicType, boolean previewMode) {
        MagicViewFragment fragment = new MagicViewFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_MAGIC_TYPE, magicType);
        args.putBoolean(ARG_PREVIEW_MOVE, previewMode);
        fragment.setArguments(args);

        return fragment;
    }
    public MagicViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMagicType = getArguments().getInt(ARG_MAGIC_TYPE);
            mPreviewMode = getArguments().getBoolean(ARG_PREVIEW_MOVE);
        }

        mHandler = new Handler();
    }

    private ImageView mMagicImage;

    private long mStartTime;

    private MediaPlayer mMediaPlayer;

    private String mMagicText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_magic_view, container, false);

        int soundId = 0;
        switch (mMagicType) {
            case MAGIC_TYPE_FIRE:
                mMagicText = "闇の炎に抱かれて消えろ";
                soundId = R.raw.fire;
                break;

            case MAGIC_TYPE_THUNDER:
                mMagicText = "神の怒りが地上に降り注ぐ";
                soundId = R.raw.thunder;
                break;

            case MAGIC_TYPE_CARE:
                mMagicText = "聖なる水よ傷つきし翼を癒やせ";
                break;

            case MAGIC_TYPE_SPECIAL:
                mMagicText = "聖なる水よ闇の束縛から解き放て";
                break;
        }

        mMagicImage = (ImageView) v.findViewById(R.id.magic_view_image);
        if (mMagicType != MAGIC_TYPE_SPECIAL) {
            mHandler.post(mMagicEffector);
            mStartTime = System.currentTimeMillis();
        } else {
            mMagicImage.setImageResource(R.drawable.water1);
            mHandler.post(mSpecialMagicEffect);
        }

        if (soundId != 0) {
            mMediaPlayer = MediaPlayer.create(getActivity(), soundId);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        dispatchEventFinished(EVENT_START_MAGIC);
    }

    @Override
    public void onStop() {
        super.onStop();

        if ((mMediaPlayer != null) && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        mHandler.removeCallbacks(mMagicEffector);
    }

    private void dispatchEventFinished(final int event) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent intent = new Intent();
            intent.putExtra(INTENT_DATA_EVENT, event);
            intent.putExtra(INTENT_DATA_MAGIC_TEXT, mMagicText);
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
    }

    private final Runnable mMagicEffector = new Runnable() {
        public void run() {
            int delayTime = 0;
            switch (mMagicType) {
                case MAGIC_TYPE_FIRE:
                    delayTime = 250;
                    break;

                case MAGIC_TYPE_THUNDER:
                    delayTime = 100;
                    break;
            }


            long now = System.currentTimeMillis();
            if ((mStartTime + MAGIC_EFFECT_TIME) > now) {
                mHandler.postDelayed(mMagicEffector, delayTime);
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dispatchEventFinished(EVENT_FINISH_MAGIC);
                    }
                }, 500);
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (mMagicType) {
                        case MAGIC_TYPE_FIRE:
                            showEffectFire();
                            break;

                        case MAGIC_TYPE_THUNDER:
                            showEffectThunder();
                            break;
                    }
                }
            });
        }
    };

    private void showEffectFire() {
        Random rand = new Random();
        int id = rand.nextInt(3);
        mMagicImage.setX(0);
        int resId = 0;
        switch (id) {
            case 0:
                resId = mPreviewMode ?
                        R.drawable.fire_trans_01 : R.drawable.fire01;
                break;
            case 1:
                resId = mPreviewMode ?
                        R.drawable.fire_trans_02 : R.drawable.fire02;
                break;
            case 2:
                resId = mPreviewMode ?
                        R.drawable.fire_trans_03 : R.drawable.fire03;
                break;
        }
        mMagicImage.setImageResource(resId);

        int moveX = rand.nextInt(1000) - 500;
        mMagicImage.setX(moveX);
    }

    private void showEffectThunder() {
        Random rand = new Random();
        int id = rand.nextInt(3);
        int resId = 0;
        switch (id) {
            case 0:
                resId = mPreviewMode ?
                        R.drawable.thunder_trans_01 : R.drawable.thunder01;
                break;
            case 1:
                resId = mPreviewMode ?
                        R.drawable.thunder_trans_02 : R.drawable.thunder02;
                break;
            case 2:
                resId = mPreviewMode ?
                        R.drawable.thunder_trans_03 : R.drawable.thunder03;
                break;
        }
        mMagicImage.setImageResource(resId);

        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    private Runnable mSpecialMagicEffect = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dispatchEventFinished(EVENT_FINISH_SPECIAL_MAGIC);
                }
            }, MAGIC_EFFECT_TIME);
        }
    };

    @Override
    public void doEvent(int eventId) {
        // nothing to do
    }
}
