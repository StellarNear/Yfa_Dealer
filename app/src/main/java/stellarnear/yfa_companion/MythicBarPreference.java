package stellarnear.yfa_companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class MythicBarPreference extends Preference {
    private Tools tools=new Tools();

    public MythicBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MythicBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public MythicBarPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent )
    {
        super.onCreateView(parent);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View mainBar = inflater.inflate(R.layout.mythic_bar, null);

        mainBar.post(new Runnable() {
            @Override
            public void run() {
                TextView percent = mainBar.findViewById(R.id.mythic_bar_percent);
                ImageView backgroundBar = mainBar.findViewById(R.id.mythic_bar_background);
                ViewGroup.LayoutParams para = (ViewGroup.LayoutParams) backgroundBar.getLayoutParams();
                ImageView overlayBar = mainBar.findViewById(R.id.mythic_bar_overlay);
                int oriWidth = overlayBar.getMeasuredWidth();
                int oriHeight = overlayBar.getMeasuredHeight();
                int currentTier = tools.toInt(settings.getString("mythic_tier", String.valueOf(getContext().getResources().getInteger(R.integer.mythic_tier_def))));

                Double coef = (double) currentTier / 10;
                if (coef < 0d) {
                    coef = 0d;
                }
                if (coef > 1d) {
                    coef = 1d;
                }
                percent.setText(String.valueOf((int) (100 * coef)) + "%");
                para.width = (int) (coef * oriWidth);
                para.height = oriHeight;
                backgroundBar.setLayoutParams(para);
            }
        });



        return mainBar;
    }
}
