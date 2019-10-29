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
            buffView.setCastExtendEventListener(new BuffView.OnCastExtendEventListener() {
                @Override
                public void onEvent() {
                    castBuffExtend();
                }
            });
        }
    }

    private void cancelBuff(){
        buff.cancel();
        yfa.getAllBuffs().saveBuffs();
        refreshView();
    }

    private void castBuff(){
        buff.normalCast(mA,yfa.getCasterLevel());
        yfa.castSpell(buff.getSpellRank());
        yfa.getAllBuffs().saveBuffs();
        if(buff.getName().equalsIgnoreCase("Simulacre de vie supérieur")){
            yfa.getAllResources().getResource("resource_hp").shield(20);
        }
        refreshView();

    }

    private void castBuffExtend(){
        buff.extendCast(mA,yfa.getCasterLevel());
        yfa.castSpell(buff.getSpellRank()+1);
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
