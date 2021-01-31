package stellarnear.yfa_companion.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import stellarnear.yfa_companion.Log.CustomLog;

public abstract class CustomFragment extends Fragment {
    public transient CustomLog log = new CustomLog(this.getClass());

    public LayoutInflater inflater;
    public ViewGroup container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }
        this.inflater=inflater;
        this.container=container;

        View result = null;
        try {
            result = returnFragView();
        } catch (Exception e) {
            log.fatal(getActivity(),"Error in Fragment : "+this.getClass().getCanonicalName(),e);
        }
        return result;
    }

    abstract public View returnFragView() throws Exception;
}
