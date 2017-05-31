package operr.com.contest.serverapi;


import android.content.Context;
import android.util.Log;

import com.operr.contest.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import operr.com.contest.models.AccessToken;
import operr.com.contest.models.Business;
import operr.com.contest.models.businesses;

/**
 * Created by Sindhura on 5/30/2017.
 */

/*
  Intended to communicate with the server
 */
public class DataGetter {

    private final String TAG = getClass().getSimpleName();
    private CompositeDisposable disposables = new CompositeDisposable();

    public static enum DataType {
        ACCESS_TOKEN, BUSINESS
    }

    public RealmObject readFromRealm(DataType type) {
        Realm realm = Realm.getDefaultInstance();
        switch (type) {
            case ACCESS_TOKEN:
                RealmResults<AccessToken> accessToken = realm.where(AccessToken.class).findAll();
                if (accessToken.size() > 0) {
                    return accessToken.get(0);
                }
                return null;
            case BUSINESS:
                RealmResults<Business> business = realm.where(Business.class).findAll();
                if (business.size() > 0) {
                    return business.get(business.size() - 1);
                }
                break;
        }
        return null;
    }

    //Generic method to write the data objects into database
    private <T extends RealmObject> String writeToRealm(T response) {
        Log.d(TAG, "Received response: " + response);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(transaction -> transaction.copyToRealmOrUpdate(response));
        realm.close();
        return response.toString();
    }

    //Set actual expiry of access token before storing it in database
    private String writeToRealm(AccessToken response) {
        Log.d(TAG, "Received response: " + response);
        Realm realm = Realm.getDefaultInstance();
        if (response.getExpiry().length() > 0)
            response.setActual_expiry(String.valueOf(System.currentTimeMillis() + Long.valueOf(response.getExpiry())));
        realm.executeTransactionAsync(transaction -> transaction.copyToRealmOrUpdate(response));
        realm.close();
        return response.toString();
    }

    //Generic method to initiate server call to download data
    //Downloaded data is written to database and on success, success() method is called and on error, processError() method is called
    private <T extends RealmObject> void requestData(Observable<T> observable) {
        disposables.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .map(this::writeToRealm)
                .subscribe(this::success, this::processError));
    }


    public businesses getBusiness(String id) {
        for (businesses business : ((Business) readFromRealm(DataType.BUSINESS)).getBusinesses()) {
            if (business.getId().equals(id)) {
                return business;
            }
        }
        return null;
    }

    public void retrieveAccessToken(Context context) {
        Observable<AccessToken> accessTokenObservable = HttpClient.getInterface().getAccessToken(context.getResources().getString(R.string.client_secret), context.getResources().getString(R.string.client_id), "client_credentials");
        disposables.add(accessTokenObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .map(this::writeToRealm)
                .subscribe(this::success, this::processError));
    }


    //Get disposables when required so you can dispose them if not needed. Prevents waiting on server calls when not needed
    public CompositeDisposable getDisposables() {
        return disposables;
    }

    //In case of an error, print the error stack trace
    private void processError(Throwable e) {
        e.printStackTrace();
    }

    //If data download is successful print the downloaded data object
    private void success(String response) {
        Log.d(TAG, "Successfully downloaded data" + response);
    }


}
