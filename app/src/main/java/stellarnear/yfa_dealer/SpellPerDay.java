package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;



public class SpellPerDay extends AppCompatActivity {
    int [] list_spell_per_day;

    public Context mContext;
    public SpellPerDay(Context mC) {
        mContext = mC;
        load_list_spell_per_day(mC);
    }


    public void save_list_spell_per_day(Context mC) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        SharedPreferences.Editor editor = prefs.edit();



         editor.putString("n_rank_1",String.valueOf(list_spell_per_day[0]));
         editor.putString("n_rank_2",String.valueOf(list_spell_per_day[1]));
         editor.putString("n_rank_3",String.valueOf(list_spell_per_day[2]));
         editor.putString("n_rank_4",String.valueOf(list_spell_per_day[3]));
         editor.putString("n_rank_5",String.valueOf(list_spell_per_day[4]));
         editor.putString("n_rank_6",String.valueOf(list_spell_per_day[5]));
         editor.putString("n_rank_7",String.valueOf(list_spell_per_day[6]));
         editor.putString("n_rank_8",String.valueOf(list_spell_per_day[7]));
         editor.putString("n_rank_9",String.valueOf(list_spell_per_day[8]));
        editor.putString("n_rank_10",String.valueOf(list_spell_per_day[9]));
        editor.putString("n_rank_11",String.valueOf(list_spell_per_day[10]));
        editor.putString("n_rank_12",String.valueOf(list_spell_per_day[11]));
        editor.putString("n_rank_13",String.valueOf(list_spell_per_day[12]));
        editor.putString("n_rank_14",String.valueOf(list_spell_per_day[13]));
        editor.putString("n_rank_15",String.valueOf(list_spell_per_day[14]));

        //ca sauve bien sur page 1 pas sur lancement lol page 2


        editor.commit();
    }

    public Integer to_int(String key){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            value=-1;
        }
        return value;
    }

    public void load_list_spell_per_day(Context mC) {
        int [] list_spell_per_day=new int[15];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        list_spell_per_day[0]=to_int(prefs.getString("n_rank_1",mC.getString(R.string.n_rank_1_def)));
        list_spell_per_day[1]=to_int(prefs.getString("n_rank_2",mC.getString(R.string.n_rank_2_def)));
        list_spell_per_day[2]=to_int(prefs.getString("n_rank_3",mC.getString(R.string.n_rank_3_def)));
        list_spell_per_day[3]=to_int(prefs.getString("n_rank_4",mC.getString(R.string.n_rank_4_def)));
        list_spell_per_day[4]=to_int(prefs.getString("n_rank_5",mC.getString(R.string.n_rank_5_def)));
        list_spell_per_day[5]=to_int(prefs.getString("n_rank_6",mC.getString(R.string.n_rank_6_def)));
        list_spell_per_day[6]=to_int(prefs.getString("n_rank_7",mC.getString(R.string.n_rank_7_def)));
        list_spell_per_day[7]=to_int(prefs.getString("n_rank_8",mC.getString(R.string.n_rank_8_def)));
        list_spell_per_day[8]=to_int(prefs.getString("n_rank_9",mC.getString(R.string.n_rank_9_def)));
        list_spell_per_day[9]=to_int(prefs.getString("n_rank_10",mC.getString(R.string.n_rank_10_def)));
        list_spell_per_day[10]=to_int(prefs.getString("n_rank_11",mC.getString(R.string.n_rank_11_def)));
        list_spell_per_day[11]=to_int(prefs.getString("n_rank_12",mC.getString(R.string.n_rank_12_def)));
        list_spell_per_day[12]=to_int(prefs.getString("n_rank_13",mC.getString(R.string.n_rank_13_def)));
        list_spell_per_day[13]=to_int(prefs.getString("n_rank_14",mC.getString(R.string.n_rank_14_def)));
        list_spell_per_day[14]=to_int(prefs.getString("n_rank_15",mC.getString(R.string.n_rank_15_def)));
        this.list_spell_per_day=list_spell_per_day;
    }

    public Integer getSpell_per_day_rank(Integer rank) {
        return this.list_spell_per_day[rank-1];
    }

    public void castSpell_rank(Integer rank) {
        this.list_spell_per_day[rank-1]-=1;
    }
    
    public bool checkRank_available(Integer rank, Context mC) {
        Integer test=-1;
         try {
            test=this.list_spell_per_day[rank-1]-1;
        } catch (Exception e){
         
            String descr="Il n'y a pas d'emplacement de sort du rang "+rank+" de disponible...";
            Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            return false;
        }
        
        if(test>=0) { 
            return true;
        } else {
            String descr="Il n'y a pas d'emplacement de sort du rang "+rank+" de disponible...";
            Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            return false;
        }
        
    }

}
