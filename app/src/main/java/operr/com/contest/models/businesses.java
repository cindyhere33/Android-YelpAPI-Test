package operr.com.contest.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class businesses extends RealmObject {

    public businesses() {
    }

    @PrimaryKey
    public String id = "";
    public String name = "";
    public String image_url = "";
    public Boolean is_closed = true;
    public String url = "";
    public Integer review_count = 0;
    public RealmList<categories> categories = new RealmList<>();
    public Double rating = 0.0;
    public coordinates coordinates;
    public RealmList<RealmString> transaction = new RealmList<>();
    public location location;
    public String price = "";
    public String phone = "";
    public String display_phone = "";
    public Double distance = 0.0;

    @Override
    public String toString() {
        return "businesses{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image_url='" + image_url + '\'' +
                ", is_closed=" + is_closed +
                ", url='" + url + '\'' +
                ", review_count=" + review_count +
                ", rating=" + rating +
                ", coordinates=" + coordinates +
                ", location=" + location +
                ", price='" + price + '\'' +
                ", phone='" + phone + '\'' +
                ", display_phone='" + display_phone + '\'' +
                ", distance=" + distance +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public Boolean getIs_closed() {
        return is_closed;
    }

    public String getUrl() {
        return url;
    }

    public Integer getReview_count() {
        return review_count;
    }

    public RealmList<operr.com.contest.models.categories> getCategories() {
        return categories;
    }

    public RealmList<RealmString> getTransaction() {
        return transaction;
    }

    public Double getRating() {
        return rating;
    }

    public operr.com.contest.models.coordinates getCoordinates() {
        return coordinates;
    }


    public operr.com.contest.models.location getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getPhone() {
        return phone;
    }

    public String getDisplay_phone() {
        return display_phone;
    }

    public Double getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setIs_closed(Boolean is_closed) {
        this.is_closed = is_closed;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDisplay_phone(String display_phone) {
        this.display_phone = display_phone;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
