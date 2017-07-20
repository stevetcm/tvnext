package com.orangemuffin.tvnext.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/* Created by OrangeMuffin on 2/6/2017 */
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
            view = inflater.inflate(R.layout.stickyoverview_header, viewGroup, false);
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
        return data.get(i).getOrder();
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
        holder.text2.setVisibility(View.GONE);

        if (i == 0) {
            holder.sliderLayout.setPadding((int) dpToPixel(2), 0, (int) dpToPixel(2), (int) dpToPixel(2));
            holder.sliderLayout.setVisibility(View.VISIBLE);
            holder.pagerIndicator.setVisibility(View.VISIBLE);
        } else if (i == 1) {
            holder.text.setPadding((int) dpToPixel(5), (int) dpToPixel(2), 0, (int) dpToPixel(5));
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
            holder.text2.setPadding((int) dpToPixel(5), 0, 0, (int) dpToPixel(5));
            holder.text2.setText(data.get(i).getText2());
            if (!data.get(i).getText().equals("No Upcoming Episode")) {
                holder.text.setPadding((int) dpToPixel(5), (int) dpToPixel(2), 0, 0);
                holder.text2.setVisibility(View.VISIBLE);
            }
        } else if (i == 2) {
            holder.text.setPadding((int) dpToPixel(7), (int) dpToPixel(2), (int) dpToPixel(5), (int) dpToPixel(5));
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
        } else if (i == 3) {
            holder.text.setPadding((int) dpToPixel(5), (int) dpToPixel(2), 0, (int) dpToPixel(5));
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
        } else if (i == 4) {
            holder.text.setPadding((int) dpToPixel(7), (int) dpToPixel(2), (int) dpToPixel(5), (int) dpToPixel(5));
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
        } else if (i == 5) {
            List<Actor> actors = data.get(i).getActors();
            Collections.sort(actors, new Comparator<Actor>() {
                @Override
                public int compare(Actor actor, Actor actor2) {
                    return actor.getSortOrder() - actor2.getSortOrder();
                }
            });
            String[] names, roles, images;
            if (actors.size() > 8) {
                names = new String[8];
                roles = new String[8];
                images = new String[8];

                for (int j = 0; j < 8; j++) {
                    names[j] = actors.get(j).getName();
                    roles[j] = "as " + actors.get(j).getRole();
                    images[j] = actors.get(j).getImage();
                }
            } else {
                names = new String[actors.size()];
                roles = new String[actors.size()];
                images = new String[actors.size()];

                for (int j = 0; j < actors.size(); j++) {
                    names[j] = actors.get(j).getName();
                    roles[j] = "as " + actors.get(j).getRole();
                    images[j] = actors.get(j).getImage();
                }
            }

            CastDetailsGridAdapter adapter = new CastDetailsGridAdapter(context, names, roles, images);
            holder.grid.setAdapter(adapter);
            holder.grid.setVisibility(View.VISIBLE);
        } else if (i == 6) {
            holder.text.setPadding((int) dpToPixel(5), (int) dpToPixel(2), 0, (int) dpToPixel(5));
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
        } else if (i == 7) {
            holder.text.setPadding((int) dpToPixel(5), (int) dpToPixel(2), 0, (int) dpToPixel(5));
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
        } else if (i == 8) {
            holder.text.setPadding((int) dpToPixel(5), (int) dpToPixel(2), 0, (int) dpToPixel(5));
            holder.text.setText(data.get(i).getText());
            holder.text.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private class ViewHolder {
        private TextView text;
        private TextView text2;
        private GridView grid;

        private SliderLayout sliderLayout;
        private PagerIndicator pagerIndicator;

        public ViewHolder(View v) {
            text = (TextView) v.findViewById(R.id.element_text);
            text2 = (TextView) v.findViewById(R.id.element_text2);
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

            sliderLayout.getLayoutParams().height = (int) ((width - (2 * dpToPixel(19))) * 1080 / 1920);

            for (int j = 0; j < data.get(0).getFanarts().size(); j++) {
                DefaultSliderView sliderView = new DefaultSliderView(context);
                sliderView.image(data.get(0).getFanarts().get(j));
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

    public float dpToPixel(int dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
