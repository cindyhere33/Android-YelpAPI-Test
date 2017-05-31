package operr.com.contest.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class location extends RealmObject {
    public location() {
    }

    public String address1 = "";
    public String address2 = "";
    public String address3 = "";
    public String city = "";
    public String zip_code = "";
    public String country = "";
    public String state = "";
    public RealmList<RealmString> display_address = new RealmList<RealmString>();

    @PrimaryKey
    public String ID = "1";

    @Override
    public String toString() {
        return "location{" +
                "address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", address3='" + address3 + '\'' +
                ", city='" + city + '\'' +
                ", zip_code='" + zip_code + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public String getCity() {
        return city;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public RealmList<RealmString> getDisplay_address() {
        return display_address;
    }
}
