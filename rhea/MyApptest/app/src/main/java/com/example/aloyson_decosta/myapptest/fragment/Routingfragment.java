package com.example.aloyson_decosta.myapptest.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aloyson_decosta.myapptest.AutoComplete.CustomAutoCompleteTextView;
import com.example.aloyson_decosta.myapptest.AutoComplete.GeocompleteAdapter;
import com.example.aloyson_decosta.myapptest.MapFragmentView;
import com.example.aloyson_decosta.myapptest.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.GeocodeRequest;
import com.here.android.mpa.search.Location;
import com.here.android.mpa.search.ResultListener;

import java.io.IOException;
import java.util.List;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link Routingfragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link Routingfragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class Routingfragment extends Fragment {

    private static final Integer THRESHOLD = 2;
    private CustomAutoCompleteTextView mGeoAutocomplete;
    private GeocompleteAdapter mGeoAutoCompleteAdapter;
    private CustomAutoCompleteTextView mGeoAutocomplete2;
    private GeocompleteAdapter mGeoAutoCompleteAdapter2;
    private GeoCoordinate geoCoordinateS;
    private GeoCoordinate geoCoordinate2;
    private MapMarker mMarker;
    private MapMarker mMarker2;
    private Map mMap;
    private  Button m_Route2;
    private  Button startNavigation;
    private MapFragmentView m_mapFragmentView;
    private MapFragment mMapFragment;

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

    public Routingfragment() {
        // Required empty public constructor

    }

//    // TODO: Rename and change types and number of parameters
//    public static Routingfragment newInstance(String param1, String param2) {
//        Routingfragment fragment = new Routingfragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_routingfragment, container,
                false);

      //  m_mapFragmentView = new MapFragmentViewSearch(getActivity(),null);

       // mMap=m_mapFragmentView.drawMap();
        //mMapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapfragment1);
       // mMap=mMapFragment.getMap();

        // Get position of argument
        //String name = getArguments().getString("name");
//
        //TextView textView = (TextView) rootView.findViewById(R.id.textView);
        // getActivity().setTitle(name);
        m_Route2 = (Button) rootView.findViewById(R.id.button2);
        startNavigation = (Button)rootView.findViewById(R.id.button);

        getSource(rootView);
        //Destination
        getDestination(rootView);


        return rootView;
    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    //source AutoComplete
    private void getSource(final View rootView){
        mGeoAutocomplete = (CustomAutoCompleteTextView) rootView.findViewById(R.id.geo_autocomplete);
        mGeoAutocomplete.setThreshold(THRESHOLD);
        // mGeoAutocomplete.setLoadingIndicator((android.widget.ProgressBar) rootView
        //        .findViewById(R.id.pb_loading_indicator));

        mGeoAutoCompleteAdapter = new GeocompleteAdapter(rootView.getContext());
        mGeoAutocomplete.setAdapter(mGeoAutoCompleteAdapter);

        mGeoAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //for route button
                if(m_Route2.getVisibility()==View.INVISIBLE){
                    startNavigation.setVisibility(View.INVISIBLE);
                    m_Route2.setVisibility(View.VISIBLE);
                }

//                //for autohiding keyboard on click
//                if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus() instanceof EditText){
//                    InputMethodManager imm = (InputMethodManager)m_activity.getSystemService(m_activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(m_activity.getWindow().getCurrentFocus().getWindowToken(), 0);
//                }

                String result = (String) adapterView.getItemAtPosition(position);
                mGeoAutocomplete.setText(result);
                GeocodeRequest req = new GeocodeRequest(result);
                req.setSearchArea(mMap.getBoundingBox());
                //stops navigating
                NavigationManager.getInstance().setMap(mMap);
                if (NavigationManager.getInstance().getRunningState() == NavigationManager.NavigationState.RUNNING) {
                   // mMapFragment.getPositionIndicator().setVisible(false);
                    NavigationManager.getInstance().stop();
                }

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
                   // mMap=mapFragmentView.drawMap();
                    req.setSearchArea(mMap.getBoundingBox());
                    req.execute(m_listener);
                }
            }
        });


    }

    //Destination AutoComplete
    private void getDestination(View rootView){
        mGeoAutocomplete2 = (CustomAutoCompleteTextView) rootView.findViewById(R.id.geo_autocomplete2);
        mGeoAutocomplete2.setThreshold(THRESHOLD);
        // mGeoAutocomplete.setLoadingIndicator((android.widget.ProgressBar) rootView
        //        .findViewById(R.id.pb_loading_indicator));

        mGeoAutoCompleteAdapter2 = new GeocompleteAdapter(rootView.getContext());
        mGeoAutocomplete2.setAdapter(mGeoAutoCompleteAdapter2);

        mGeoAutocomplete2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //for route button
                if(m_Route2.getVisibility()==View.INVISIBLE){
                    startNavigation.setVisibility(View.INVISIBLE);
                    m_Route2.setVisibility(View.VISIBLE);
                }
//                //for autohiding keyboard on click
//                if(m_activity.getCurrentFocus()!=null && m_activity.getCurrentFocus() instanceof EditText){
//                    InputMethodManager imm = (InputMethodManager)m_activity.getSystemService(m_activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(m_activity.getWindow().getCurrentFocus().getWindowToken(), 0);
//                }

                String result = (String) adapterView.getItemAtPosition(position);
                mGeoAutocomplete2.setText(result);
                GeocodeRequest req = new GeocodeRequest(result);
                //mMap=mapFragmentView.drawMap();
                req.setSearchArea(mMap.getBoundingBox());
                //stops navigating
                NavigationManager.getInstance().setMap(mMap);
                if (NavigationManager.getInstance().getRunningState() == NavigationManager.NavigationState.RUNNING) {
                  //  mMapFragment.getPositionIndicator().setVisible(false);
                    NavigationManager.getInstance().stop();
                }

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
                  //  mMap=mapFragmentView.drawMap();
                    req.setSearchArea(mMap.getBoundingBox());
                    req.execute(m_listener2);
                }
            }
        });
    }

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
        mMap.setCenter(geoCoordinate, Map.Animation.BOW);
    }

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
