package com.example.aloyson_decosta.myapptest.Routing_Navigation;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aloyson_decosta.myapptest.R;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.guidance.VoiceSkin;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteTta;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

/**
 * Created by aloyson_decosta on 31-08-2017.
 */

public class Navigation {

    private Activity m_activity;
    private MapRoute m_mapRoute;
    private Map map;
    private final int EN_US_ID = 206;
    private Button startNavigation;
    private MapFragment mMapFragment;
    private TextView textViewNav;
    private TextView textViewFixed;
    Date current;
    Date dateval;
    long dateDiffBetman;
    long datestored;
    long timeleft;
    long hours;
    long min;
    RouteTta rrta;



    public Navigation(Activity activity){
        m_activity=activity;
        textViewNav = (TextView) m_activity.findViewById(R.id.textViewNavigate);
        textViewFixed = (TextView) m_activity.findViewById(R.id.textView);
        current=new Date();
    }

    // listen for new instruction events
    public NavigationManager.NewInstructionEventListener instructionHandler = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {
            Maneuver m = NavigationManager.getInstance().getNextManeuver();

            // Log.i(TAG, "New instruction : in " + NavigationManager.getInstance().getNextManeuverDistance() + " m do " + m.getTurn().name() + " / " + m.getAction().name() + " on " + m.getNextRoadName() + " (from " + m.getRoadName() + ")");
            // ...
            // do something with this information, e.g. show it to the user

            //distance left=route_dist-next_man_dist_from_start+dist_bet_current_next_man
            float distanceleft=m_mapRoute.getRoute().getLength()-m.getDistanceFromStart()+m.getDistanceFromPreviousManeuver();
                 distanceleft= Math.round(distanceleft/10);
            distanceleft/=100;

            //cal time remaining
            dateval=m.getStartTime();
           dateDiffBetman= dateval.getTime()-current.getTime();//in millisec
            datestored+= dateDiffBetman/1000;
            current=dateval;

            rrta=m_mapRoute.getRoute().getTta(Route.TrafficPenaltyMode.OPTIMAL, Route.WHOLE_ROUTE);
            int getTimesec=rrta.getDuration();
            timeleft=getTimesec-datestored;
            hours=timeleft/3600;
            min=(timeleft-hours*3600)/60;
            String Time="";

            if(hours==0)
                Time=min+" mins ";
            else if(min==0)
                Time=timeleft+" secs";
                else
                Time=hours+" hrs "+min+" mins";


          //  if(!textViewNav.getText().toString().matches(""))
        //        textViewNav.setText("");
            textViewNav.setText("dist-Left:"+distanceleft+" km\nEstimated Time:"+Time);

            if(textViewNav.getVisibility()== View.GONE)
            textViewNav.setVisibility(View.VISIBLE);
          //  Toast.makeText(m_activity.getApplicationContext(),"dist-Left:"+distanceleft+" Time: "+datestored,Toast.LENGTH_SHORT).show();

            super.onNewInstructionEvent();

        }

    };


//    public void startNavigationTrack(Map m_Map,MapRoute mapRoute){
//        map=m_Map;
//        m_mapRoute=mapRoute;
//        startNavigation = (Button)m_activity.findViewById(R.id.button);
//
//        startNavigation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                    if (startNavigation.getText().equals(getText(R.string.navigation_start))) {
////                        startNavigation.setText(R.string.navigation_stop);
////                    } else if (startNavigation.getText().equals(getText(R.string.navigation_stop))) {
////                        startNavigation.setText(R.string.navigation_start);
////                    }
//                startGuidance();
//            }
//        });
//    }


    public void startGuidance(Map m_Map, MapRoute mapRoute) {
        map=m_Map;
        m_mapRoute=mapRoute;
        mMapFragment = (MapFragment) m_activity.getFragmentManager().findFragmentById(R.id.mapfragment);
        //hide dist n tym
        if(textViewFixed.getVisibility()== View.VISIBLE)
        textViewFixed.setVisibility(View.GONE);
        //get voice
        downloadCatalogAndSkin();
        // better visuals when switching to special car navigation map scheme
        NavigationManager navMgr = NavigationManager.getInstance();

        navMgr.setMap(map);
        map.setMapScheme(Map.Scheme.CARNAV_DAY);
        map.setTilt(45);
        mMapFragment.getPositionIndicator().setVisible(true);
        mMapFragment.getPositionIndicator().setZIndex(0);

        map.setZoomLevel(18);
        //       mMapFragment.getPositionIndicator().setAccuracyIndicatorVisible(true);


        // set guidance view to position with road ahead, tilt and zoomlevel was setup before manually
        // choose other update modes for different position and zoom behavior

        navMgr.setMapUpdateMode(NavigationManager.MapUpdateMode.POSITION);

        // get new guidance instructions
        navMgr.addNewInstructionEventListener(new WeakReference<>(instructionHandler));

        // set usage of Nuance TTS engine if specified
//        if (useNuance && nuance.isInitialized()) {
//            NavigationManager.getInstance().getAudioPlayer().setDelegate(player);
//        } else {
        // passing null delete any custom audio player that was set earlier
        navMgr.getAudioPlayer().setDelegate(null);
        // }

        // start simulation with speed of 10 m/s
        NavigationManager.Error e =navMgr.simulate(m_mapRoute.getRoute(), 10);



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
   //                 Toast.makeText(m_activity.getApplicationContext(), "Catalog downloaded", Toast.LENGTH_LONG).show();

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
