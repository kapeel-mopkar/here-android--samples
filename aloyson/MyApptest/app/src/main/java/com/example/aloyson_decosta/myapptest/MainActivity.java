package com.example.aloyson_decosta.myapptest;

import android.Manifest;
import android.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aloyson_decosta.myapptest.adapter.SlidingMenuAdapter;
import com.example.aloyson_decosta.myapptest.fragment.Routingfragment;
import com.example.aloyson_decosta.myapptest.fragment.VersionFragment;
import com.example.aloyson_decosta.myapptest.model.ItemSlideMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private MapFragmentView m_mapFragmentView ;
    private  String scheme;
    private boolean hide=false;


    private List<ItemSlideMenu> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private FrameLayout mainContent;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //Init comp
        listViewSliding=(ListView)findViewById(R.id.drawerList);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        mainContent=(FrameLayout)findViewById(R.id.frameContainer);
        listSliding=new ArrayList<>();
        //Add item item for sliding
        listSliding.add(new ItemSlideMenu("Routing"));
        listSliding.add(new ItemSlideMenu("Satelite"));
        listSliding.add(new ItemSlideMenu("Terrain"));
        adapter=new SlidingMenuAdapter(this,listSliding);
        listViewSliding.setAdapter(adapter);

        //Display icon to open/close
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set Title
        setTitle("HERE MAPS");
        //item selected
        listViewSliding.setItemChecked(0,true);
        //close menu
        drawerLayout.closeDrawer(listViewSliding);

        //Display frag:wen start
        //replaceFragment(0);
        requestPermissions();


        //Handle on item Click
        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //set title
                setTitle(listSliding.get(position).getTitle());
                //item selected
                listViewSliding.setItemChecked(position,true);
                //Replace frag
                replaceFragment(position);
                //close menu
                drawerLayout.closeDrawer(listViewSliding);
            }
        });

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    //for menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        if(hide)
            menu.setGroupVisible(R.id.group1, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        int id=item.getItemId();
        if(id==R.id.toll || id==R.id.ferry)
            item.setChecked(!item.isChecked());

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    //create method replace fragment
    private  void replaceFragment(int pos) {
        Fragment fragment=null;
//        SateliteFragment fragment = null;
        RoutingController routingController=null;
        switch (pos) {
            case 0:
                hide=true;
                LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflator.inflate(R.layout.fragment_routingfragment, null);
//                if(v.isShown())
//                    mainContent.removeView(v);
                mainContent.addView(v);
                routingController=new RoutingController(this);
               // for Routing menu

                break;
            case 1:
               scheme="Satelite";
                break;
            case 2:
                scheme="Terrain";
                break;
            default:
                scheme="Normal";
                break;
        }


        m_mapFragmentView = new MapFragmentView(this,scheme);
    }



    //Permission Method
    private void requestPermissions() {

        final List<String> requiredSDKPermissions = new ArrayList<String>();
        requiredSDKPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredSDKPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredSDKPermissions.add(Manifest.permission.INTERNET);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        ActivityCompat.requestPermissions(this,
                requiredSDKPermissions.toArray(new String[requiredSDKPermissions.size()]),
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    //Permission Listeners
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /**
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permissions[index])) {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /**
                 * All permission requests are being handled.Create map fragment view.Please note
                 * the HERE SDK requires all permissions defined above to operate properly.
                 */
                 m_mapFragmentView = new MapFragmentView(this,scheme);
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
