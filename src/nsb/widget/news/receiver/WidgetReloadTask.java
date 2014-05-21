/**
 * 
 */
package nsb.widget.news.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nsb.widget.news.data.dao.ConfigurationDao;
import nsb.widget.news.data.dao.DaoUtils;
import nsb.widget.news.model.Behaviour;
import nsb.widget.news.model.Configuration;
import nsb.widget.news.model.Feed;
import nsb.widget.news.utils.DeviceUtils;
import nsb.widget.news.utils.feed.load.FeedLoader;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class WidgetReloadTask extends AsyncTask<Void, Void, List<Feed>>
{
    private FeedLoader loader;
    
    Context context;

    int[] appWidgetIds; 
    
    private boolean force = false;
    
    public WidgetReloadTask(Context context, int[] appWidgetIds, boolean force)
    {
        this.context = context;
        this.appWidgetIds = appWidgetIds;
        this.loader = new FeedLoader();
        this.force = force;
    }
    
    @Override
    protected void onPreExecute()
    {
        Intent intent = new Intent(context, NewsWidgetProvider.class);
        intent.setAction(NewsWidgetProvider.SET_LOADING);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra(NewsWidgetProvider.LOADING_KEY, true);
        context.sendBroadcast(intent);   
    }
    
    @Override
    protected List<Feed> doInBackground(Void... args)
    {
        if ( !DeviceUtils.canAccessNetwork(context) ) 
        {
            return new ArrayList<Feed>();
        }
        
        ConfigurationDao dao = DaoUtils.getConfigurationDao(context);
        
        List<Configuration> configs = dao.getConfigurationByWidgetIds(appWidgetIds);
        
        Map<Feed, Boolean> clearFeeds = new HashMap<Feed, Boolean>();
        List<Feed> refHolder = new ArrayList<Feed>();
        
        for (Configuration configuration : configs)
        {
            List<Feed> widgetFeeds = configuration.getFeeds();

            Behaviour behaviour = configuration.getBehaviour();

            for (Feed feed : widgetFeeds)
            {
                refHolder.add(feed);                 
                clearFeeds.put(feed, behaviour.isClearBeforeLoad());
            }
            
        }
        
        List<Feed> loaded = loader.loadAndSave(context, clearFeeds, force);
        
        List<Feed> fetchThumbnails = new ArrayList<Feed>();
        
        for (Feed l : loaded)
        {
            if ( refHolder.contains(l) ) fetchThumbnails.add(l);
        }
        
        return fetchThumbnails;
    }
    
    @Override
    protected void onPostExecute(final List<Feed> result)
    {
        if ( result == null || result.size() == 0 ) 
        {
            Intent intent = new Intent(context, NewsWidgetProvider.class);
            intent.setAction(WidgetUpdater.ACTION_UPDATE);
            intent.putExtra(NewsWidgetProvider.UPDATE_DATE_KEY, true);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            context.sendBroadcast(intent);
        }
        else 
        {
            //refresh widget with new items before loading thumbnails
            Intent intent = new Intent(context, NewsWidgetProvider.class);
            intent.setAction(WidgetUpdater.ACTION_UPDATE);
            intent.putExtra(NewsWidgetProvider.UPDATE_DATE_KEY, true);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            context.sendBroadcast(intent);
            
            //load thumbnails
            new FetchThumbnailTask(this, result).execute();
        }
    }
    
}