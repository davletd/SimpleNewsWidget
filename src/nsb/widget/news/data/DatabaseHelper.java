package nsb.widget.news.data;

import java.util.Date;

import nsb.widget.news.Constants;
import nsb.widget.news.data.dao.BehaviourDao;
import nsb.widget.news.data.dao.ThemeDao;
import nsb.widget.news.model.Behaviour;
import nsb.widget.news.model.Theme;
import nsb.widget.news.utils.IOUtils;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import nsb.widget.news.R;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 6;
    
    private static final String DATABASE_NAME = "nsb.widget.news.db"; //$NON-NLS-1$
    private static final String DATABASE_PATH = "database/database.ddl"; //$NON-NLS-1$
    private static final String UPDATE_FOLDER_PATH = "database/updates/"; //$NON-NLS-1$
    
    private Context context;
    
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    @Override
    public synchronized SQLiteDatabase getReadableDatabase()
    {
        SQLiteDatabase db = super.getReadableDatabase();
        db.setLockingEnabled(true);
        return db;
    }
    
    @Override
    public synchronized SQLiteDatabase getWritableDatabase()
    {
        SQLiteDatabase db = super.getWritableDatabase();
        db.setLockingEnabled(false);
        return db;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        execBatch(db, DATABASE_PATH);
        
        //insert initial sample data
        createSampleInstances(db);
    }

    private void execBatch(SQLiteDatabase database, String ddlFilePath)
    {
        String batch = IOUtils.loadTextAsset(context, ddlFilePath);
        
        try 
        {
            String[] instrs = TextUtils.split(batch, "-- break");  //$NON-NLS-1$
            for (String ddl : instrs)
            {
                ddl = ddl.trim();
                if ( !TextUtils.isEmpty(ddl) ) 
                {
                    database.execSQL(ddl);
                }
            }

            
        }
        catch ( Exception e ) 
        {
            Log.e(Constants.PACKAGE, "Unable to install database",e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
    }

    private void createSampleInstances(SQLiteDatabase db)
    {
        db.execSQL("insert into feed (title, url, type, image_url) values (?, ?, ?, ?)",  //$NON-NLS-1$
                   new Object[] { 
                        "Altran News",  //$NON-NLS-1$
                        "http://www.altran.com/rss-feed/rss/6033/rss.xml",  //$NON-NLS-1$
                        "rss",  //$NON-NLS-1$
                        ""  //$NON-NLS-1$
                    });
        
        
//        db.insert("theme", null, ThemeDao.getContentValues(getDefaultDarkTheme())); //$NON-NLS-1$
        db.insert("theme", null, ThemeDao.getContentValues(getDefaultLightTheme())); //$NON-NLS-1$

        db.insert("behaviour", null, BehaviourDao.getContentValues(new Behaviour(getContext()))); //$NON-NLS-1$
    }

//    private Theme getDefaultDarkTheme()
//    {
//        Theme defaultDarkTheme = new Theme(context);
//        defaultDarkTheme.setName(context.getText(R.string.defaults_dark_theme).toString());
//        return defaultDarkTheme;
//    }
    
    private Theme getDefaultLightTheme()
    {
        Theme defaultLightTheme = new Theme(context);
        defaultLightTheme.setName(context.getText(R.string.defaults_light_theme).toString());
        defaultLightTheme.setBackgroundColor(Color.WHITE);
        defaultLightTheme.setBackgroundOpacity(70);
        defaultLightTheme.setStoryAuthorColor(Color.BLACK);
        defaultLightTheme.setStoryDateColor(Color.BLACK);
        defaultLightTheme.setStoryTitleColor(Color.BLACK);
        defaultLightTheme.setStoryDescriptionColor(Color.BLACK);
        defaultLightTheme.setNumColumns(2);
        defaultLightTheme.setThumbnailSize(120);
        return defaultLightTheme;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        int inc = oldVersion + 1;
        while ( inc <= newVersion ) 
        {
            execBatch(db, UPDATE_FOLDER_PATH + inc + ".ddl"); //$NON-NLS-1$
            inc++;
        }
    }
    
    public void onOpen(SQLiteDatabase db) 
    {
        super.onOpen(db);
        
        if (!db.isReadOnly()) 
        {
            db.execSQL("PRAGMA foreign_keys=ON;"); //$NON-NLS-1$
        }
    }

    
    public Cursor executeQuery(String query, Object[] parameters, IQueryCallback callback) 
    {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransactionNonExclusive();
        Cursor cursor = null;
        try 
        {
            cursor = database.rawQuery(query, convertToStrings(parameters));
            if ( callback != null ) 
            {
                callback.doInTransaction(cursor);
            }
            database.setTransactionSuccessful();
            return cursor;
        }
        finally 
        {
            database.endTransaction();
        }
    }
    
    public Cursor executeQuery(String query, Object[] parameters) 
    {
        return executeQuery(query, parameters, null);
    }

    //TODO: ok for single statements. needs to be refined  
    //    : for multi-statement transactions (main cases)
    public void executeUpdate(String query, Object[] parameters) 
    {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransactionNonExclusive();
        try 
        {
            database.execSQL(query, parameters);
            database.setTransactionSuccessful();
        }
        finally 
        {
            database.endTransaction();
        }
    }
    
    //TODO: this doesn't seem right, but then again android is one the crappiest 
    //    : framework around today, so perhaps this is the right way to do it
    private String[] convertToStrings(Object[] parameters) 
    {
        String[] s = new String[parameters.length];
        
        for (int i = 0; i < parameters.length; i++)
        {
            Object o = parameters[i];
            if ( o == null ) 
            {
                s[i] = null;
            }
            else
            {
                if ( o instanceof Date ) 
                {
                    s[i] = Long.toString(((Date) o).getTime());
                }
                else 
                {
                    s[i] = o.toString();
                }
            }
        }
        
        return s;
    }
    
    public Context getContext()
    {
        return context;
    }
}
