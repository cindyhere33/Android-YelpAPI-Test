package operr.com.contest.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class Business extends RealmObject {
    public Business() {
    }

    @PrimaryKey
    public String ID = "1";

    public RealmList<businesses> businesses = new RealmList<>();

    public RealmList<operr.com.contest.models.businesses> getBusinesses() {
        return businesses;
    }

    @Override
    public String toString() {
        return "Business{" +
                "businesses=" + businesses +
                '}';
    }
}
