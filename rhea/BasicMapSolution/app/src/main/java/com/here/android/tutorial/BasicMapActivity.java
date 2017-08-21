/*
 * Copyright (c) 2011-2017 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */
package com.here.android.tutorial;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapContainer;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;

import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
import com.here.android.mpa.search.Category;
import com.here.android.mpa.search.CategoryFilter;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ExploreRequest;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.lang.Object;

public class BasicMapActivity extends Activity {

    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // map embedded in the map fragment

    private Map map = null;


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        checkPermissions();
//    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }


        private void initialize() {
        setContentView(R.layout.activity_main);
         //Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.maptest);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Set the map center to the Vancouver region (no animation)
                    map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0),
                            Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });
    }
//


    /**
     * code from github starts here
     **/

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    private PositioningManager posManager;

    // markers and containers
    private MapContainer placesContainer = null;
    private MapMarker selectedMapMarker = null;

    private MapRoute mapRoute = null;
    private float range;

    // Create a gesture listener and add it to the MapFragment
    MapGesture.OnGestureListener listener =
            new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
                @Override
                public boolean onMapObjectsSelected(List<ViewObject> objects) {
                    // There are various types of map objects, but we only want
                    // to handle the MapMarkers we have added
                    for (ViewObject viewObj : objects) {
                        if (viewObj.getBaseType() == ViewObject.Type.USER_OBJECT) {
                            if (((MapObject) viewObj).getType() == MapObject.Type.MARKER) {

                                // save the selected marker to use during route calculation
                                selectedMapMarker = ((MapMarker) viewObj);

                                // Create the RoutePlan and add two waypoints
                                RoutePlan routePlan = new RoutePlan();

                                /**
                                 * TODO: changed code
                                 */
                                // Use our current position as the first waypoint
                                routePlan.addWaypoint( new RouteWaypoint(new GeoCoordinate(
                                        posManager.getPosition().getCoordinate())));
                                // Use the marker's position as the second waypoint
                                routePlan.addWaypoint(  new RouteWaypoint(new GeoCoordinate(
                                        ((MapMarker) viewObj).getCoordinate())));

                                // Create RouteOptions and set to fastest & pedestrian mode
                                RouteOptions routeOptions = new RouteOptions();
                                routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
                                routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
                                routePlan.setRouteOptions(routeOptions);

                                // Create a RouteManager and calculate the route

                                CoreRouter rm = new CoreRouter();
                                rm.calculateRoute(routePlan, new RouteListener());

                                // Remove all other markers from the map
                                for (MapObject mapObject : placesContainer.getAllMapObjects()) {
                                    if (!mapObject.equals(viewObj)) {
                                        placesContainer.removeMapObject(mapObject);
                                    }
                                }

                                // If user has tapped multiple markers, just display one route
                                break;
                            }
                        }
                    }
                    // return false to allow the map to handle this callback also
                    return false;
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(
                R.id.maptest);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    mapFragment.getMapGesture().addOnGestureListener(listener);
                    onMapFragmentInitializationCompleted();
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment: " + error);
                }
            }
        });
    }



    private void onMapFragmentInitializationCompleted() {
        // retrieve a reference of the map from the map fragment
        map = mapFragment.getMap();
        // start the position manager
        posManager = PositioningManager.getInstance();
        posManager.start(PositioningManager.LocationMethod.GPS_NETWORK);

        // Set a pedestrian friendly map scheme
        map.setMapScheme(Map.Scheme.PEDESTRIAN_DAY);

        // Display position indicator
        map.getPositionIndicator().setVisible(true);

        placesContainer = new MapContainer();
        map.addMapObject(placesContainer);

        // Set the map center coordinate to the current position
        map.setCenter(posManager.getPosition().getCoordinate(), Map.Animation.NONE);
        map.setZoomLevel(14);
    }

    public void findPlaces(View view) {

        // clear the map
        if (mapRoute != null) {
            map.removeMapObject(mapRoute);
            mapRoute = null;
        }
        placesContainer.removeAllMapObjects();

        // collect data to set options
        GeoCoordinate myLocation = posManager.getPosition().getCoordinate();
        /**
         * TODO: Add editSteps fragment
         */
        EditText editSteps = (EditText) findViewById(R.id.maptest);
        int noOfSteps;
        try {
            noOfSteps = Integer.valueOf(editSteps.getText().toString());
        } catch (NumberFormatException e) {
            // if input is not a number set to default of 2500
            noOfSteps = 2500;
        }
        range = ((noOfSteps * 0.762f) / 2);

        // create an exploreRequest and set options
        ExploreRequest request = new ExploreRequest();
        request.setSearchArea(myLocation, (int) range);
        request.setCollectionSize(50);
        request.setCategoryFilter(new CategoryFilter().add(Category.Global.SIGHTS_MUSEUMS));

        try {
            ErrorCode error = request.execute(new SearchRequestListener());
            if (error != ErrorCode.NONE) {
                // Handle request error
            }
        } catch (IllegalArgumentException ex) {
            // Handle invalid create search request parameters
        }
    }

    // search request listener
    class SearchRequestListener implements ResultListener<DiscoveryResultPage> {

        @Override
        public void onCompleted(DiscoveryResultPage data, ErrorCode error) {
            if (error != ErrorCode.NONE) {
                // Handle error
            } else {
                // results can be of different types
                // we are only interested in PlaceLinks
                List<PlaceLink> results = data.getPlaceLinks();
                if (results.size() > 0) {
                    for (PlaceLink result : results) {
                        // get all results that are far away enough to be a good candidate
                        if (result.getDistance() < range && result.getDistance() > (range * 0.7f)) {
                            GeoCoordinate c = result.getPosition();

                            com.here.android.mpa.common.Image img =
                                    new com.here.android.mpa.common.Image();
                            try {
                                img.setImageAsset("pin.png");
                            } catch (IOException e) {
                                // handle exception
                            }
                            MapMarker marker = new MapMarker(c, img);
                            marker.setTitle(result.getTitle());

                            // using a container to group the markers
                            placesContainer.addMapObject(marker);
                        }
                    }
                } else {
                    // handle empty result case}
                }
            }
        }
    }

    private class RouteListener implements CoreRouter.Listener {

        public void onProgress(int percentage) {
            // You can use this to display the progress of the route calculation
        }

        @Override
        public void onCalculateRouteFinished(List<RouteResult> routeResult, RoutingError routingError) {
            if(routingError == RoutingError.NONE){
                // Render the route on the map
                mapRoute = new MapRoute(routeResult.get(0).getRoute());
                map.addMapObject(mapRoute);
                int routeLength = routeResult.get(0).getRoute().getLength();
                float steps = 2 * (routeLength / 0.8f);
                String title = selectedMapMarker.getTitle();
                title = String.format(Locale.ENGLISH, "It's %d steps to %s and back!",
                        ((int) steps), title);
                selectedMapMarker.setTitle(title);
                selectedMapMarker.showInfoBubble();
            } else{
                // Display a message indicating route calculation failure
            }
        }

        /*public void onCalculateRouteFinished(RouteManager.Error error, List<RouteResult> routeResult) {
            // If the route was calculated successfully
            if (error == RouteManager.Error.NONE) {
                // Render the route on the map
                mapRoute = new MapRoute(routeResult.get(0).getRoute());
                map.addMapObject(mapRoute);
                int routeLength = routeResult.get(0).getRoute().getLength();
                float steps = 2 * (routeLength / 0.8f);
                String title = selectedMapMarker.getTitle();
                title = String.format(Locale.ENGLISH, "It's %d steps to %s and back!",
                        ((int) steps), title);
                selectedMapMarker.setTitle(title);
                selectedMapMarker.showInfoBubble();
            } else {
                // Display a message indicating route calculation failure
            }
        }*/
    }

}