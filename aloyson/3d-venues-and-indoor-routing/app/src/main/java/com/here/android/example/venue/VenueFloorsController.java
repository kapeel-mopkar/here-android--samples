package com.here.android.example.venue;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.here.android.mpa.venues3d.DeselectionSource;
import com.here.android.mpa.venues3d.Level;
import com.here.android.mpa.venues3d.Space;
import com.here.android.mpa.venues3d.Venue;
import com.here.android.mpa.venues3d.VenueController;
import com.here.android.mpa.venues3d.VenueMapFragment;

/**
 * Created by aloyson_decosta on 04-09-2017.
 */

public class VenueFloorsController implements AdapterView.OnItemClickListener, VenueMapFragment.VenueListener {
    private Context m_activity = null;
    private VenueMapFragment m_venueMapFragment = null;
    private ListView m_floorListView = null;
    private final int m_floorItem;
    private final int m_floorName;
    private final int m_floorGroundSep;
    private VenueMapFragment m_mapFragment = null;

    public VenueFloorsController(Context context, VenueMapFragment venueLayer,
                                 ListView listView, int floorItemId, int floorNameId, int floorGroundSepId) {
        m_activity = context;
        m_floorItem = floorItemId;
        m_floorName = floorNameId;
        m_floorGroundSep = floorGroundSepId;

        m_venueMapFragment = venueLayer;
        m_venueMapFragment.addListener(this);

        m_floorListView = listView;
        m_floorListView.setOnItemClickListener(this);

        m_mapFragment = (VenueMapFragment)venueLayer.getFragmentManager().findFragmentById(R.id.map_fragment);

        if (m_venueMapFragment.getSelectedVenue() != null) {
            onVenueSelected(m_venueMapFragment.getSelectedVenue());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
        view.setSelected(true);
        VenueController controller = m_mapFragment
                .getVenueController(m_venueMapFragment.getSelectedVenue());
        if (controller != null) {
            int levelIndex = controller.getVenue().getLevels().size() - 1 - index;
            Level level = controller.getVenue().getLevels().get(levelIndex);
            controller.selectLevel(level);
        }
    }

    @Override
    public void onVenueSelected(Venue venue) {
        m_floorListView.setAdapter(new VenueFloorAdapter(m_activity, venue.getLevels(),
                m_floorItem, m_floorName, m_floorGroundSep));
        updateSelectedLevel(m_mapFragment.getVenueController(venue));

        m_floorListView.setVisibility(View.VISIBLE);
    }

    private void updateSelectedLevel(VenueController controller) {
        Level selectedLevel = controller.getSelectedLevel();
        if (selectedLevel != null) {
            int pos = ((VenueFloorAdapter) m_floorListView.getAdapter())
                    .getLevelIndex(selectedLevel);
            if (pos != -1) {
                m_floorListView.setSelection(pos);
                m_floorListView.smoothScrollToPosition(pos);
                m_floorListView.setItemChecked(pos, true);
            }
        }
    }

    @Override
    public void onVenueDeselected(Venue venue, DeselectionSource source) {
        m_floorListView.setAdapter(null);
        m_floorListView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFloorChanged(Venue venue, Level oldLevel, Level newLevel) {
        updateSelectedLevel(m_mapFragment.getVenueController(venue));
    }

    @Override
    public void onVenueTapped(Venue venue, float x, float y) {
    }

    @Override
    public void onSpaceSelected(Venue venue, Space space) {
    }

    @Override
    public void onSpaceDeselected(Venue venue, Space space) {
    }

    @Override
    public void onVenueVisibleInViewport(Venue venue, boolean visible) {
    }

}
