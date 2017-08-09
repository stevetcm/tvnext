package com.orangemuffin.tvnext.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/* Created by OrangeMuffin on 7/28/2017 */
public class MeasurementUtil {
    public static float dpToPixel(int dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static void setPadding(View view, int left, int top, int right, int bottom) {
        view.setPadding((int)dpToPixel(left), (int)dpToPixel(top), (int)dpToPixel(right), (int)dpToPixel(bottom));
    }

    public static void setMargin(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins((int)dpToPixel(left), (int)dpToPixel(top), (int)dpToPixel(right), (int)dpToPixel(bottom));
            view.requestLayout();
        }
    }
}
