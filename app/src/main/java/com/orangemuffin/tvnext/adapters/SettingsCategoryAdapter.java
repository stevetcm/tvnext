package com.orangemuffin.tvnext.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orangemuffin.tvnext.R;

/* Created by OrangeMuffin on 5/3/2017 */
public class SettingsCategoryAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private Context context;
    private String[] settingsCategory;
    private String[] settingsDetails;

    public SettingsCategoryAdapter(String[] settingsCategory, String[] settingsDetails, Context context) {
        this.settingsCategory = settingsCategory;
        this.settingsDetails = settingsDetails;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return settingsCategory.length;
    }

    @Override
    public Object getItem(int i) {
        return settingsCategory[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder {
        TextView settingName;
        TextView settingDetails;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder = new Holder();
        View rootView = inflater.inflate(R.layout.settingslist_element, null);
        holder.settingName = (TextView) rootView.findViewById(R.id.settingslist_name);
        holder.settingDetails = (TextView) rootView.findViewById(R.id.settingslist_details);

        holder.settingDetails.setVisibility(View.GONE);
        holder.settingName.setText(settingsCategory[i]);
        holder.settingName.setPadding(0, 0, 0, (int)dpToPixel(10));
        //holder.settingDetails.setText(settingsDetails[i]);

        return rootView;
    }

    public float dpToPixel(int dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
