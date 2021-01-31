package stellarnear.yfa_companion.SettingsFragments;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import stellarnear.yfa_companion.Log.CustomLog;

public abstract class CustomPreference extends Preference {
    public CustomLog log = new CustomLog(this.getClass());

    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public CustomPreference(Context context) {
        super(context);
    }
}
