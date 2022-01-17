package stellarnear.yfa_companion.Perso;

import android.app.Activity;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Log.SelfCustomLog;
import stellarnear.yfa_companion.Tools;

public class BuffManager extends SelfCustomLog {
    private Activity mA;
    private Buff buff;
    private BuffView buffView;
    private Perso yfa = MainActivity.yfa;
    private Tools tools = Tools.getTools();

    public BuffManager(Activity mA, Buff buff) {
        try {
            this.mA = mA;
            this.buff = buff;
            this.buffView = new BuffView(mA, buff);
            if (!buff.isPerma()) {
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
                    public void onEvent(int nCast) {
                        castBuffExtend(nCast);
                    }
                });
            }
        } catch (Exception e) {
            log.err("Error on BuffManager creation", e);
        }
    }

    private void cancelBuff() {
        buff.cancel();
        yfa.getAllBuffs().saveBuffs();
        refreshView();
    }

    private void castBuff() {
        buff.normalCast(mA, yfa.getCasterLevel());
        if (buff.isFromSpell()) {
            yfa.castSpell(buff.getSpellRank());
        } else {
            yfa.getAllResources().getResource(buff.getId().replace("capacity", "resource")).spend(1);
        }
        yfa.getAllBuffs().saveBuffs();
        if (buff.getName().equalsIgnoreCase("Simulacre de vie supérieur")) {
            yfa.getAllResources().getResource("resource_hp").shield(20);
        }
        refreshView();
    }

    private void castBuffExtend(int nCastDuration) {
        buff.extendCast(mA, yfa.getCasterLevel(), nCastDuration);
        if (buff.isFromSpell()) {
            yfa.castSpell(buff.getSpellRank() + nCastDuration);
        } else {
            yfa.getAllResources().getResource(buff.getId().replace("capacity", "resource")).spend(1);
            yfa.getAllResources().getResource("capacity_epic_bloodline".replace("capacity", "resource")).spend(1);
        }
        yfa.getAllBuffs().saveBuffs();
        if (buff.getName().equalsIgnoreCase("Simulacre de vie supérieur")) {
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
