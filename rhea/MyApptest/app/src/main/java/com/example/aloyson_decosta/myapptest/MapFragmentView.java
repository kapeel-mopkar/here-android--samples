package com.example.aloyson_decosta.myapptest;

/**
 * Created by aloyson_decosta on 23-08-2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.example.aloyson_decosta.myapptest.FindPlaces.MainActivitySearch;
import com.example.aloyson_decosta.myapptest.FindPlaces.MapFragmentViewSearch;
import com.example.aloyson_decosta.myapptest.adapter.venue.Venue3dActivity;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.customization.CustomizableScheme;
import com.here.android.mpa.mapping.customization.CustomizableVariables;
import com.here.android.mpa.mapping.customization.ZoomRange;

import java.lang.ref.WeakReference;
import java.util.EnumSet;

/**
 * Created by aloyson_decosta on 23-08-2017.
 */

public class MapFragmentView {

    private MapFragmentViewSearch m_mapFragmentViewSearch;
    private MapFragment mMapFragment;
    private Map mMap;
    private Activity m_activity;
    private MainActivity main;
    private String scheme;
    private PositioningManager mPositionManager;
    private Venue3dActivity venue;
    private Map map;
    private ZoomRange range;

    // helper for the very first fix after startup (we want to jump to that position then)
    private boolean firstPositionSet = false;

    // listen for positioning events
    private PositioningManager.OnPositionChangedListener mapPositionHandler = new PositioningManager.OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position, boolean isMapMatched) {
            if (!position.isValid())
                return;
            if (!firstPositionSet) {
                mMap.setCenter(position.getCoordinate(), Map.Animation.BOW);
                firstPositionSet = true;
            }
            GeoCoordinate pos = position.getCoordinate();
            //Log.d(TAG, "New position: " + pos.getLatitude() + " / " + pos.getLongitude() + " / " + pos.getAltitude());
            // ... do something with position ...
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) {
        }
    };

    public MapFragmentView(Activity activity,String scheme) {
        m_activity=activity;

        if(scheme==null){
            scheme="Welcome";
            this.scheme=scheme;
            createMap();
        }
        else if(scheme=="Normal"){
            this.scheme=scheme;
            createMap();
        }
        else if(scheme=="Terrain"){
            this.scheme=scheme;
            createMap();
        }
        else if(scheme=="Satelite"){
            this.scheme=scheme;
            createMap();
        }
        else if(scheme=="Venue"){
            this.scheme=scheme;
            Intent intent = new Intent(m_activity, Venue3dActivity.class);
            m_activity.startActivity(intent);
        }

        else if(scheme=="Search"){
            this.scheme=scheme;
            Intent intent1 = new Intent(m_activity, MainActivitySearch.class);
            m_activity.startActivity(intent1);
        }
    }

    public void createMap() {
        mMapFragment = (MapFragment) m_activity.getFragmentManager().findFragmentById(R.id.mapfragment);
//check if gps is on o off
//        final LocationManager manager = (LocationManager)m_activity.getSystemService(m_activity.LOCATION_SERVICE );
//
//        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
//            Toast.makeText(m_activity, "GPS is disable!", Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(m_activity, "GPS is Enable!", Toast.LENGTH_LONG).show();

           mMapFragment.init(new OnEngineInitListener() {

               @Override
            public void onEngineInitializationCompleted(Error error) {
                if (error == Error.NONE) {
                    mMap = mMapFragment.getMap();
                    // more map settings
                    //setting Map scheme
                    if (scheme.matches("Satelite"))
                    {
                        mMap.setZoomLevel(20);
                        mMap.setMapScheme(Map.Scheme.SATELLITE_DAY);
                    mMap.setProjectionMode(Map.Projection.MERCATOR);
                    mMap.setPedestrianFeaturesVisible(EnumSet.of(Map.PedestrianFeature.STAIRS, Map.PedestrianFeature.ESCALATOR, Map.PedestrianFeature.CROSSWALK, Map.PedestrianFeature.TUNNEL, Map.PedestrianFeature.BRIDGE));
                    }
                    else if (scheme.matches("Terrain"))
                    {
                        mMap.setZoomLevel(20);
                        mMap.setMapScheme(Map.Scheme.TERRAIN_DAY);
                    mMap.setProjectionMode(Map.Projection.MERCATOR);
                    mMap.setPedestrianFeaturesVisible(EnumSet.of(Map.PedestrianFeature.STAIRS, Map.PedestrianFeature.ESCALATOR, Map.PedestrianFeature.CROSSWALK, Map.PedestrianFeature.TUNNEL, Map.PedestrianFeature.BRIDGE));
                    }
                    else if (scheme.matches("Normal"))
                    {
                        mMap.setZoomLevel(20);
                        mMap.setMapScheme(Map.Scheme.NORMAL_DAY);
                    mMap.setProjectionMode(Map.Projection.MERCATOR);
                     mMap.setPedestrianFeaturesVisible(EnumSet.of(Map.PedestrianFeature.STAIRS, Map.PedestrianFeature.ESCALATOR, Map.PedestrianFeature.CROSSWALK, Map.PedestrianFeature.TUNNEL, Map.PedestrianFeature.BRIDGE));
                    }
                    else if (scheme.matches("Welcome"))
                    {  //  mMap.setMapScheme(Map.Scheme.NORMAL_DAY);
                        // create a new scheme
                        mMap.createCustomizableScheme("newCustomScheme", Map.Scheme.NORMAL_DAY_TRANSIT);
                        CustomizableScheme scheme =mMap.getCustomizableScheme("newCustomScheme");
                        ZoomRange range = new ZoomRange (0, 20);

                        // change water color 0m
                        int lightYellow = Color.argb(0, 255,255,224);
                        CustomizableScheme.ErrorCode err = scheme.setVariableValue(CustomizableVariables.Water.COLOR_0M, lightYellow , range);

                        // change water color 3000m
                         scheme.setVariableValue(CustomizableVariables.Water.COLOR_3000M, Color.YELLOW, range);
                        mMap.setProjectionMode(Map.Projection.GLOBE);
                        mMap.setZoomLevel(2);
                        // activate scheme
                        mMap.setMapScheme(scheme);
                    }

                  //  mMap.setProjectionMode(Map.Projection.MERCATOR);
                  //  mMap.setPedestrianFeaturesVisible(EnumSet.of(Map.PedestrianFeature.STAIRS, Map.PedestrianFeature.ESCALATOR, Map.PedestrianFeature.CROSSWALK, Map.PedestrianFeature.TUNNEL, Map.PedestrianFeature.BRIDGE));
                    /**
                     * TODO: check deprecated
                     */

                    mMapFragment.getPositionIndicator().setVisible(true);
                    mPositionManager = PositioningManager.getInstance();
                    mPositionManager
                           .addListener(new WeakReference<>(mapPositionHandler));
                    mPositionManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                    GeoPosition lkp = PositioningManager.getInstance().getLastKnownPosition();
                    if (lkp != null && lkp.isValid())
                        mMap.setCenter(lkp.getCoordinate(), Map.Animation.NONE);
                }

            }
        });
    }


    public Map drawMap(){
        return mMap;
    }

}
