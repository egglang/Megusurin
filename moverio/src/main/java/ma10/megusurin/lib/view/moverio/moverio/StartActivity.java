package ma10.megusurin.lib.view.moverio.moverio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioGroup;


public class StartActivity extends Activity {

    private RadioGroup mRGMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= 0x80000000;

        setContentView(R.layout.activity_start);

        ViewGroup root = (ViewGroup) findViewById(R.id.start_root);
        root.setBackgroundResource(R.drawable.megusurin);

        mRGMode = (RadioGroup) findViewById(R.id.start_rg_mode);
        mRGMode.setOnCheckedChangeListener(mModeChangeListener);
    }

    private RadioGroup.OnCheckedChangeListener mModeChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup mRadioGroup, int i) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMainActivity();
                }
            }, 5000);
        }
    };

    private void startMainActivity() {
        Intent intent = new Intent(this, MegusurinActivity.class);
        if (mRGMode.getCheckedRadioButtonId() == ma10.megusurin.lib.view.R.id.start_radio_training) {
            intent.putExtra(MegusurinActivity.KEY_MODE_TRAINING, true);
        }
        startActivity(intent);

        overridePendingTransition(ma10.megusurin.lib.view.R.anim.scale_in, ma10.megusurin.lib.view.R.anim.scale_out);

        this.finish();
    }

}
