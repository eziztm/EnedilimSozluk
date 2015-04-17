package com.enedilim.dict;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.enedilim.dict.asynctasks.WordListInitializerTask;
import com.enedilim.dict.fragments.AboutFragment;
import com.enedilim.dict.fragments.HistoryFragment;
import com.enedilim.dict.fragments.SearchFragment;
import com.enedilim.dict.utils.CacheManager;
import com.enedilim.dict.utils.DatabaseHelper;

/**
 * Main activity.
 *
 * @author Eziz Annagurban
 * @version 1.2
 */
public class MainActivity extends ActionBarActivity implements HistoryFragment.OnWordSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_FRAGMENT = "SEARCH";
    private static final String ABOUT_FRAGMENT = "ABOUT";
    private static final String HISTORY_FRAGMENT = "HISTORY";

    private DatabaseHelper dbHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initDatabase();
        initCache();
        dbHelper = DatabaseHelper.getInstance(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new SearchFragment(), SEARCH_FRAGMENT);
        transaction.commit();
    }

    /**
     * Initializes and cleans the cache.
     */
    private void initCache() {
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.setCacheDir(getCacheDir());
        cacheManager.cleanCache();
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment requestedFragment;
        String requestFragmentLabel;
        boolean isCurrentlyVisible;

        switch (item.getItemId()) {
            case R.id.menu_search:
                requestedFragment = fragmentManager.findFragmentByTag(SEARCH_FRAGMENT);
                isCurrentlyVisible = requestedFragment != null && requestedFragment.isVisible();
                if (requestedFragment == null) {
                    requestedFragment = new SearchFragment();
                }
                if (!isCurrentlyVisible) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                ((SearchFragment)requestedFragment).setDisplayWord(null);
                requestFragmentLabel = SEARCH_FRAGMENT;
                break;
            case R.id.aboutMenu:
                requestedFragment = fragmentManager.findFragmentByTag(ABOUT_FRAGMENT);
                isCurrentlyVisible = requestedFragment != null && requestedFragment.isVisible();
                if (requestedFragment == null) {
                    requestedFragment = new AboutFragment();
                }
                requestFragmentLabel = ABOUT_FRAGMENT;
                break;
            case R.id.menu_history:
                requestedFragment = fragmentManager.findFragmentByTag(HISTORY_FRAGMENT);
                isCurrentlyVisible = requestedFragment != null && requestedFragment.isVisible();
                if (requestedFragment == null) {
                    requestedFragment = new HistoryFragment();
                }
                requestFragmentLabel = HISTORY_FRAGMENT;
                break;
            default:
                return true;
        }

        if (!isCurrentlyVisible) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, requestedFragment, requestFragmentLabel);
            if (!requestFragmentLabel.equals(SEARCH_FRAGMENT)) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Database should only be populated once.
     *
     * Checks if this is the first run, if it is, initiate word table
     * initialization.
     */
    private void initDatabase() {
        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int dbVersion = preferences.getInt("DB_VERSION", 0);
        if (DatabaseHelper.DATABASE_VERSION > dbVersion) {
            WordListInitializerTask initTask = new WordListInitializerTask(this);
            initTask.execute(dbHelper);

            // Code to run once
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("DB_VERSION", DatabaseHelper.DATABASE_VERSION);
            editor.commit();
        }

    }

    /**
     * Communicates from History fragment list view selection to Search Fragment
     * @param word selected word.
     */
    @Override
    public void onWordSelected(String word) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        SearchFragment fragment = (SearchFragment) fragmentManager.findFragmentByTag(SEARCH_FRAGMENT);
        fragment.setDisplayWord(word);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment, SEARCH_FRAGMENT);
        transaction.commit();
    }
}