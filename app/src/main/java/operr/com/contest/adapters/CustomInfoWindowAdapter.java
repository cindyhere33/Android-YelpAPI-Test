package operr.com.contest.adapters;

/**
 * Created by Sindhura on 5/30/2017.
 */


import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.operr.contest.R;

import operr.com.contest.models.businesses;
import operr.com.contest.serverapi.DataGetter;
import operr.com.contest.utils.Utils;

/*
    Infowindow display when user clicks on a marker (restaurant)
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    RatingBar ratingBar;
    TextView tvLine1, tvLine2;
    LayoutInflater inflater;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        mWindow = inflater.inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = inflater.inflate(R.layout.custom_info_window, null);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        tvLine1 = (TextView) v.findViewById(R.id.tvLine1);
        tvLine2 = (TextView) v.findViewById(R.id.tvLine2);
        String ID = marker.getSnippet();
        businesses business = new DataGetter().getBusiness(ID);
        if (business != null) {
            tvLine1.setText(business.name);
            tvLine2.setText(Utils.getAddress(business.getLocation().getDisplay_address()));
            ratingBar.setStepSize(0.1f);
            ratingBar.setRating((float) business.rating.doubleValue());
        }
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

}
