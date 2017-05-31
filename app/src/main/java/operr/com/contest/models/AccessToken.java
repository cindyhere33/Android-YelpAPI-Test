package operr.com.contest.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class AccessToken extends RealmObject {
    @PrimaryKey
    public String access_token = "";
    public String expiry = "";
    public String token_type = "";
    public String actual_expiry = "";

    @Override
    public String toString() {
        return "AccessToken{" +
                "access_token='" + access_token + '\'' +
                ", expiry='" + expiry + '\'' +
                ", token_type='" + token_type + '\'' +
                ", actual_expiry='" + actual_expiry + '\'' +
                '}';
    }

    public void setActual_expiry(String actual_expiry) {
        this.actual_expiry = actual_expiry;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getActual_expiry() {
        return actual_expiry;
    }
}
