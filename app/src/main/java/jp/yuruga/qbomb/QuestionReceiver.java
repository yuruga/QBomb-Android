package jp.yuruga.qbomb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import static jp.yuruga.qbomb.common.Share.*;
import static jp.yuruga.qbomb.common.Constants.*;

/**
 * Created by maeda on 2014/10/04.
 */
public class QuestionReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        log("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        return super.getActivity(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        log("aaaaaaaaaaaaaaaaaaaaaaaaa");
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //super.onPushReceive(context, intent);
        log("0000000");
        Intent aintent = new Intent(context.getApplicationContext(), GeofenceService.class);
        aintent.setAction(GeofenceService.ACTION_PUSH_RECEIVED);
        //aintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        aintent.putExtra("bomb_id", "bomb_idhere");

        aintent.putExtra("lat", 35.9563443);
        aintent.putExtra("lon", 136.22557);
        aintent.putExtra("radius",100f);
        //context.startActivity(aintent);
        context.startService(aintent);
    }


}
