package ma10.megusurin;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageReceiveService extends WearableListenerService {

    private static final String TAG = MessageReceiveService.class.getSimpleName();
    private static final String PATH_START_APP = "/start_app";

    @Override
    public void onMessageReceived(final MessageEvent event) {
        Log.d(TAG, "onMessageReceived : " + event);

        String path = event.getPath();
        if (path.equals(PATH_START_APP)) {
            Intent intent = new Intent(this, WearActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
