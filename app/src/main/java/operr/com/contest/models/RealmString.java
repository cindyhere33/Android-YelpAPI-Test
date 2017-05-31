package operr.com.contest.models;

import io.realm.RealmObject;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class RealmString extends RealmObject {

    private String data = "";

    public RealmString(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public RealmString() {
    }


}
