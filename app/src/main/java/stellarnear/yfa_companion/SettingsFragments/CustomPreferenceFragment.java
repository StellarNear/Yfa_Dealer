package stellarnear.yfa_companion.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import stellarnear.yfa_companion.Log.CustomLog;

public abstract class CustomPreferenceFragment extends PreferenceFragment {
    protected SharedPreferences settings;
    public CustomLog log = new CustomLog(this.getClass());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        this.settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            onCreateFragment();
        } catch (Exception e) {
            log.fatal(getActivity(),e.getMessage(),e);
        }
    }

    protected abstract void onCreateFragment();

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        try {
            onPreferenceTreeClickFragment(preferenceScreen, preference);
        } catch (Exception e) {
            log.fatal(getActivity(),e.getMessage(),e);
        }
        return true;
    }

    protected abstract void onPreferenceTreeClickFragment(PreferenceScreen preferenceScreen, Preference preference) throws Exception;

    @Override
    public void onDestroy() {
        try {
            onDestroyFragment();
        } catch (Exception e) {
            log.fatal(getActivity(),e.getMessage(),e);
        }
        super.onDestroy();
    }

    protected abstract void onDestroyFragment();
}
