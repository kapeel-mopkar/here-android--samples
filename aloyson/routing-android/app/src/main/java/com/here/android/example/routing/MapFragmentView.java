/*
 * Copyright (c) 2011-2017 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.here.android.example.routing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This class encapsulates the properties and functionality of the Map view.A route calculation from
 * HERE Burnaby office to Langley BC is also being handled in this class
 */
public class MapFragmentView {
    private MapFragment m_mapFragment;
    private Button m_createRouteButton;
    private Activity m_activity;
    private Map m_map;
    private MapRoute m_mapRoute;
    private Spinner spinner,spinner2;
    private Properties prop = null;
    private Context context;
    private ToggleButton toggle;

    List<String> list=new ArrayList<String>();

    public MapFragmentView(Activity activity) {
        m_activity = activity;
        initMapFragment();
        /*
         * We use a button in this example to control the route calculation
         */
         try{
             context=m_activity.getApplicationContext();
           Util utilobj=new Util(m_activity.getApplicationContext());
                     for(Object s:utilobj.getKeys())
                   list.add(s.toString());

         } catch (IOException e) {
             e.printStackTrace();
         }

         //Feed Map Schemes
        spinner=addItemsOnSpinner();
        spinner2=addItemsOnSpinner2();
       initCreateRouteButton(spinner,spinner2);
        toggleButton();
        //initCreateRouteButton();
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = (MapFragment) m_activity.getFragmentManager()
                .findFragmentById(R.id.mapfragment);

        if (m_mapFragment != null) {
            /* Initialize the MapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                    if (error == Error.NONE) {
                        /* get the map object */
                        m_map = m_mapFragment.getMap();

                        /*
                         * Set the map center to the 4350 Still Creek Dr Burnaby BC (no animation).
                         */
                        m_map.setCenter(new GeoCoordinate(49.259149, -123.008555, 0.0),
                                Map.Animation.NONE);

                       // m_map.setMapScheme(Map.Scheme.SATELLITE_DAY);

                        /* Set the zoom level to the average between min and max zoom level. */
                        m_map.setZoomLevel((m_map.getMaxZoomLevel() + m_map.getMinZoomLevel()) / 2);
                    } else {
                        Toast.makeText(m_activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    //adding items on Spinner
    // add items into spinner dynamically
    public Spinner addItemsOnSpinner() {
        spinner = (Spinner) m_activity.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(m_activity,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        return spinner;
    }

    public Spinner addItemsOnSpinner2() {

        spinner2 = (Spinner) m_activity.findViewById(R.id.spinner2);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(m_activity,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
        return spinner2;
    }

    //for toggle button
    public void toggleButton(){
        toggle = (ToggleButton)m_activity.findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    m_map.setMapScheme(Map.Scheme.SATELLITE_DAY);
                }
                else
                {
                    m_map.setMapScheme(Map.Scheme.TERRAIN_DAY);
                }
            }
        });
    }


    private void initCreateRouteButton(final Spinner spinner, final Spinner spinner2) {
//private void initCreateRouteButton() {
        m_createRouteButton = (Button) m_activity.findViewById(R.id.button);

        m_createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Clear map if previous results are still on map,otherwise proceed to creating
                 * route
                 */
                if (m_map != null && m_mapRoute != null) {
                    m_map.removeMapObject(m_mapRoute);
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
                    //createRoute();
                    createRoute(spinner,spinner2);
                }
            }
        });

    }

    /* Creates a route from 4350 Still Creek Dr to Langley BC with highways disallowed */
    private void createRoute(Spinner spinner,Spinner spinner2) {
    //private void createRoute() {
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

        RouteWaypoint startPoint=new RouteWaypoint(new GeoCoordinate(0,0));
        RouteWaypoint destination=new RouteWaypoint(new GeoCoordinate(0,0));
        /* Define waypoints for the route */
//        /* START: 4350 Still Creek Dr*/
//        RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(15.283549, 73.982564));
//        /* END: Langley BC */
//        RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(15.486710, 73.822039));

    try{
        Util utilobj=new Util(context);

        //if(spinner.getSelectedItem().toString().equals("Margao")){
        String start=utilobj.getProperty(spinner.getSelectedItem().toString());
        String[] tokens=start.split(",");
        startPoint = new RouteWaypoint(new GeoCoordinate(Float.parseFloat(tokens[0]),Float.parseFloat(tokens[1])));
        //}


        String end=utilobj.getProperty(spinner2.getSelectedItem().toString());
        String[] tokend=end.split(",");
       destination = new RouteWaypoint(new GeoCoordinate(Float.parseFloat(tokend[0]),Float.parseFloat(tokend[1])));

    } catch (IOException e) {
        e.printStackTrace();
    }

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

                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
                                GeoBoundingBox gbb = routeResults.get(0).getRoute()
                                        .getBoundingBox();
                                m_map.zoomTo(gbb, Map.Animation.NONE,
                                        Map.MOVE_PRESERVE_ORIENTATION);
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
}
