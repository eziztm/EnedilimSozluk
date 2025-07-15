package com.enedilim.dict;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.enedilim.dict.asynctasks.TaskRunner;
import com.enedilim.dict.asynctasks.UpdateWordListTask;
import com.enedilim.dict.asynctasks.WordListInitializerTask;
import com.enedilim.dict.fragments.AboutFragment;
import com.enedilim.dict.fragments.HistoryFragment;
import com.enedilim.dict.fragments.SearchFragment;
import com.enedilim.dict.utils.DatabaseHelper;

/**
 * Main activity.
 *
 * @author Eziz Annagurban
 * @version 1.3
 */
public class MainActivity extends AppCompatActivity implements HistoryFragment.OnWordSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_FRAGMENT = "SEARCH";
    private static final String ABOUT_FRAGMENT = "ABOUT";
    private static final String HISTORY_FRAGMENT = "HISTORY";

    private DatabaseHelper dbHelper;
    private String currentFragment;
    private String currentWord;

    private TaskRunner taskRunner = new TaskRunner();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initDatabase();
        dbHelper = DatabaseHelper.getInstance(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        currentFragment = SEARCH_FRAGMENT;
        transaction.replace(R.id.fragment_container, new SearchFragment(taskRunner), SEARCH_FRAGMENT);
        Log.d(TAG, "On create, displaying fragment: " + currentFragment);
        transaction.commit();
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
                    requestedFragment = new SearchFragment(taskRunner);
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
        currentFragment = requestFragmentLabel;
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

            final LinearLayout installationNotice = findViewById(R.id.installationNotice);
            installationNotice.setVisibility(View.VISIBLE);
            Context parent = this;

            taskRunner.executeAsync(new WordListInitializerTask(this, dbHelper), (result) -> {
                installationNotice.setVisibility(View.GONE);
                if (!result) {
                    String toastDbMsg = parent.getResources().getString(R.string.errorDb);
                    Toast.makeText(parent, toastDbMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Exception while creating database.");
                }
            });
        } else {
            taskRunner.executeAsync(new UpdateWordListTask(this, dbHelper));
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
        currentWord = word;
        fragment.setDisplayWord(word);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment, SEARCH_FRAGMENT);
        currentFragment = SEARCH_FRAGMENT;
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_FRAGMENT", currentFragment);
        Log.d(TAG, "Storing instance state, current fragment " + currentFragment);

        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);
        if (searchFragment != null) {
            currentWord = searchFragment.getDisplayWord();
        }
        outState.putString("CURRENT_WORD", currentWord);

        Log.d(TAG, "Storing instance state, current word " + currentWord);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        currentFragment = savedInstanceState.getString("CURRENT_FRAGMENT");
        currentWord = savedInstanceState.getString("CURRENT_WORD");
        Log.d(TAG, "Restoring instance state to " + currentFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment requestedFragment;
        boolean isCurrentlyVisible;

        switch (currentFragment) {
            case SEARCH_FRAGMENT:
                requestedFragment = fragmentManager.findFragmentByTag(SEARCH_FRAGMENT);
                isCurrentlyVisible = requestedFragment != null && requestedFragment.isVisible();
                if (requestedFragment == null) {
                    requestedFragment = new SearchFragment(taskRunner);
                }
                if (!isCurrentlyVisible) {
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                ((SearchFragment) requestedFragment).setDisplayWord(currentWord);
                break;
            case ABOUT_FRAGMENT:
                requestedFragment = fragmentManager.findFragmentByTag(ABOUT_FRAGMENT);
                isCurrentlyVisible = requestedFragment != null && requestedFragment.isVisible();
                if (requestedFragment == null) {
                    requestedFragment = new AboutFragment();
                }
                break;
            case HISTORY_FRAGMENT:
                requestedFragment = fragmentManager.findFragmentByTag(HISTORY_FRAGMENT);
                isCurrentlyVisible = requestedFragment != null && requestedFragment.isVisible();
                if (requestedFragment == null) {
                    requestedFragment = new HistoryFragment();
                }
                break;
            default:
                return;
        }

        if (!isCurrentlyVisible) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, requestedFragment, currentFragment);
            if (!currentFragment.equals(SEARCH_FRAGMENT)) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }
}