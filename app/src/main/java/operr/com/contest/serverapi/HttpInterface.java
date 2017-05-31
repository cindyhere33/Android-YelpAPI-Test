package operr.com.contest.serverapi;

import io.reactivex.Observable;
import operr.com.contest.models.AccessToken;
import operr.com.contest.models.Business;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Sindhura on 5/30/2017.
 */

public interface HttpInterface {
    @FormUrlEncoded
    @POST("oauth2/token")
    Observable<AccessToken> getAccessToken(@Field("client_secret") String client_secret, @Field("client_id") String client_id, @Field("grant_type") String grant_type);


    @GET("v3/businesses/search")
    Observable<Business> getBusinessObservable(@Header("Authorization") String accessToken, @Query("latitude") double latitude, @Query("longitude") double longitude);

}
