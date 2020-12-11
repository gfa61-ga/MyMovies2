package com.example.mymovies.utils;

import android.content.Context;
import android.util.DisplayMetrics;

// https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
public class Utility {
    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }
}
