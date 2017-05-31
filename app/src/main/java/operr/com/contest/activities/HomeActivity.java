package operr.com.contest.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.operr.contest.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import operr.com.contest.adapters.CustomInfoWindowAdapter;
import operr.com.contest.models.AccessToken;
import operr.com.contest.models.Business;
import operr.com.contest.models.businesses;
import operr.com.contest.serverapi.DataGetter;
import operr.com.contest.serverapi.HttpClient;
import operr.com.contest.utils.LocationUtils;
import operr.com.contest.utils.Utils;
import operr.com.contest.utils.VolleySingleton;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraIdleListener {


    //To make server calls
    DataGetter dataGetter;

    //last known location
    Location mLastLocation;


    CompositeDisposable disposable = new CompositeDisposable();
    List<Marker> markers = new ArrayList<>();
    GoogleMap mMap;
    boolean isDrawerLocked = true;
    Toolbar toolbar;
    LocationUtils locationUtils;
    DrawerLayout drawerLayout;
    businesses business;
    double latitude, longitude;
    float zoom = 15.0f;
    static boolean onStartup = true;

    private final String TAG = getClass().getSimpleName();
    private final String KEY_ID = "ID";
    private final String KEY_DRAWER_LOCKED = "isDrawerLocked";
    private final String KEY_STARTUP = "onStartup";
    private final String KEY_LATITUDE = "latitude", KEY_LONGITUDE = "longitude", KEY_ZOOM = "zoom";
    public final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final int REQUEST_CHECK_SETTINGS = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataGetter = new DataGetter();
        setContentView(R.layout.activity_maps);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        extractFromSavedInstance(savedInstanceState);
        Utils.requestInternetConnectionIfNeeded(this);
        locationUtils = new LocationUtils(this);
        if (onStartup) {
            locationUtils.requestLocationServices();
            onStartup = false;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (isDrawerLocked) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        else drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(this)));
        mMap.setOnInfoWindowClickListener(infoWindowClickListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        updateResults(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    //update default location to user's current location
    public void updateLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(locationUtils.getGoogleApiClient());
        if (mLastLocation != null && mMap != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            zoom = 15.0f;
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(latitude, longitude), zoom)));
            updateResults(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    //Get the nearby restaurants for the given location in map bounds
    private void updateResults(double latitude, double longitude) {
        AccessToken accessToken = (AccessToken) dataGetter.readFromRealm(DataGetter.DataType.ACCESS_TOKEN);
        if (accessToken != null) {
            disposable.add(HttpClient.getInterface().getBusinessObservable(accessToken.getToken_type() + " " + accessToken.getAccess_token(), latitude, longitude)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(this::writeToRealm)
                    .subscribe(this::updateUI, this::processError));
        }
    }

    //Save to database
    private Business writeToRealm(Business business) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(transaction -> transaction.copyToRealmOrUpdate(business));
        realm.close();
        return business;
    }

    //Display markers for every business within the map bounds
    private void updateUI(Business business) {
        for (Marker marker : markers) {
            marker.remove();
        }
        for (businesses businesses : business.getBusinesses()) {
            markers.add(mMap.addMarker(new MarkerOptions()
                    .title(businesses.getName())
                    .position(new LatLng(businesses.getCoordinates().getLatitude(), businesses.getCoordinates().getLongitude()))
                    .snippet(businesses.getId())
                    .draggable(false)));
            //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_food))));
        }
    }

    private void processError(Throwable error) {
        error.printStackTrace();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Permission for location usage granted
                        updateLocationOnMap();
                    }
                }
            }
        }
    }


    //When device is rotated, save data
    private void extractFromSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            isDrawerLocked = savedInstanceState.getBoolean(KEY_DRAWER_LOCKED, true);
            onStartup = savedInstanceState.getBoolean(KEY_STARTUP, false);
            latitude = savedInstanceState.getDouble(KEY_LATITUDE);
            longitude = savedInstanceState.getDouble(KEY_LONGITUDE);
            zoom = savedInstanceState.getFloat(KEY_ZOOM);
            String id = savedInstanceState.getString(KEY_ID, null);
            if (id != null) {
                Business allBusinesses = (Business) dataGetter.readFromRealm(DataGetter.DataType.BUSINESS);
                for (businesses business : allBusinesses.getBusinesses()) {
                    if (business.getId().equals(id)) {
                        this.business = business;
                        updateRestaurantDetails(business);
                        break;
                    }
                }
            }
        }
    }


    //Open the drawer when an info window is clicked, update the details of the restaurant on the UI
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = marker -> {
        drawerLayout.openDrawer(Gravity.END);
        Business allBusinesses = (Business) dataGetter.readFromRealm(DataGetter.DataType.BUSINESS);
        for (businesses business : allBusinesses.getBusinesses()) {
            if (business.getId().equals(marker.getSnippet())) {
                this.business = business;
                updateRestaurantDetails(business);
                break;
            }
        }
    };

    //Restaurant details show up in navigation drawer on the right. Update these details when a restaurant info window is selected
    private void updateRestaurantDetails(businesses business) {
        isDrawerLocked = false;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        this.business = business;
        TextView tvTitle = ((TextView) findViewById(R.id.tv_title));
        tvTitle.setText(business.getName());
        tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvTitle.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, WebsiteViewerActivity.class).putExtra(HomeActivity.this.getResources().getString(R.string.WEBSITE_URL), business.getUrl())));
        ((TextView) findViewById(R.id.tv_address)).setText(Utils.getAddress(business.getLocation().getDisplay_address()));
        TextView tvPhone = ((TextView) findViewById(R.id.tv_phone));
        tvPhone.setText("Phone: " + business.getDisplay_phone());
        tvPhone.setOnClickListener(v -> {
            Utils.call(this, business.getPhone());
        });

        ((TextView) findViewById(R.id.tv_price)).setText("Price range: " + business.getPrice());
        if (business.getIs_closed())
            ((TextView) findViewById(R.id.tv_isOpen)).setText("Closed");
        else
            ((TextView) findViewById(R.id.tv_isOpen)).setText("Open");
        RatingBar ratingBar = ((RatingBar) findViewById(R.id.ratingBar));
        ratingBar.setStepSize(0.1f);
        ratingBar.setRating((float) business.getRating().doubleValue());
        ((NetworkImageView) findViewById(R.id.niv_image)).setImageUrl(business.getImage_url(), VolleySingleton.getInstance().getImageLoader());
        ((TextView) findViewById(R.id.tv_distance)).setText("Located " + String.format("%.1f", (business.getDistance()) / 1609.34) + " mi away");
    }

    protected void onStart() {
        if (locationUtils.getGoogleApiClient() != null)
            locationUtils.getGoogleApiClient().connect();
        super.onStart();
    }

    protected void onStop() {
        if (locationUtils.getGoogleApiClient() != null)
            locationUtils.getGoogleApiClient().disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Dispose the observables to avoid receiving callbacks once activity is destroyed
        disposable.dispose();
    }

    @Override
    public void onCameraMove() {
    }

    boolean cameraMove = false;

    //Track camera movement
    @Override
    public void onCameraMoveStarted(int i) {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            cameraMove = true;
        }
    }


    @Override
    public void onCameraMoveCanceled() {
        cameraMove = false;
    }


    //Update the restaurants when user changes the bounds of the map
    @Override
    public void onCameraIdle() {
        if (cameraMove) {
            cameraMove = false;
            if (mMap.getCameraPosition().zoom <= 15.0f) {
                LatLng latlng = mMap.getCameraPosition().target;
                updateResults(latlng.latitude, latlng.longitude);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //Location settings turned on
                        updateLocationOnMap();
                        break;
                    case Activity.RESULT_CANCELED:
                        //User refused to turn on location
                        locationUtils.displayErrorMessage();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DRAWER_LOCKED, isDrawerLocked);
        outState.putBoolean(KEY_STARTUP, false);
        outState.putDouble(KEY_LATITUDE, mMap.getCameraPosition().target.latitude);
        outState.putDouble(KEY_LONGITUDE, mMap.getCameraPosition().target.longitude);
        outState.putFloat(KEY_ZOOM, mMap.getCameraPosition().zoom);
        if (this.business != null) {
            outState.putString(KEY_ID, business.getId());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        extractFromSavedInstance(savedInstanceState);

    }

}
