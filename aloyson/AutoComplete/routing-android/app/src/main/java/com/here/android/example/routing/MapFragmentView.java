package com.here.android.example.routing;

/**
 * Created by aloyson_decosta on 23-08-2017.
 */

import android.app.Activity;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
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

    // Position Listener
    PositioningManager.OnPositionChangedListener mPositionListener = new PositioningManager.OnPositionChangedListener() {

        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position,
                                      boolean isMapMatched) {
            if (position != null) {
                mGeoAutoCompleteAdapter.setPosition(position);
            }
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) {

        }
    };

    public MapFragmentView(Activity activity) {
        m_activity=activity;
        createMap();
    }

    public void createMap() {
        mMapFragment = (MapFragment) m_activity.getFragmentManager().findFragmentById(R.id.mapfragment);
//           mMapFragment.init(new OnEngineInitListener() {
//
//            @Override
//            public void onEngineInitializationCompleted(Error error) {
//                if (error == Error.NONE) {
//                    mMap = mMapFragment.getMap();
//                    mMap.setProjectionMode(Map.Projection.MERCATOR);
//                    /**
//                     * TODO: check deprecated
//                     */
//                    mMap.getPositionIndicator().setVisible(true);
//                    mPositionManager = PositioningManager.getInstance();
//                    mPositionManager
//                            .addListener(new WeakReference<>(mPositionListener));
//                    mPositionManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
//
//                }
//
//            }
//        });


        if (mMapFragment != null) {
            /* Initialize the MapFragment, results will be given via the called back. */
            mMapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                    if (error == Error.NONE) {
                        /* get the map object */
                        mMap = mMapFragment.getMap();

                        /*
                         * Set the map center to the 4350 Still Creek Dr Burnaby BC (no animation).
                         */
                        mMap.setCenter(new GeoCoordinate(49.259149, -123.008555, 0.0),
                                Map.Animation.NONE);

                        // m_map.setMapScheme(Map.Scheme.SATELLITE_DAY);

                        /* Set the zoom level to the average between min and max zoom level. */
                        mMap.setZoomLevel((mMap.getMaxZoomLevel() + mMap.getMinZoomLevel()) / 2);
                    } else {
                        Toast.makeText(m_activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
//
        }
    }


    public Map drawMap(){
        return mMap;
    }

}
