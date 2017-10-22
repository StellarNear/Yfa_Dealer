package stellarnear.yfa_dealer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || CombatPreferenceFragment.class.getName().equals(fragmentName)
                || SpellByDayPreferenceFragment.class.getName().equals(fragmentName)
                || RazPreferenceFragment.class.getName().equals(fragmentName)
                || SleepPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     page générale
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);


        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     page de combat
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CombatPreferenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_combat);
            setHasOptionsMenu(true);

        }



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    //autre fragement liste de sort par jour
    /**
     page de sort par jour
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SpellByDayPreferenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_spell_by_day);
            setHasOptionsMenu(true);

        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     page de reset settings
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RazPreferenceFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();

            String descr="Remise à zero des paramètres de l'application";
            Toast toast = Toast.makeText(getContext(), descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            startActivity(new Intent(getActivity(), MainActivity.class));

        }
    }


    /**
     page de dodo
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SleepPreferenceFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("n_rank_1",getString(R.string.n_rank_1_def));
            editor.putString("n_rank_2",getString(R.string.n_rank_2_def));
            editor.putString("n_rank_3",getString(R.string.n_rank_3_def));
            editor.putString("n_rank_4",getString(R.string.n_rank_4_def));
            editor.putString("n_rank_5",getString(R.string.n_rank_5_def));
            editor.putString("n_rank_6",getString(R.string.n_rank_6_def));
            editor.putString("n_rank_7",getString(R.string.n_rank_7_def));
            editor.putString("n_rank_8",getString(R.string.n_rank_8_def));
            editor.putString("n_rank_9",getString(R.string.n_rank_9_def));
            editor.putString("n_rank_10",getString(R.string.n_rank_10_def));
            editor.putString("n_rank_11",getString(R.string.n_rank_11_def));
            editor.putString("n_rank_12",getString(R.string.n_rank_12_def));
            editor.putString("n_rank_13",getString(R.string.n_rank_13_def));
            editor.putString("n_rank_14",getString(R.string.n_rank_14_def));
            editor.putString("n_rank_15",getString(R.string.n_rank_15_def));
            editor.commit();

            String descr="Bonne nuit ! Demain une nouvelle journée pleine de sortilèges t'attends.";
            Toast toast = Toast.makeText(getContext(), descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            startActivity(new Intent(getActivity(), MainActivity.class));

        }

        public Integer to_int(String key){
            Integer value;
            try {
                value = Integer.parseInt(key);
            } catch (Exception e){
                value=0;
            }
            return value;
        }
    }



}
