package com.example.aloyson_decosta.myapptest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aloyson_decosta.myapptest.R;
import com.example.aloyson_decosta.myapptest.model.ItemSlideMenu;

import java.util.List;

/**
 * Created by aloyson_decosta on 18-09-2017.
 */

public class SlidingMenuAdapter extends BaseAdapter {

    private Context context;
    private List<ItemSlideMenu> lstItem;

    public SlidingMenuAdapter(Context context, List<ItemSlideMenu> lstItem) {
        this.context = context;
        this.lstItem = lstItem;
    }

    @Override
    public int getCount() {
        return lstItem.size();
    }

    @Override
    public Object getItem(int i) {
        return lstItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v=View.inflate(context, R.layout.drawer_list_item,null);
     //   ImageView img=(ImageView)v.findViewById(R.id.item_img);  //for image
        TextView tv=(TextView)v.findViewById(R.id.item_title);

        ItemSlideMenu item=lstItem.get(i);
        tv.setText(item.getTitle());
        return v;
    }
}
