package nsb.widget.news.fragments;

import java.util.ArrayList;
import java.util.List;

import nsb.widget.news.activity.MainActivity;
import nsb.widget.news.data.dao.BehaviourDao;
import nsb.widget.news.data.dao.ConfigurationDao;
import nsb.widget.news.data.dao.DaoUtils;
import nsb.widget.news.data.dao.FeedDao;
import nsb.widget.news.data.dao.ThemeDao;
import nsb.widget.news.model.Behaviour;
import nsb.widget.news.model.Configuration;
import nsb.widget.news.model.Feed;
import nsb.widget.news.model.Theme;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nsb.widget.news.R;

public class WidgetConfigFragment extends PreferenceFragment implements OnPreferenceChangeListener, OnPreferenceClickListener
{
    private static final String LAUNCH_MAIN_ACTIVITY = "launch_main_activity"; //$NON-NLS-1$
    private static final String WIDGET_TITLE = "widget_title"; //$NON-NLS-1$
    private static final String BEHAVIOUR = "behaviour"; //$NON-NLS-1$
    private static final String THEME = "theme"; //$NON-NLS-1$
    private static final String FEEDS = "feeds"; //$NON-NLS-1$
 
    private static final int REQUEST_MAIN_ACTIVITY = 1;
    
    private int appWidgetId;
    
    private Configuration configuration;
    
    private Preference mainLauncherPreference;
    private Preference behaviourPreference;
    private Preference themePreference;
    private Preference feedsPreference;
    
    private FeedDao feedDao;
    private ThemeDao themeDao;
    private BehaviourDao behaviourDao;
    private ConfigurationDao configurationDao;
    private EditTextPreference widgetTitlePreference;
    
    private boolean refreshRequired = false;
    
    private List<Behaviour> declaredBehaviours;
    private List<Theme> declaredThemes;
    private List<Feed> declaredFeeds;
    
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        if ( getArguments() != null )
        {
            appWidgetId = getArguments().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            configuration = configurationDao.getConfigurationByWidgetId(appWidgetId);
            
            declaredBehaviours = behaviourDao.getAll();
            declaredThemes = themeDao.getAll();
            declaredFeeds = feedDao.getAll();
            
            if ( configuration == null )
            {
                configuration = new Configuration(getActivity());
                configuration.setAppWidgetId(appWidgetId);
                configuration.setBehaviour(declaredBehaviours.get(0));
                configuration.setFeeds(declaredFeeds);
                configuration.setTheme(declaredThemes.get(0));
            }
        }
        addPreferencesFromResource(R.xml.widget_config);
        
        setup();
    }
    
    private void setup()
    {
        mainLauncherPreference = findPreference(LAUNCH_MAIN_ACTIVITY);
        widgetTitlePreference = (EditTextPreference) findPreference(WIDGET_TITLE);
        behaviourPreference = findPreference(BEHAVIOUR);
        themePreference = findPreference(THEME);
        feedsPreference = findPreference(FEEDS);
        
        widgetTitlePreference.setText(configuration.getWidgetTitle());
        widgetTitlePreference.setOnPreferenceChangeListener(this);
        
        mainLauncherPreference.setOnPreferenceClickListener(this);
        feedsPreference.setOnPreferenceClickListener(this);
        behaviourPreference.setOnPreferenceClickListener(this);
        themePreference.setOnPreferenceClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.buttonized_list, null);
    }
    
    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        String key = preference.getKey();
        if ( TextUtils.equals(key, LAUNCH_MAIN_ACTIVITY) ) 
        {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivityForResult(intent, REQUEST_MAIN_ACTIVITY);
            return true;
        }

        return false;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch ( requestCode ) 
        {
            case REQUEST_MAIN_ACTIVITY:
                //always reload feeds, behaviours and themes
                declaredBehaviours = behaviourDao.getAll();
                declaredThemes = themeDao.getAll();
                declaredFeeds = feedDao.getAll();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String key = preference.getKey();
        if ( TextUtils.equals(WIDGET_TITLE, key) )
        {
            configuration.setWidgetTitle((String) newValue);
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity)
    {
        feedDao = DaoUtils.getFeedDao(activity);
        themeDao = DaoUtils.getThemeDao(activity);
        behaviourDao = DaoUtils.getBehaviourDao(activity);
        configurationDao = DaoUtils.getConfigurationDao(activity);
        
        super.onAttach(activity);
    }
    
    public void saveConfig()
    {
        configurationDao.saveOrInsert(configuration);
    }

    public Integer[] validate()
    {
        List<Integer> errors = new ArrayList<Integer>();
        
        if ( configuration.getBehaviour() == null ) 
        {
            if ( declaredBehaviours.size() == 1 ) 
            {
                configuration.setBehaviour(declaredBehaviours.get(0));
            }
            else
            {
                errors.add(R.string.config_widget_behaviour_not_set);
            }
        }
        
        if ( configuration.getTheme() == null ) 
        {
            if ( declaredThemes.size() == 1 ) 
            {
                configuration.setTheme(declaredThemes.get(0));
            }
            else
            {
                errors.add(R.string.config_widget_theme_not_set);
            }
        }
        
        if ( configuration.getFeeds().size() == 0 ) 
        {
            errors.add(R.string.config_widget_no_feed_selected);
        }
        
        return errors.toArray(new Integer[errors.size()]);
    }
    
    public boolean requiresRefresh()
    {
        return refreshRequired;
    }
}
