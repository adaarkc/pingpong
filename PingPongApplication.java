package janel.pingpong;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import janel.pingpong.utils.ParseConstants;


public class PingPongApplication extends Application{

    @Override
    public void onCreate() {

        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "LOLJvZxB101G1nqX91DU6KVbngBUFsngcimgNsrk", "RFEDW5yped2c1zaT288JJBLbSUzmOCbe6KccdU0B");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
