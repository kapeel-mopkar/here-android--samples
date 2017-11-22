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

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.aloyson_decosta.myapptest.MainActivity;
import com.example.aloyson_decosta.myapptest.R;
import com.example.aloyson_decosta.myapptest.adapter.SlidingMenuAdapter;
import com.example.aloyson_decosta.myapptest.model.ItemSlideMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivitySearch extends AppCompatActivity {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private MapFragmentViewSearch m_mapFragmentView;
    private List<ItemSlideMenu> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private FrameLayout mainContent;
    private ActionBarDrawerToggle drawerToggle;
    private MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Init comp
        listViewSliding=(ListView)findViewById(R.id.drawerList);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        mainContent=(FrameLayout)findViewById(R.id.frameContainer);
        listSliding=new ArrayList<>();
        //Add item item for sliding
        listSliding.add(new ItemSlideMenu("Routing"));
        listSliding.add(new ItemSlideMenu("Navigation"));
        listSliding.add(new ItemSlideMenu("Satelite"));
        listSliding.add(new ItemSlideMenu("Terrain"));
        listSliding.add(new ItemSlideMenu("3-D Venue"));
        listSliding.add(new ItemSlideMenu("Find Places"));

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
                main.replaceFragment(position);
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

        m_mapFragmentView = new MapFragmentViewSearch(this);
    }

}
