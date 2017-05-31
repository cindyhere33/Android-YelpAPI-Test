package operr.com.contest.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class categories extends RealmObject {

    public categories() {
    }

    @PrimaryKey
    public String ID = "1";

    public String alias = "";
    public String title = "";

    @Override
    public String toString() {
        return "categories{" +
                "alias='" + alias + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getAlias() {
        return alias;
    }

    public String getTitle() {
        return title;
    }
}
