package com.orangemuffin.tvnext.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;
import com.squareup.picasso.Picasso;

/* Created by OrangeMuffin on 8/1/2017 */
public class CastDetailsGridAdapter extends BaseAdapter {
    private Context context;
    private final String[] names;
    private final String[] roles;
    private final String[] images;

    public CastDetailsGridAdapter(Context context,String[] names, String[] roles, String[] images ) {
        this.context = context;
        this.names = names;
        this.roles = roles;
        this.images = images;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(R.layout.cast_grid_element, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_name);
            TextView textView2 = (TextView) grid.findViewById(R.id.grid_role);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            textView.setText(names[position]);
            textView2.setText(roles[position]);
            String urlActor = images[position];

            if (!urlActor.equals("http://thetvdb.com/banners/_cache/")) {
                Picasso.with(context).load(urlActor).noFade().fit().centerCrop().into(imageView);
            } else {
                Picasso.with(context).load(R.drawable.placeholder_poster).noFade().fit().centerCrop().into(imageView);
            }
        } else {
            grid = convertView;
        }

        return grid;
    }
}
