package com.here.android.example.routing;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.guidance.VoiceSkin;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by aloyson_decosta on 22-08-2017.
 */

public class Routing {

    private Activity m_activity;
    private MapRoute m_mapRoute;
    private Map map;
    private final int EN_US_ID = 206;
    private Button startNavigation;
    private  MapFragment mMapFragment;

    public Routing(Activity activity) {
        m_activity = activity;
    }

    // listen for new instruction events
    private NavigationManager.NewInstructionEventListener instructionHandler = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {
            Maneuver m = NavigationManager.getInstance().getNextManeuver();
           // Log.i(TAG, "New instruction : in " + NavigationManager.getInstance().getNextManeuverDistance() + " m do " + m.getTurn().name() + " / " + m.getAction().name() + " on " + m.getNextRoadName() + " (from " + m.getRoadName() + ")");
            // ...
            // do something with this information, e.g. show it to the user
            super.onNewInstructionEvent();

        }
    };


    public void initCreateRouteButton(final GeoCoordinate geoCoordinate, final GeoCoordinate geoCoordinate2, final Map m_map) {
                if (m_map != null && m_mapRoute != null) {
                    m_map.removeMapObject(m_mapRoute);
                    m_mapRoute = null;
                }
                    /*
                     * The route calculation requires local map data.Unless there is pre-downloaded
                     * map data on device by utilizing MapLoader APIs, it's not recommended to
                     * trigger the route calculation immediately after the MapEngine is
                     * initialized.The INSUFFICIENT_MAP_DATA error code may be returned by
                     * CoreRouter in this case.
                     *
                     */
                    map=m_map;
        createRoute(geoCoordinate,geoCoordinate2,m_map);
        downloadCatalogAndSkin();

    }

    /* Creates a route from 4350 Still Creek Dr to Langley BC with highways disallowed */
        private void createRoute(GeoCoordinate geoCoordinate, GeoCoordinate geoCoordinate2, final Map m_map) {
        /* Initialize a CoreRouter */
            CoreRouter coreRouter = new CoreRouter();

        /* Initialize a RoutePlan */
            RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption.HERE SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
            RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
            routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        /* Disable highway in this route. */
            routeOptions.setHighwaysAllowed(false);
        /* Calculate the shortest route available. */
            routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
        /* Calculate 1 route. */
            routeOptions.setRouteCount(1);
        /* Finally set the route option */
            routePlan.setRouteOptions(routeOptions);

            RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(0, 0));
            RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(0, 0));
        /* Define waypoints for the route */
//        /* START: 4350 Still Creek Dr*/
            // RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(15.283549, 73.982564));
//        /* END: Langley BC */
            //RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(15.486710, 73.822039));


            //    try{
            //  Util utilobj=new Util(context);

            //if(spinner.getSelectedItem().toString().equals("Margao")){
