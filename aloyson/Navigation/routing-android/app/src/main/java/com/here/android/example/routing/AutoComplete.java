package com.here.android.example.routing;

import android.app.Activity;
import android.graphics.PointF;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.GeocodeRequest;
import com.here.android.mpa.search.Location;
import com.here.android.mpa.search.ResultListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by aloyson_decosta on 23-08-2017.
 */

public class AutoComplete {

    private static final Integer THRESHOLD = 2;
    private CustomAutoCompleteTextView mGeoAutocomplete;
    private CustomAutoCompleteTextView mGeoAutocomplete2;
    private GeocompleteAdapter mGeoAutoCompleteAdapter;
    private GeocompleteAdapter mGeoAutoCompleteAdapter2;
    private GeoCoordinate geoCoordinateS;
    private GeoCoordinate geoCoordinate2;
    private PositioningManager mPositionManager;
    private MapMarker mMarker;
    private MapMarker mMarker2;
    private Routing routingInstance;

    private Button m_Route2;
    private Button startNavigation;
    private MapRoute m_mapRoute;
    private Activity m_activity;
    private MapFragmentView mapFragmentView;
    private  Map mMap;
    private  MapFragment mMapFragment;


    public AutoComplete(Activity activity){
        m_activity=activity;
        mapFragmentView=new MapFragmentView(m_activity);
        autoComplete();
    }



    protected ResultListener<List<Location>> m_listener = new ResultListener<List<Location>>() {

        @Override
        public void onCompleted(List<Location> data, ErrorCode error) {
            if (error == ErrorCode.NONE) {
                if (data != null && data.size() > 0) {
                    geoCoordinateS=data.get(0).getCoordinate();
                    addMarker(data.get(0).getCoordinate());
                }
            }
        }
    };
    protected ResultListener<List<Location>> m_listener2 = new ResultListener<List<Location>>() {
        @Override
        public void onCompleted(List<Location> data, ErrorCode error) {
            if (error == ErrorCode.NONE) {
                if (data != null && data.size() > 0) {
                    geoCoordinate2=data.get(0).getCoordinate();
                    addMarker2(data.get(0).getCoordinate());

                }
            }
        }
    };

