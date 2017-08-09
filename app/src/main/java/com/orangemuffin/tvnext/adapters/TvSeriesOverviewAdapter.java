package com.orangemuffin.tvnext.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.orangemuffin.tvnext.R;
import com.orangemuffin.tvnext.models.Actor;
import com.orangemuffin.tvnext.models.Overview;
import com.orangemuffin.tvnext.utils.MeasurementUtil;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/* Created by OrangeMuffin on 8/1/2017 */
public class TvSeriesOverviewAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<Overview> data;
    private Context context;

    public TvSeriesOverviewAdapter(Context context, List<Overview> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view == null) {
            view = inflater.inflate(R.layout.stickydetails_header, viewGroup, false);
            holder = new HeaderViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        holder.header.setText(data.get(i).getHeader());

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return data.get(i).getId();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.stickyoverview_row, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.sliderLayout.setVisibility(View.GONE);
        holder.pagerIndicator.setVisibility(View.GONE);
        holder.grid.setVisibility(View.GONE);
        holder.text.setVisibility(View.GONE);

        if (i == 0) {
            holder.sliderLayout.setVisibility(View.VISIBLE);
            holder.pagerIndicator.setVisibility(View.VISIBLE);
        } else if (i == 5) {
            List<Actor> actors = data.get(i).getActors();
            int size = Math.min(actors.size(), 8);
            String[] names = new String[size];
            String[] roles = new String[size];
            String[] images = new String[size];

            for (int j = 0; j < size; j++) {
                Actor actor = actors.get(j);
                names[j] = actor.getName();
                roles[j] = actor.getRole();
                images[j] = actor.getImage();
            }

            holder.grid.setAdapter(new CastDetailsGridAdapter(context, names, roles, images));
            holder.grid.setVisibility(View.VISIBLE);
        } else {
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private class ViewHolder {
        private TextView text;
        private GridView grid;

        private SliderLayout sliderLayout;
        private PagerIndicator pagerIndicator;

        public ViewHolder(View v) {
            text = (TextView) v.findViewById(R.id.element_text);
            grid = (GridView) v.findViewById(R.id.grid);

            sliderLayout = (SliderLayout) v.findViewById(R.id.slider);
            pagerIndicator = (PagerIndicator) v.findViewById(R.id.custom_indicator2);
            pagerIndicator.setBackgroundColor(Color.TRANSPARENT);
            sliderLayout.stopAutoCycle();
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
            sliderLayout.setCustomIndicator(pagerIndicator);
            sliderLayout.requestLayout();

            SharedPreferences sp_data = context.getSharedPreferences("PHONEDATA", context.MODE_PRIVATE);
            int width = sp_data.getInt("PHONE_RES", 1080);

            sliderLayout.getLayoutParams().height = (int) ((width - (2 * MeasurementUtil.dpToPixel(19))) * 1080 / 1920);

            for (int j = 0; j < data.get(0).getBackgrounds().size(); j++) {
                DefaultSliderView sliderView = new DefaultSliderView(context);
                sliderView.image(data.get(0).getBackgrounds().get(j));
                sliderLayout.addSlider(sliderView);
            }
        }
    }

    private class HeaderViewHolder {
        private TextView header;

        public HeaderViewHolder(View v) {
            header = (TextView) v.findViewById(R.id.header_text);
        }
    }
}
