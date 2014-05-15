package fr.gdi.android.news.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import fr.gdi.android.news.preference.utils.PreferenceUtils;
import fr.gdi.android.news.receiver.NewsWidgetProvider;

public class DeviceUtils
{
    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null || cm.getActiveNetworkInfo() == null)
        {
            return false;
        }
        
        return cm.getActiveNetworkInfo().isConnected();
    }
    
    public static boolean canAccessNetwork(Context context) 
    {
        return isOnline(context);
    }
    
    public static int[] getAllWidgetIds(Context context)
    {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        return awm.getAppWidgetIds(new ComponentName(context, NewsWidgetProvider.class));
    }
}