    //method to call autocomplete
    public void autoComplete(){
        //Source

        //CaLLING ROUTING
        routingInstance = new Routing(m_activity);
        m_Route2 = (Button) m_activity.findViewById(R.id.button2);
        startNavigation = (Button)m_activity.findViewById(R.id.button);
        mMapFragment = (MapFragment) m_activity.getFragmentManager().findFragmentById(R.id.mapfragment);
          getSource();
        //Destination
          getDestination();

        // m_map=mMap;

        m_Route2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for autohiding keyboard on click
                if(m_activity.getCurrentFocus()!=null && m_activity.getCurrentFocus() instanceof EditText){
                    InputMethodManager imm = (InputMethodManager)m_activity.getSystemService(m_activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(m_activity.getWindow().getCurrentFocus().getWindowToken(), 0);
                }
                /*
                 * Clear map if previous results are still on map,otherwise proceed to creating
                 * route
                 */
                m_Route2.setVisibility(View.INVISIBLE);
                 startNavigation.setVisibility(View.VISIBLE);
                NavigationManager.getInstance().setMap(mMap);
                //stops navigating
                if (NavigationManager.getInstance().getRunningState() == NavigationManager.NavigationState.RUNNING) {
                    mMapFragment.getPositionIndicator().setVisible(false);
                    NavigationManager.getInstance().stop();
                }

                //startNavigation.setText(R.string.wait_gps);
                if (mMap != null && m_mapRoute != null) {
                    mMap.removeMapObject(m_mapRoute);
                    m_mapRoute = null;
                } else {
                    /*
                     * The route calculation requires local map data.Unless there is pre-downloaded
                     * map data on device by utilizing MapLoader APIs, it's not recommended to
                     * trigger the route calculation immediately after the MapEngine is
                     * initialized.The INSUFFICIENT_MAP_DATA error code may be returned by
                     * CoreRouter in this case.
                     *
                     */
                    showRoute(geoCoordinateS,geoCoordinate2,mMap);
                }
            }
        });
    }

    public void showRoute(GeoCoordinate geo1, GeoCoordinate geo2, Map mMap){
        //Routing routing=new Routing(this);
        routingInstance.initCreateRouteButton(geo1,geo2,mMap);
    }


    //source AutoComplete
    private void getSource(){
        mGeoAutocomplete = (CustomAutoCompleteTextView) m_activity.findViewById(R.id.geo_autocomplete);
        mGeoAutocomplete.setThreshold(THRESHOLD);
        mGeoAutocomplete.setLoadingIndicator((android.widget.ProgressBar)
                m_activity.findViewById(R.id.pb_loading_indicator));

        mGeoAutoCompleteAdapter = new GeocompleteAdapter(m_activity);
        mGeoAutocomplete.setAdapter(mGeoAutoCompleteAdapter);

        mGeoAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //for route button
                if(m_Route2.getVisibility()==View.INVISIBLE){
                    startNavigation.setVisibility(View.INVISIBLE);
                    m_Route2.setVisibility(View.VISIBLE);
                }

                //for autohiding keyboard on click
                if(m_activity.getCurrentFocus()!=null && m_activity.getCurrentFocus() instanceof EditText){
                    InputMethodManager imm = (InputMethodManager)m_activity.getSystemService(m_activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(m_activity.getWindow().getCurrentFocus().getWindowToken(), 0);
                }

                String result = (String) adapterView.getItemAtPosition(position);
                mGeoAutocomplete.setText(result);
                GeocodeRequest req = new GeocodeRequest(result);
                mMap=mapFragmentView.drawMap();
                req.setSearchArea(mMap.getBoundingBox());
                req.execute(m_listener);

                //test
            }
        });

        mGeoAutocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //for route button
                if(m_Route2.getVisibility()==View.INVISIBLE){
                    startNavigation.setVisibility(View.INVISIBLE);
                    m_Route2.setVisibility(View.VISIBLE);
                }

                if (!"".equals(mGeoAutocomplete.getText().toString())) {
                    GeocodeRequest req = new GeocodeRequest(mGeoAutocomplete.getText().toString());
                    mMap=mapFragmentView.drawMap();
                    req.setSearchArea(mMap.getBoundingBox());
                    req.execute(m_listener);
                }
            }
        });


    }

    //Destination AutoComplete
    private void getDestination(){
        mGeoAutocomplete2 = (CustomAutoCompleteTextView) m_activity.findViewById(R.id.geo_autocomplete2);
        mGeoAutocomplete2.setThreshold(THRESHOLD);
        mGeoAutocomplete2.setLoadingIndicator((android.widget.ProgressBar)
                m_activity.findViewById(R.id.pb_loading_indicator2));

        mGeoAutoCompleteAdapter2 = new GeocompleteAdapter(m_activity);
        mGeoAutocomplete2.setAdapter(mGeoAutoCompleteAdapter2);

        mGeoAutocomplete2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //for route button
                if(m_Route2.getVisibility()==View.INVISIBLE){
                    startNavigation.setVisibility(View.INVISIBLE);
                    m_Route2.setVisibility(View.VISIBLE);
                }
                //for autohiding keyboard on click
                if(m_activity.getCurrentFocus()!=null && m_activity.getCurrentFocus() instanceof EditText){
                    InputMethodManager imm = (InputMethodManager)m_activity.getSystemService(m_activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(m_activity.getWindow().getCurrentFocus().getWindowToken(), 0);
                }

                String result = (String) adapterView.getItemAtPosition(position);
                mGeoAutocomplete2.setText(result);
                GeocodeRequest req = new GeocodeRequest(result);
                mMap=mapFragmentView.drawMap();
                req.setSearchArea(mMap.getBoundingBox());
                req.execute(m_listener2);
            }
        });

        mGeoAutocomplete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for route button
                if(m_Route2.getVisibility()==View.INVISIBLE){
                    startNavigation.setVisibility(View.INVISIBLE);
                    m_Route2.setVisibility(View.VISIBLE);
                }

                if (!"".equals(mGeoAutocomplete2.getText().toString())) {
                    GeocodeRequest req = new GeocodeRequest(mGeoAutocomplete2.getText().toString());
                    mMap=mapFragmentView.drawMap();
                    req.setSearchArea(mMap.getBoundingBox());
                    req.execute(m_listener2);
                }
            }
        });
    }
    /**
     * Add marker on map.
     *
     * @param geoCoordinate GeoCoordinate for marker to be added.
     */
    private void addMarker(GeoCoordinate geoCoordinate) {
        if (mMarker == null) {
            Image image = new Image();
            try {
                image.setImageResource(R.mipmap.redicon1);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            mMarker = new MapMarker(geoCoordinate, image);
            mMarker.setAnchorPoint(new PointF(image.getWidth() / 2, image.getHeight()));
            mMap.addMapObject(mMarker);
        } else {
            mMarker.setCoordinate(geoCoordinate);
        }
        mMap.setCenter(geoCoordinate, Map.Animation.BOW);    }

    private void addMarker2(GeoCoordinate geoCoordinate) {
        if (mMarker2 == null) {
            Image image = new Image();
            try {
                image.setImageResource(R.mipmap.black);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            mMarker2 = new MapMarker(geoCoordinate, image);
            mMarker2.setAnchorPoint(new PointF(image.getWidth() / 2, image.getHeight()));
            mMap.addMapObject(mMarker2);
        } else {
            mMarker2.setCoordinate(geoCoordinate);
        }

        mMap.setCenter(geoCoordinate, Map.Animation.BOW);
    }
}
