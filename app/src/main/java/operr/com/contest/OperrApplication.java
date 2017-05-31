package operr.com.contest;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import operr.com.contest.models.AccessToken;
import operr.com.contest.serverapi.DataGetter;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class OperrApplication extends Application {

    private static OperrApplication mInstance;

    public static Context getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        DataGetter dataGetter = new DataGetter();
        AccessToken token = (AccessToken) dataGetter.readFromRealm(DataGetter.DataType.ACCESS_TOKEN);
        if (token == null)
            dataGetter.retrieveAccessToken(this);
    }

}
