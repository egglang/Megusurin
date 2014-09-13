package ma10.megusurin;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import java.io.IOException;
import java.util.Random;


/**
 *
 */
public class MagicViewFragment extends Fragment {

    public interface OnMagicEffectListener {
        void onFinished();
    }

    private OnMagicEffectListener mListener;

    private static final int MAGIC_EFFECT_TIME = 5000;

    public static final int MAGIC_TYPE_FIRE = 0;

    public static final int MAGIC_TYPE_THUNDER = 1;

    /** Magic Type */
    private static final String ARG_MAGIC_TYPE = "magic_type";

    private int mMagicType;

    private Handler mHandler;

    public static MagicViewFragment newInstance(int magicType) {
        MagicViewFragment fragment = new MagicViewFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_MAGIC_TYPE, magicType);
        fragment.setArguments(args);

        return fragment;
    }
    public MagicViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnMagicEffectListener) {
            mListener = (OnMagicEffectListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMagicType = getArguments().getInt(ARG_MAGIC_TYPE);
        }

        mHandler = new Handler();
    }

    private ImageView mMagicImage;

    private long mStartTime;

    private MediaPlayer mMediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_magic_view, container, false);

        TextView magicText = (TextView) v.findViewById(R.id.magic_view_text);
        int soundId = 0;
        switch (mMagicType) {
            case MAGIC_TYPE_FIRE:
                magicText.setText("闇の炎に抱かれて消えろ");
                soundId = R.raw.fire;
                break;

            case MAGIC_TYPE_THUNDER:
                magicText.setText("神の怒りが地上に降り注ぐ");
                soundId = R.raw.thunder;
                break;
        }

        mMagicImage = (ImageView) v.findViewById(R.id.magic_view_image);
        mHandler.post(mMagicEffector);
        mStartTime = System.currentTimeMillis();

        mMediaPlayer = MediaPlayer.create(getActivity(), soundId);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        mHandler.removeCallbacks(mMagicEffector);
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
                        if (mListener != null) {
                            mListener.onFinished();
                        }
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
        int id = rand.nextInt(2);
        mMagicImage.setX(0);
        switch (id) {
            case 0:
                mMagicImage.setImageResource(R.drawable.fire01);
                break;
            case 1:
                mMagicImage.setImageResource(R.drawable.fire02);
                break;
            case 2:
                mMagicImage.setImageResource(R.drawable.fire03);
                break;
        }

        int moveX = rand.nextInt(1000) - 500;
        mMagicImage.setX(moveX);
    }

    private void showEffectThunder() {
        Random rand = new Random();
        int id = rand.nextInt(2);
        switch (id) {
            case 0:
                mMagicImage.setImageResource(R.drawable.thunder01);
                break;
            case 1:
                mMagicImage.setImageResource(R.drawable.thunder02);
                break;
            case 2:
                mMagicImage.setImageResource(R.drawable.thunder03);
                break;
        }

        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

}
