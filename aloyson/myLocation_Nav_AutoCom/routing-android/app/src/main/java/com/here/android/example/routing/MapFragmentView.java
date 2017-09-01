package com.here.android.example.routing;

/**
 * Created by aloyson_decosta on 23-08-2017.
 */

import android.app.Activity;
import android.location.LocationManager;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.IconCategory;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;

import java.lang.ref.WeakReference;

/**
 * Created by aloyson_decosta on 23-08-2017.
 */

public class MapFragmentView {
    private MapFragment mMapFragment;
    private Map mMap;
    private Activity m_activity;
    private PositioningManager mPositionManager;
    private GeocompleteAdapter mGeoAutoCompleteAdapter;
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
           // Log.i(TAG, "Position fix changed : " + status.name() + " / " + method.name());
            // only allow guidance, when we have a position fix
//            if (status == PositioningManager.LocationStatus.AVAILABLE &&
//                    (method == PositioningManager.LocationMethod.NETWORK || method == PositioningManager.LocationMethod.GPS)) {
//                // we have a fix, so allow start of guidance now
//                if (!startNavigation.isEnabled())
//                    startNavigation.setEnabled(true);
//                if (startNavigation.getText().equals(getText(R.string.wait_gps)))
//                    startNavigation.setText(R.string.navigation_start);
//            }
        }
    };

    public MapFragmentView(Activity activity) {
        m_activity=activity;
        createMap();
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
                    mMap.setProjectionMode(Map.Projection.MERCATOR);
                   // mMap.setProjectionMode(Map.Projection.GLOBE);  // globe projection
                    mMap.setExtrudedBuildingsVisible(true);  // enable 3D building footprints
                    mMap.setLandmarksVisible(true);  // 3D Landmarks visible
                    mMap.setCartoMarkersVisible(IconCategory.ALL, true);  // show embedded map markers
                    mMap.setSafetySpotsVisible(true); // show speed cameras as embedded markers on the map
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


//        if (mMapFragment != null) {
//            /* Initialize the MapFragment, results will be given via the called back. */
//            mMapFragment.init(new OnEngineInitListener() {
//                @Override
//                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
//
//                    if (error == Error.NONE) {
//                        /* get the map object */
//                        mMap = mMapFragment.getMap();
//
//                        // more map settings
//                        mMap.setProjectionMode(Map.Projection.GLOBE);  // globe projection
//                        mMap.setExtrudedBuildingsVisible(true);  // enable 3D building footprints
//                        mMap.setLandmarksVisible(true);  // 3D Landmarks visible
//                        mMap.setCartoMarkersVisible(IconCategory.ALL, true);  // show embedded map markers
//                        mMap.setSafetySpotsVisible(true); // show speed cameras as embedded markers on the map
//
//                        /*
//                         * Set the map center to the 4350 Still Creek Dr Burnaby BC (no animation).
//                         */
//
//                        mMap.setCenter(new GeoCoordinate(49.259149, -123.008555, 0.0),
//                                Map.Animation.NONE);
//
//                        // m_map.setMapScheme(Map.Scheme.SATELLITE_DAY);
//                       // mMapFragment.getPositionIndicator().setVisible(true);
//
//                        /* Set the zoom level to the average between min and max zoom level. */
//                        mMap.setZoomLevel((mMap.getMaxZoomLevel() + mMap.getMinZoomLevel()) / 2);
//
//                    } else {
//                        Toast.makeText(m_activity,
//                                "ERROR: Cannot initialize Map with error " + error,
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
////
//        }
    }


    public Map drawMap(){
        return mMap;
    }

}
