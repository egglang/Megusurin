package ma10.megusurin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import ma10.megusurin.lib.web.MegusurinEventChecker;


public class StartActivity extends Activity implements MegusurinEventChecker.IMegusurinEventListener {

    private Handler mHandler;

    private MegusurinEventChecker mEventChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_start);

        ViewGroup root = (ViewGroup) findViewById(R.id.start_root);
        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        root.setBackgroundResource(R.drawable.megusurin);

        mHandler = new Handler();

        mEventChecker = new MegusurinEventChecker(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mEventChecker != null) {
            mEventChecker.startEventCheck();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mEventChecker != null) {
            mEventChecker.stopEventCheck();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MegusurinActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.scale_in, R.anim.scale_out);

        this.finish();
    }

    @Override
    public void onGameStart() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 3000);
    }

    @Override
    public void onEnemyEncount() {

    }

    @Override
    public void onWaitBattlePrepare() {

    }

    @Override
    public void onBattlePrepared() {

    }
}
