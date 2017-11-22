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

package com.example.aloyson_decosta.myapptest.FindPlaces;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aloyson_decosta.myapptest.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.search.Category;
import com.here.android.mpa.search.CategoryFilter;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ExploreRequest;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the properties and functionality of the Map view.It also implements 4
 * types of discovery requests that HERE Android SDK provides as example.
 */
public class MapFragmentViewSearch {
    public static List<DiscoveryResult> s_ResultList;
    private MapFragment m_mapFragment;
    private Activity m_activity;
    private Map m_map;
    private Button m_placeDetailButton;
    private List<MapObject> m_mapObjectList = new ArrayList<>();
    private List<MapObject> m_mapObjectList_new = new ArrayList<>();
    private float range;
    private Routing routingInstance;

    public
    MapFragmentViewSearch(Activity activity) {
        m_activity = activity;
        /*
         * The map fragment is not required for executing search requests. However in this example,
         * we will put some markers on the map to visualize the location of the search results.
         */
        initMapFragment();
        initSearchControlButtons();
        /* We use a list view to present the search results */
        initResultListButton();
        routingInstance=new Routing(m_activity);
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = (MapFragment) m_activity.getFragmentManager()
                .findFragmentById(R.id.mapfragmentsearch);

        /* Initialize the MapFragment, results will be given via the called back. */
        if (m_mapFragment != null)
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    if (error == Error.NONE) {
                        m_map = m_mapFragment.getMap();
                        m_map.setCenter(new GeoCoordinate(49.259149, -123.008555),
                                Map.Animation.NONE);
                        m_map.setZoomLevel(13.2);

                        m_mapFragment.getPositionIndicator().setVisible(true);

                        m_mapFragment.getMapGesture()
                                .addOnGestureListener(new MapGesture.OnGestureListener() {
                                    @Override
                                    public void onPanStart() {
                                        //showMsg("onPanStart");
                                    }

                                    @Override
                                    public void onPanEnd() {
                                        /* show toast message for onPanEnd gesture callback */
                                        //showMsg("onPanEnd");
                                    }

                                    @Override
                                    public void onMultiFingerManipulationStart() {

                                    }

                                    @Override
                                    public void onMultiFingerManipulationEnd() {

                                    }

                                    @Override
                                    public boolean onTapEvent(PointF pointF) {

                                        return false;
                                    }

                                    @Override
                                    public boolean onMapObjectsSelected(List<ViewObject> objects) {
                                        // TODO Auto-generated method stub

                                        if(objects.size()>1)
                                            Toast.makeText(m_activity.getApplicationContext(),"Select 1 Point", Toast.LENGTH_LONG).show();
                                        else {
                                            for (ViewObject viewObject : objects) {
//                                            if (viewObject.getBaseType() == ViewObjectType.USER_OBJECT) {

                                                MapObject mapObject = (MapObject) viewObject;
//                                                if (mapObject.getType() == MapObjectType.MARKER) {
                                                MapMarker marker = FindObject((MapMarker) mapObject);
                                                if (marker != null) {
                                                    Image img1 = new Image();
                                                    try {
                                                        img1.setImageResource(R.mipmap.destination);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    marker.setIcon(img1);
                                                    routingInstance.initCreateRouteButton(new GeoCoordinate(49.259149, -123.008555), marker.getCoordinate(), m_map);

                                                }
//                                                    return false;
//                                                }
//                                            }
                                            }
                                        }
                                        return false;
                                    }

                                    @Override
                                    public boolean onDoubleTapEvent(PointF pointF) {
                                        return false;
                                    }

                                    @Override
                                    public void onPinchLocked() {

                                    }

                                    @Override
                                    public boolean onPinchZoomEvent(float v, PointF pointF) {
                                        return false;
                                    }

                                    @Override
                                    public void onRotateLocked() {

                                    }

                                    @Override
                                    public boolean onRotateEvent(float v) {
                                        /* show toast message for onRotateEvent gesture callback */
                                        //showMsg("onRotateEvent");
                                        return false;
                                    }

                                    @Override
                                    public boolean onTiltEvent(float v) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onLongPressEvent(PointF pointF) {
                                        return false;
                                    }

                                    @Override
                                    public void onLongPressRelease() {

                                    }

                                    @Override
                                    public boolean onTwoFingerTapEvent(PointF pointF) {
                                        return false;
                                    }
                                });
                    } else {
                        Toast.makeText(m_activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }


                }
            });

    }

    private void initResultListButton() {
        m_placeDetailButton = (Button) m_activity.findViewById(R.id.resultListBtn);
        m_placeDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Open the ResultListActivity */
                Intent intent = new Intent(m_activity, ResultListActivity.class);
                m_activity.startActivity(intent);
            }
        });
    }

    private void initSearchControlButtons() {

        Button exploreRequestButton = (Button) m_activity.findViewById(R.id.exploreRequestBtn);
        exploreRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Trigger an ExploreRequest based on the bounding box of the current map and the
                 * filter for Shopping category.Please refer to HERE Android SDK API doc for other
                 * supported location parameters and categories.
                 */
                cleanMap();
                ExploreRequest exploreRequest = new ExploreRequest();
                exploreRequest.setSearchArea(m_map.getBoundingBox());
                CategoryFilter filter = new CategoryFilter();

                /**
                 * TODO: Add editSteps fragment
                 */
                EditText editSteps = (EditText) m_activity.findViewById(R.id.editsteps);
                int noOfSteps;
                try {
                    noOfSteps = Integer.valueOf(editSteps.getText().toString());
                } catch (NumberFormatException e) {
                    // if input is not a number set to default of 2500
                    noOfSteps = 2500;
                }
                range = ((noOfSteps * 0.762f) / 2);

                exploreRequest.setSearchArea(new GeoCoordinate(49.259149, -123.008555), (int) range);
                filter.add(Category.Global.SHOPPING);
                exploreRequest.setCategoryFilter(filter);
                exploreRequest.execute(discoveryResultPageListener);

            }
        });




    }

    private ResultListener<DiscoveryResultPage> discoveryResultPageListener = new ResultListener<DiscoveryResultPage>() {
        @Override
        public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
            if (errorCode == ErrorCode.NONE) {
                /* No error returned,let's handle the results */
                m_placeDetailButton.setVisibility(View.VISIBLE);

                /*
                 * The result is a DiscoveryResultPage object which represents a paginated
                 * collection of items.The items can be either a PlaceLink or DiscoveryLink.The
                 * PlaceLink can be used to retrieve place details by firing another
                 * PlaceRequest,while the DiscoveryLink is designed to be used to fire another
                 * DiscoveryRequest to obtain more refined results.
                 */
                s_ResultList = discoveryResultPage.getItems();
                for (DiscoveryResult item : s_ResultList) {
                    /*
                     * Add a marker for each result of PlaceLink type.For best usability, map can be
                     * also adjusted to display all markers.This can be done by merging the bounding
                     * box of each result and then zoom the map to the merged one.
                     */
                    if (item.getResultType() == DiscoveryResult.ResultType.PLACE) {
                        PlaceLink placeLink = (PlaceLink) item;
                        addMarkerAtPlace(placeLink);
                    }
                }
            } else {
                Toast.makeText(m_activity,
                        "ERROR:Discovery search request returned return error code+ " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void addMarkerAtPlace(PlaceLink placeLink) {
        Image img = new Image();
        try {
            img.setImageResource(R.drawable.marker);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapMarker mapMarker = new MapMarker();
        mapMarker.setIcon(img);
        mapMarker.setCoordinate(new GeoCoordinate(placeLink.getPosition()));
        m_map.addMapObject(mapMarker);
        m_mapObjectList.add(mapMarker);
    }

    public MapMarker FindObject(MapMarker marker){

        if (!m_mapObjectList_new.isEmpty()) {
            m_map.removeMapObjects(m_mapObjectList_new);
            m_mapObjectList_new.clear();
        }

        MapMarker ret = null;
        for (int i = 0; i < m_mapObjectList.size(); i++)
        {
            if (m_mapObjectList.get(i).hashCode() == marker.hashCode()) {
                ret = (MapMarker) m_mapObjectList.get(i);
                m_mapObjectList_new.add(ret);
            }
        }
        return ret;
    }

    private void cleanMap() {
        if (!m_mapObjectList.isEmpty()) {
            m_map.removeMapObjects(m_mapObjectList);
            m_mapObjectList.clear();
        }
        m_placeDetailButton.setVisibility(View.GONE);
    }
}