//            String start=utilobj.getProperty(spinner.getSelectedItem().toString());
//            String[] tokens=start.split(",");
//            startPoint = new RouteWaypoint(new GeoCoordinate(Float.parseFloat(tokens[0]),Float.parseFloat(tokens[1])));
//            //}
//
//
//            String end=utilobj.getProperty(spinner2.getSelectedItem().toString());
//            String[] tokend=end.split(",");
//            destination = new RouteWaypoint(new GeoCoordinate(Float.parseFloat(tokend[0]),Float.parseFloat(tokend[1])));
            if (geoCoordinate == null && geoCoordinate2 == null) {
                Toast.makeText(m_activity,
                        "Select Source and Destination",
                        Toast.LENGTH_LONG).show();
                return;
            } else {

                startPoint = new RouteWaypoint(new GeoCoordinate(geoCoordinate));
                destination = new RouteWaypoint(new GeoCoordinate(geoCoordinate2));

            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        /* Add both waypoints to the route plan */
            routePlan.addWaypoint(startPoint);
            routePlan.addWaypoint(destination);

        /* Trigger the route calculation,results will be called back via the listener */
            coreRouter.calculateRoute(routePlan,
                    new Router.Listener<List<RouteResult>, RoutingError>() {
                        @Override
                        public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                        }

                        @Override
                        public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                             RoutingError routingError) {
                        /* Calculation is done.Let's handle the result */
                            if (routingError == RoutingError.NONE) {
                                if (routeResults.get(0).getRoute() != null) {
                                /* Create a MapRoute so that it can be placed on the map */
                                    m_mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                /* Show the maneuver number on top of the route */
                                    m_mapRoute.setManeuverNumberVisible(true);



                                /* Add the MapRoute to the map */
                                    m_map.addMapObject(m_mapRoute);
                                    GeoBoundingBox gbb = routeResults.get(0).getRoute()
                                            .getBoundingBox();
                                    m_map.zoomTo(gbb, Map.Animation.NONE,
                                            Map.MOVE_PRESERVE_ORIENTATION);


                                    startNavigationTrack();

                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
//
                                } else {
                                    Toast.makeText(m_activity,
                                            "Error:route results returned is not valid",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(m_activity,
                                        "Error:route calculation returned error code: " + routingError,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

public void startNavigationTrack(){
            startNavigation = (Button)m_activity.findViewById(R.id.button);

            startNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (startNavigation.getText().equals(getText(R.string.navigation_start))) {
//                        startNavigation.setText(R.string.navigation_stop);
//                    } else if (startNavigation.getText().equals(getText(R.string.navigation_stop))) {
//                        startNavigation.setText(R.string.navigation_start);
//                    }
                    startGuidance();
                }
            });
    }

    private void startGuidance() {
       mMapFragment = (MapFragment) m_activity.getFragmentManager().findFragmentById(R.id.mapfragment);
        // better visuals when switching to special car navigation map scheme
        NavigationManager.getInstance().setMap(map);

        map.setMapScheme(Map.Scheme.CARNAV_DAY);
        map.setTilt(45);
        mMapFragment.getPositionIndicator().setVisible(true);
        //mMapFragment.getPositionIndicator().setZIndex(0);

        map.setZoomLevel(18);
 //       mMapFragment.getPositionIndicator().setAccuracyIndicatorVisible(true);


        // set guidance view to position with road ahead, tilt and zoomlevel was setup before manually
        // choose other update modes for different position and zoom behavior

        NavigationManager.getInstance().setMapUpdateMode(NavigationManager.MapUpdateMode.POSITION);

        // get new guidance instructions
        NavigationManager.getInstance().addNewInstructionEventListener(new WeakReference<>(instructionHandler));

        // set usage of Nuance TTS engine if specified
//        if (useNuance && nuance.isInitialized()) {
//            NavigationManager.getInstance().getAudioPlayer().setDelegate(player);
//        } else {
            // passing null delete any custom audio player that was set earlier
            NavigationManager.getInstance().getAudioPlayer().setDelegate(null);
       // }

        // start simulation with speed of 10 m/s
        NavigationManager.Error e = NavigationManager.getInstance().simulate(m_mapRoute.getRoute(), 10);

        // start real guidance
        //NavigationManager.Error e = NavigationManager.getInstance().startNavigation(currentRoute.getRoute());

//        Log.i(TAG, "Guidance start result : " + e.name());
    }

    private void downloadCatalogAndSkin() {
        // First get the voice catalog from the backend that contains all available languages (so called voiceskins) for download
        VoiceCatalog.getInstance().downloadCatalog(new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
//                if (error != VoiceCatalog.Error.NONE) {
//                    Toast.makeText(m_activity.getApplicationContext(), "Failed to download catalog", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(m_activity.getApplicationContext(), "Catalog downloaded", Toast.LENGTH_LONG).show();

                    // If catalog was successfully downloaded, you can iterate over it / show it to the user / select a skin for download
                    List<VoicePackage> packages = VoiceCatalog.getInstance().getCatalogList();
//                    Log.i(TAG, "# of available packages: " + packages.size());

                    // debug print of the voice skins that are available
//                    for (VoicePackage lang : packages)
//                        Log.d(TAG, "Language name: " + lang.getLocalizedLanguage() + " is TTS: " + lang.isTts() + " ID: " + lang.getId());

                    // Return list of already installed voices on device
                    List<VoiceSkin> localInstalledSkins = VoiceCatalog.getInstance().getLocalVoiceSkins();

                    // debug print of the already locally installed skins
                   // Log.d(TAG, "# of local skins: " + localInstalledSkins.size());
//                    for (VoiceSkin voice : localInstalledSkins) {
//                        Log.d(TAG, "ID: " + voice.getId() + " Language: " + voice.getLanguage());
//                    }

                    downloadVoice(EN_US_ID);
              //  }
            }
        });
    }

    private void downloadVoice(final long skin_id) {
        // kick off the download for a voice skin from the backend
        VoiceCatalog.getInstance().downloadVoice(skin_id, new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
//                if (error != VoiceCatalog.Error.NONE) {
//                    Toast.makeText(m_activity.getApplicationContext(), "Failed downloading voice skin", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(m_activity.getApplicationContext(), "Voice skin downloaded and activated", Toast.LENGTH_LONG).show();
                    // set output for Nuance TTS
//                    if (useNuance && nuance.isInitialized()) {
//                        NavigationManager.getInstance().setTtsOutputFormat(NavigationManager.TtsOutputFormat.NUANCE);
//                    }
                    // set usage of downloaded voice
                    NavigationManager.getInstance().setVoiceSkin(VoiceCatalog.getInstance().getLocalVoiceSkin(skin_id));
//                }
            }
        });
    }
}
