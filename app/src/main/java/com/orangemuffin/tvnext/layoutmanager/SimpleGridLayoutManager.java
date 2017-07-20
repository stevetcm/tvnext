package com.orangemuffin.tvnext.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/* Created by OrangeMuffin on 1/24/2017 */
public class SimpleGridLayoutManager extends GridLayoutManager {
    //manager to re-activate animation on recyclerview items
    public SimpleGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}