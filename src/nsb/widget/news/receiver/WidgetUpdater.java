package nsb.widget.news.receiver;

import nsb.widget.news.data.dao.ConfigurationDao;
import nsb.widget.news.data.dao.DaoUtils;
import nsb.widget.news.utils.DeviceUtils;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class WidgetUpdater extends BroadcastReceiver
{
    public static final String ACTION_UPDATE = "nsb.widget.news.ACTION_UPDATE"; //$NON-NLS-1$

    public static final String FORCE_REFRESH = "force"; //$NON-NLS-1$
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (TextUtils.equals(ACTION_UPDATE, intent.getAction()) && DeviceUtils.canAccessNetwork(context))
        {
            int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            int[] ids = null;
            if ( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID )
            {
                ConfigurationDao dao = DaoUtils.getConfigurationDao(context);
                ids = dao.getConfiguredWidgetIds(); 
            }
            else  
            {
                ids = new int[] { appWidgetId };
            }
            
            boolean force = intent.getBooleanExtra(FORCE_REFRESH, false);
            
            WidgetReloadTask loadTask = new WidgetReloadTask(context, ids, force);
            loadTask.execute();
        }
    }
    
}
