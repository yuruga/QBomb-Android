package jp.yuruga.qbomb;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.PushService;
import static jp.yuruga.qbomb.common.Constants.*;





public class QBombApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, PARSE_APP_ID, PARSE_APP_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        /*PushService.setDefaultPushCallback(this, MockActivity.class);
        PushService.subscribe(this, "M5S", MockActivity.class);
        ParseFacebookUtils.initialize(FACEBOOK_APP_ID);*/
    }
}
