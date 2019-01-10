package stellarnear.yfa_dealer.Rolls;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import stellarnear.yfa_dealer.CustomAlertDialog;
import stellarnear.yfa_dealer.R;

public class DisplayRolls {
    private DiceList selectedDiceList;
    private Context mC;
    private View mainView;
    private CustomAlertDialog metaPopup;
    public DisplayRolls(Activity mA, Context mC, DiceList selectedDiceList) {
        this.mC=mC;
        this.selectedDiceList = selectedDiceList;
        LayoutInflater inflate = mA.getLayoutInflater();
        this.mainView = inflate.inflate(R.layout.damagedetail, null);

        fillWithRolls();

        metaPopup =new CustomAlertDialog(mA,mC,mainView);
        metaPopup.setPermanent(true);
        metaPopup.clickToHide(mainView.findViewById(R.id.fab_damage_detail_ondetlay));

    }

    public void showPopup() {
        metaPopup.showAlert();
    }

    private void fillWithRolls() {
        LinearLayout scrollLinear= this.mainView.findViewById(R.id.scroll_linear);
        int count=0;
        LinearLayout line = new LinearLayout(mC);
        scrollLinear.addView(line);
        line.setOrientation(LinearLayout.HORIZONTAL);
        for (Dice dice : selectedDiceList.getList()){
            if(count>=5){
                line = new LinearLayout(mC);
                line.setOrientation(LinearLayout.HORIZONTAL);
                scrollLinear.addView(line);
                count=0;
            }
                line.addView(dice.getImg());
                count++;
        }
    }

    public int size() {
        return selectedDiceList.getList().size();
    }
}
