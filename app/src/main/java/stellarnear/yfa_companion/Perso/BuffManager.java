package stellarnear.yfa_companion.Perso;

import android.app.Activity;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Tools;

public class BuffManager {
    private Activity mA;
    private Buff buff;
    private BuffView buffView;
    private Perso yfa=MainActivity.yfa;
    private Tools tools=new Tools();

    public BuffManager(Activity mA,Buff buff){
        this.mA=mA;
        this.buff=buff;
        this.buffView=new BuffView(mA,buff);
        if(!buff.isPerma()){
            buffView.setClickListner();
            buffView.setCancelEventListener(new BuffView.OnCancelEventListener() {
                @Override
                public void onEvent() {
                    cancelBuff();
                }
            });
            buffView.setCastEventListener(new BuffView.OnCastEventListener() {
                @Override
                public void onEvent() {
                    castBuff();
                }
            });
        }
    }

    private float calculateMinute() {
        String duration = buff.getSpellDuration();
        float floatMinute=0f;
        if(!duration.equalsIgnoreCase("permanente")){
            Integer result= tools.toInt(duration.replaceAll("[^0-9?!]",""));
            String duration_unit = duration.replaceAll("[0-9?!]","");
            if(duration.contains("/lvl")){
                Integer lvl =  yfa.getCasterLevel();
                result = result * lvl;
                duration_unit = duration.replaceAll("/lvl","").replaceAll("[0-9?!]","");
            }

            if(duration_unit.equalsIgnoreCase("h")){
                floatMinute=result*60f;
            } else if(duration_unit.equalsIgnoreCase("min")){
                floatMinute=result*1f;
            }
        }
        return floatMinute;
    }

    private void cancelBuff(){
        buff.cancel();
        yfa.getAllBuffs().saveBuffs();
        refreshView();
    }

    private void castBuff(){
        buff.normalCast(calculateMinute());
        yfa.castSpell(buff.getSpellRank());
        yfa.getAllBuffs().saveBuffs();
        if(buff.getName().equalsIgnoreCase("Simulacre de vie supérieur")){
            yfa.getAllResources().getResource("resource_hp").shield(20);
        }
        refreshView();
    }


    public BuffView getbuffView() {
        return buffView;
    }

    public void refreshView() {
        buffView.refresh();
    }
}
