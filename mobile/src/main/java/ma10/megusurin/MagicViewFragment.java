package ma10.megusurin;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import java.util.Random;


/**
 *
 */
public class MagicViewFragment extends Fragment {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMagicType = getArguments().getInt(ARG_MAGIC_TYPE);
        }

        mHandler = new Handler();
    }

    private ImageView mMagicImage;

    private long mStartTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_magic_view, container, false);

        TextView magicText = (TextView) v.findViewById(R.id.magic_view_text);
        switch (mMagicType) {
            case MAGIC_TYPE_FIRE:
                magicText.setText("闇の炎に抱かれて消えろ");
                break;

            case MAGIC_TYPE_THUNDER:
                magicText.setText("神の怒りが地上に降り注ぐ");
                break;
        }

        mMagicImage = (ImageView) v.findViewById(R.id.magic_view_image);
        mHandler.post(mMagicEffector);
        mStartTime = System.currentTimeMillis();

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        mHandler.removeCallbacks(mMagicEffector);
    }

    private final Runnable mMagicEffector = new Runnable() {
        public void run() {
            long now = System.currentTimeMillis();
            if ((mStartTime + MAGIC_EFFECT_TIME) > now) {
                mHandler.postDelayed(mMagicEffector, 100);
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
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

                }
            });
        }
    };


}
