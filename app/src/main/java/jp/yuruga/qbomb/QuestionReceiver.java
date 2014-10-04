package jp.yuruga.qbomb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

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
        /*aintent.putExtra("bomb_id", "bomb_idhere");

        aintent.putExtra("lat", 35.9563443);
        aintent.putExtra("lon", 136.22557);
        aintent.putExtra("radius",100f);*/

        log("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+intent.getExtras().toString());
        Bundle b = intent.getExtras();
        JSONObject json = null;
        try {
            json = new JSONObject(b.getString("com.parse.Data"));
            String bombId = json.getString("bomb_id");
            String lat = json.getString("lat");
            String lon = json.getString("lon");
            String rad = json.getString("radius");

            log(bombId+":"+lat+"/"+lon+":"+rad);
            aintent.putExtra("bomb_id", bombId);
            aintent.putExtra("lat", Double.valueOf(lat));
            aintent.putExtra("lon", Double.valueOf(lon));
            aintent.putExtra("radius",Float.valueOf(rad));

            context.startService(aintent);

        } catch (JSONException e) {
            e.printStackTrace();
        }





    }


}
