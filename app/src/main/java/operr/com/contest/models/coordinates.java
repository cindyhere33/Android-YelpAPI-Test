package operr.com.contest.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class coordinates extends RealmObject {
    public coordinates() {
    }

    @PrimaryKey
    public String ID = "1";
    public Double latitude = 0.0;
    public Double longitude = 0.0;

    @Override
    public String toString() {
        return "coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
