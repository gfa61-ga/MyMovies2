package com.example.mymovies.utils;

import android.content.Context;
import android.util.DisplayMetrics;

// https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
public class LayoutUtils {
    public static int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    public static int calculateNoOfTrailerColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics.widthPixels<displayMetrics.heightPixels) {
            return 1; // Screen in portrait mode
        } else
        {
            return 2; // Screen in landscape mode
        }
    }
}
