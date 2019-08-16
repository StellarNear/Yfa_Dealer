package stellarnear.yfa_companion.Activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.SettingsFragments.SettingsFragment;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category pref shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity
{
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if ( PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsFragment = new SettingsFragment();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }

    //
    // Handle what happens on up button
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                settingsFragment.onUpButton();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        settingsFragment.onUpButton();
    }

    @Override
    protected void onDestroy() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
        super.onDestroy();
    }
}