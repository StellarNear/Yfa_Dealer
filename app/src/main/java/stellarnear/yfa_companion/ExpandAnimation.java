package stellarnear.yfa_companion;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ExpandAnimation extends Animation {

    private float mStartWeight;
    private final float mEndWeight;
    private final LinearLayout mContent;

    public ExpandAnimation(LinearLayout content, float startWeight,
                           float endWeight) {
        mStartWeight = startWeight;
        mEndWeight = endWeight;
        mContent = content;
    }

    @Override
    protected void applyTransformation(float interpolatedTime,Transformation t) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
        mStartWeight = lp.weight;
        lp.weight = (mStartWeight + ((mEndWeight - mStartWeight) * interpolatedTime));
        mContent.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}