package nsb.widget.news.preference;

import java.util.Date;

import nsb.widget.news.Constants;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class PreferenceUtils
{
    private static final String LAST_WIDGET_UPDATE_PREFIX = "LAST_WIDGET_UPDATE_"; //$NON-NLS-1$
    public static final String REFRESH_INTERVAL = "refresh_interval_in_minutes"; //$NON-NLS-1$
    private static final String DEBUG_MODE = "debug_mode"; //$NON-NLS-1$
        
    private static final String DEFAULT_REFRESH_INTERVAL = "180"; //$NON-NLS-1$
    private static final String DEFAULT_EMBEDDED_IMAGE_MIN_WIDTH = "240"; //$NON-NLS-1$
    
    
    public static int getRefreshInterval(Context context)
    {
        return getInteger(context, REFRESH_INTERVAL, DEFAULT_REFRESH_INTERVAL);
    }
    
    public static void setRefreshInterval(Context context, int interval)
    {
        setInteger(context, REFRESH_INTERVAL, interval);
    }
    
    public static boolean isDebugModeEnabled(Context context)
    {
        return getBoolean(context, DEBUG_MODE, false);
    }
    
    public static void enableDebugMode(Context context, boolean val)
    {
        setBoolean(context, DEBUG_MODE, val);
    }   

    private static void preferenceUnavailable(String methodName)
    {
        Log.w(Constants.PACKAGE, "[" + methodName + "] Cannot access preference store: passed context is null."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue)
    {
        if ( context == null )
        {
            preferenceUnavailable(key);
            return defaultValue;
        }
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defaultValue);
    }
    
    public static void setBoolean(Context context, String key, boolean value)
    {
        if ( context == null )
        {
            preferenceUnavailable(key);
            return;
        }
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    private static int getInteger(Context context, String key, String defaultValue)
    {
        if ( context == null )
        {
            preferenceUnavailable(key);
            return Integer.parseInt(defaultValue);
        }
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String val = prefs.getString(key, defaultValue);
        
        if ( TextUtils.isEmpty(val) )
        {
            return Integer.parseInt(defaultValue);
        }
      
        try
        {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e)
        {
            return Integer.parseInt(defaultValue);
        }
    }
    
    private static void setInteger(Context context, String key, int value)
    {
        if ( context == null )
        {
            preferenceUnavailable(key);
            return;
        }
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putString(key, new Integer(value).toString());
        edit.commit();
    }


    public static Date getLastUpdateDate(Context context, int appWidgetId)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long l = prefs.getLong(LAST_WIDGET_UPDATE_PREFIX + appWidgetId, -1);
        
        if ( l == -1 ) 
        {
            return null;
        }
        
        return new Date(l);
    }

    public static void setLastUpdateDate(Context context, int appWidgetId, Date value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putLong(LAST_WIDGET_UPDATE_PREFIX + appWidgetId, value.getTime());
        edit.commit();
    }

}
