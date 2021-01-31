package stellarnear.yfa_companion.Activities;

import android.preference.PreferenceActivity;
import android.view.MenuItem;

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
public class SettingsActivity extends CustomActivity {
    SettingsFragment settingsFragment;

    @Override
    protected void doActivity() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsFragment = new SettingsFragment();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }

    @Override
    protected void onResumeActivity() {
        //do nothing
    }

    //
    // Handle what happens on up button
    //

    @Override
    protected boolean onOptionsItemSelectedActivity(MenuItem item) throws Exception {
        switch (item.getItemId()) {
            case android.R.id.home:
                settingsFragment.onUpButton();
                return true;
        }
        return true;
    }

    @Override
    protected void onConfigurationChangedActivity() {
        //do nothing
    }

    @Override
    protected void onBackPressedActivity() throws Exception {
        settingsFragment.onUpButton();
    }

    @Override
    protected void onDestroyActivity() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
    }
}