package s198.project2.reuse;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android


public class MapActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private String type;
    private Item mItem;
    private List<Item> mItems;
    private Map<Marker, Item> markerMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle extras = getIntent().getExtras();
        Log.i("RECIEVED", extras.toString());
        type = extras.getString("type");
        Log.i("TYPE", type.toString());
        if(type.toString().equals("single")){
            mItem = extras.getParcelable("item");
        }else{
            mItems = (List<Item>) extras.getSerializable("items");
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setUpMapIfNeeded();
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            mMap.setOnInfoWindowClickListener(this);
            mMap.setInfoWindowAdapter(new MapInfoWindow());
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        if (type.equals("single")) {
            setUpSingleViewMap();
        } else {
            setUpMultiViewMap();
        }
    }

    private void setUpMultiViewMap() {
        final String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("TYPE", "multi");


        int numItems =0;
        LatLngBounds bound = null;
        Log.i("ITEMS", ""+mItems.size());


        for (Item i : mItems) {
            List<Double> location = i.getLocation();
            LatLng coordinates = new LatLng(location.get(0), location.get(1));
            if(numItems == 0){
                bound = new LatLngBounds(coordinates, coordinates);
                numItems++;
            } else{
                bound = bound.including(coordinates);
            }
            Marker m = mMap.addMarker(new MarkerOptions().position(coordinates).title(i.getName()));
            markerMap.put(m,i);
        }
//        Log.i("BOUNDS", bound.toString());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(bound.getCenter())      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        Log.i("POSITION", cameraPosition.toString());
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 500, 500, 20));
    }

    private void setUpSingleViewMap(){
        Log.i("ITEM", mItem.toString());
        List<Double> location = mItem.getLocation();
        Log.i("LOCATION", location.toString());
        LatLng coordinates = new LatLng(location.get(0), location.get(1));
        Log.i("COORDINATES", coordinates.toString());

        Marker m = mMap.addMarker(new MarkerOptions().position(coordinates).title(mItem.getName()));
        markerMap.put(m,mItem);
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinates)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        Log.i("CAMERA", cameraPosition.toString());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.i("MARKER", "added");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i("TAG CLICKED" , marker.toString());
        Intent i = new Intent(getBaseContext(), ItemActivity.class);
        i.putExtra("item", markerMap.get(marker));
        startActivity(i);
    }


    public class MapInfoWindow  implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(final Marker marker) {
            Log.i("INFO WINDOW" , "window");
            Button title = new Button(getBaseContext());
            title.setBackgroundColor(getResources().getColor(R.color.green));
            title.setTextColor(getResources().getColor(R.color.white));
            title.setText(marker.getTitle());
            return title;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            Log.i("INFO WINDOW" , "contents");
            Button title = new Button(getBaseContext());
            title.setBackgroundColor(getResources().getColor(R.color.green));
            title.setTextColor(getResources().getColor(R.color.white));
            title.setText(marker.getTitle());
            return title;
        }
    }
}

