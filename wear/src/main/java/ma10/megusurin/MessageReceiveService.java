package ma10.megusurin;

import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageReceiveService extends WearableListenerService {

    private static final String TAG = MessageReceiveService.class.getSimpleName();
    private static final String PATH_START_APP = "/start_app";
    private static final String PATH_START_CHARGE = "/start_charge";

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "MyWakelockTag");
    }

    @Override
    public void onMessageReceived(final MessageEvent event) {
        Log.d(TAG, "onMessageReceived : " + event);

        Intent intent = null;

        String path = event.getPath();
        if (path.equals(PATH_START_APP)) {
            intent = new Intent(this, WearActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        else if (path.equals(PATH_START_CHARGE)) {
            intent = new Intent(this, ChargeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (intent != null) {
            if (!mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }
            startActivity(intent);

            mWakeLock.release();
        }
    }
}
