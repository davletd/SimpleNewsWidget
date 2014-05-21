package nsb.widget.news.data.dao;

import nsb.widget.news.SimpleNewsApplication;
import nsb.widget.news.data.DatabaseHelper;
import nsb.widget.news.model.Behaviour;
import nsb.widget.news.model.Configuration;
import nsb.widget.news.model.Feed;
import nsb.widget.news.model.Item;
import nsb.widget.news.model.Theme;
import android.content.Context;

public class DaoUtils
{
    public static FeedDao getFeedDao(Context context) 
    {
        return (FeedDao) getDao(context, Feed.class);
    }
    
    
    public static ItemDao getItemDao(Context context) 
    {
        ItemDao dao = (ItemDao) getDao(context, Item.class);
        dao.setFeedDao(getFeedDao(context));
        return dao;
    }

    public static BehaviourDao getBehaviourDao(Context context)
    {
        return (BehaviourDao) getDao(context, Behaviour.class);
    }

    public static ThemeDao getThemeDao(Context context)
    {
        return (ThemeDao) getDao(context, Theme.class);
    }
    
    public static ConfigurationDao getConfigurationDao(Context context)
    {
        ConfigurationDao dao = (ConfigurationDao) getDao(context, Configuration.class);
        dao.setFeedDao(getFeedDao(context));
        dao.setThemeDao(getThemeDao(context));
        dao.setBehaviourDao(getBehaviourDao(context));
        return dao;
    }
    
    public static <T> AbstractDao<T> getDao(Context context, Class<T> c)
    {
        return ((SimpleNewsApplication) context.getApplicationContext()).getDao(c);
    }
    
    public static String getDatabasePath(Context context)
    {
        DatabaseHelper helper = ((SimpleNewsApplication) context.getApplicationContext()).getDatabaseHelper();
        return helper.getReadableDatabase().getPath();
    }
}
