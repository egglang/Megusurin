package ma10.megusurin;



import android.app.Activity;
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


/**
 * Message and Status View Fragment
 *
 */
public class MessageViewFragment extends Fragment implements EventManager.IEventListener{

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
    }

    private Handler mHandler;

    private ViewGroup mViewRoot;

    private TextView mTextHp;

    private TextView mTextLv;

    private ImageView mImageChar;

    private TextView mTextMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message_view, container, false);

        mViewRoot = (ViewGroup) v.findViewById(R.id.message_root);

        mImageChar = (ImageView) v.findViewById(R.id.message_image_char);
        mImageChar.setImageResource(R.drawable.char_claudia);

        mTextHp = (TextView) v.findViewById(R.id.message_text_hp);
        mTextLv = (TextView) v.findViewById(R.id.message_text_lv);
        mTextMessage = (TextView) v.findViewById(R.id.message_text_msg);

        return v;
    }

    public void setMessage(String message) {
        mTextMessage.setText(message);
    }

    public void showDamageEffect() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 75, 0, 0);
        translateAnimation.setDuration(200);
        translateAnimation.setRepeatCount(2);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dispatchEventFinished();
//                        if (mListener != null) {
//                            mListener.onFinishDamageEffect();
//                        }
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mViewRoot.startAnimation(translateAnimation);
    }

    @Override
    public void doEvent(int eventId) {
        if (eventId == EventManager.EVENT_DAMAGED_MINE) {
            showDamageEffect();
        }
        else if (eventId == EventManager.EVENT_WAIT_MAGIC) {
            showWaitMagicMessage();
        }
    }

    private void showWaitMagicMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wearから 魔法を使って 攻撃だ！");
        sb.append("\n");
        setMessage(sb.toString());
    }

    private void dispatchEventFinished() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
    }
}
