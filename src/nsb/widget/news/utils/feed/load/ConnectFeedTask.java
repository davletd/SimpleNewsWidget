package nsb.widget.news.utils.feed.load;
import java.util.List;

import nsb.widget.news.Constants;
import nsb.widget.news.activity.ItemListActivity;
import nsb.widget.news.data.dao.DaoUtils;
import nsb.widget.news.data.dao.FeedDao;
import nsb.widget.news.data.dao.ItemDao;
import nsb.widget.news.fragments.item.ItemListFragment;
import nsb.widget.news.model.Feed;
import nsb.widget.news.model.Item;
import nsb.widget.news.utils.dialog.ToastUtils;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import nsb.widget.news.R;

public class ConnectFeedTask extends AsyncTask<Feed, Void, ConnectFeedResult>
{
    private Context context;
    
    private Dialog dlg;
    
    private FeedLoader loader;
    
    public ConnectFeedTask(Context context)
    {
        this.context = context;
        this.loader = new FeedLoader();
        
    }

    @Override
    protected void onPreExecute()
    {
        dlg = ProgressDialog.show(context, 
                context.getText(R.string.feed_connect_title),
                context.getText(R.string.feed_connect_message), true, false);
    }
    
    public ConnectFeedResult doInBackground(Feed... feeds)
    {
        Feed feed = feeds[0];
        
        FeedLoaderResult result;
        
        try 
        {
            //ItemDao itemDao = DaoUtils.getItemDao(context);
            //itemDao.deleteFeedItems(feeds);
            result = loader.loadFeed(context, feed);            
        }
        catch ( Exception e ) 
        {
            Log.e(Constants.PACKAGE, "Unable to connect to feed", e); //$NON-NLS-1$
            return new ConnectFeedResult(ConnectFeedResult.ERROR_CANNOT_CONNECT, feed);
        }
        
        if ( result.isSuccess() ) 
        {
            try
            {
                Feed resultFeed = result.getFeed();
                feed.setRefresh(resultFeed.getRefresh());
                feed = resultFeed;
                FeedDao feedDao = DaoUtils.getFeedDao(context);
                feedDao.updateRefreshDate(feed);
                
                List<Item> items = feed.getItems();
                ItemDao dao = DaoUtils.getItemDao(context);
                dao.save(items);
            }
            catch ( Exception e ) 
            {
                Log.e(Constants.PACKAGE, "Unable to save feed items", e); //$NON-NLS-1$
                return new ConnectFeedResult(ConnectFeedResult.ERROR_CANNOT_SAVE_ITEMS, feed);
            }            
        }
        
        return new ConnectFeedResult(ConnectFeedResult.SUCCESS, feed);
    }
    
    
    @Override
    protected void onPostExecute(ConnectFeedResult result)
    {
        dlg.dismiss();
        if ( result.getState() != ConnectFeedResult.SUCCESS ) 
        {
            ToastUtils.showError(context, result.getState() == ConnectFeedResult.ERROR_CANNOT_CONNECT ? R.string.feed_connect_error : R.string.feed_connect_save_failure);
        }
        else
        {
            Intent intent = new Intent(context, ItemListActivity.class);;
            intent.putExtra(ItemListFragment.FEED_ID_KEY, result.getFeed().getId());
            context.startActivity(intent);
        }
    }
}
