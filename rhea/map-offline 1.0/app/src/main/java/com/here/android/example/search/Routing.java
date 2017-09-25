package com.here.android.example.search;

import android.app.Activity;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import java.util.List;

/**
 * Created by aloyson_decosta on 22-08-2017.
 */

public class Routing {

    private Activity m_activity;
    private MapRoute m_mapRoute;

    public Routing(Activity activity) {
        m_activity = activity;
    }


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
        createRoute(geoCoordinate,geoCoordinate2,m_map);

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

        RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(0,0));
        RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(0,0));
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
        if(geoCoordinate==null && geoCoordinate2==null){
            Toast.makeText(m_activity,
                    "Select Source and Destination",
                    Toast.LENGTH_LONG).show();
            return;
        }else {

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