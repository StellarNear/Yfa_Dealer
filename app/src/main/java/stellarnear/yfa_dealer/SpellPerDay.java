package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;


public class SpellPerDay extends AppCompatActivity {
    int [] list_spell_per_day;
    int [] list_spell_per_day_conv;
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

        editor.putString("n_rank_1_conv",String.valueOf(list_spell_per_day_conv[0]));
        editor.putString("n_rank_2_conv",String.valueOf(list_spell_per_day_conv[1]));
        editor.putString("n_rank_3_conv",String.valueOf(list_spell_per_day_conv[2]));
        editor.putString("n_rank_4_conv",String.valueOf(list_spell_per_day_conv[3]));


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
        int [] list_spell_per_day_conv=new int[5];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
       
        list_spell_per_day[0]=to_int(prefs.getString("n_rank_1",prefs.getString("n_rank_1_start",mC.getString(R.string.n_rank_1_def))));
        list_spell_per_day[1]=to_int(prefs.getString("n_rank_2",prefs.getString("n_rank_2_start",mC.getString(R.string.n_rank_2_def))));
        list_spell_per_day[2]=to_int(prefs.getString("n_rank_3",prefs.getString("n_rank_3_start",mC.getString(R.string.n_rank_3_def))));
        list_spell_per_day[3]=to_int(prefs.getString("n_rank_4",prefs.getString("n_rank_4_start",mC.getString(R.string.n_rank_4_def))));
        list_spell_per_day[4]=to_int(prefs.getString("n_rank_5",prefs.getString("n_rank_5_start",mC.getString(R.string.n_rank_5_def))));
        list_spell_per_day[5]=to_int(prefs.getString("n_rank_6",prefs.getString("n_rank_6_start",mC.getString(R.string.n_rank_6_def))));
        list_spell_per_day[6]=to_int(prefs.getString("n_rank_7",prefs.getString("n_rank_7_start",mC.getString(R.string.n_rank_7_def))));
        list_spell_per_day[7]=to_int(prefs.getString("n_rank_8",prefs.getString("n_rank_8_start",mC.getString(R.string.n_rank_8_def))));
        list_spell_per_day[8]=to_int(prefs.getString("n_rank_9",prefs.getString("n_rank_9_start",mC.getString(R.string.n_rank_9_def))));
        list_spell_per_day[9]=to_int(prefs.getString("n_rank_10",prefs.getString("n_rank_10_start",mC.getString(R.string.n_rank_10_def))));
        list_spell_per_day[10]=to_int(prefs.getString("n_rank_11",prefs.getString("n_rank_11_start",mC.getString(R.string.n_rank_11_def))));
        list_spell_per_day[11]=to_int(prefs.getString("n_rank_12",prefs.getString("n_rank_12_start",mC.getString(R.string.n_rank_12_def))));
        list_spell_per_day[12]=to_int(prefs.getString("n_rank_13",prefs.getString("n_rank_13_start",mC.getString(R.string.n_rank_13_def))));
        list_spell_per_day[13]=to_int(prefs.getString("n_rank_14",prefs.getString("n_rank_14_start",mC.getString(R.string.n_rank_14_def))));
        list_spell_per_day[14]=to_int(prefs.getString("n_rank_15",prefs.getString("n_rank_15_start",mC.getString(R.string.n_rank_15_def))));
        this.list_spell_per_day=list_spell_per_day;

        list_spell_per_day_conv[0]=to_int(prefs.getString("n_rank_1_conv",prefs.getString("n_rank_1_start_conv",mC.getString(R.string.n_rank_1_def_conv))));
        list_spell_per_day_conv[1]=to_int(prefs.getString("n_rank_2_conv",prefs.getString("n_rank_2_start_conv",mC.getString(R.string.n_rank_2_def_conv))));
        list_spell_per_day_conv[2]=to_int(prefs.getString("n_rank_3_conv",prefs.getString("n_rank_3_start_conv",mC.getString(R.string.n_rank_3_def_conv))));
        list_spell_per_day_conv[3]=to_int(prefs.getString("n_rank_4_conv",prefs.getString("n_rank_4_start_conv",mC.getString(R.string.n_rank_4_def_conv))));
        list_spell_per_day_conv[4]=to_int(prefs.getString("n_rank_5_conv",prefs.getString("n_rank_5_start_conv",mC.getString(R.string.n_rank_5_def_conv))));
        this.list_spell_per_day_conv=list_spell_per_day_conv;
    }

    public Integer getSpell_per_day_rank(Integer rank) {
        if (rank==0){return 1;}  //il y aura toujours un sort de rank 0 dispo
        return this.list_spell_per_day[rank-1];
    }

    public void castSpell_rank(Integer rank) {
        if(!(rank==0)){
            this.list_spell_per_day[rank-1]-=1;

            try { //si c'est un slot convertible on check qu'on en cast pas
                if (this.list_spell_per_day[rank-1]<this.list_spell_per_day_conv[rank-1]){
                    this.list_spell_per_day_conv[rank-1]=this.list_spell_per_day[rank-1];
                }
            } catch (Exception e) {}
        }
    }
    
    public Boolean checkRank_available(Integer rank, Context mC) {
        if (rank==0){return true;}
        Integer test=-1;
         try {
            test=this.list_spell_per_day[rank-1]-1;
        } catch (Exception e){
         
            String descr="Il n'y a pas d'emplacement de sort du rang "+rank+" de disponible...";
            Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL,0,0);
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
    // convertible

    public void castSpell_rank_conv(Integer rank) {
        if(!(rank==0)){
            this.list_spell_per_day[rank-1]-=1;
            this.list_spell_per_day_conv[rank-1]-=1;
        }
    }

    public Integer getSpell_per_day_rank_conv(Integer rank) {
        try {
            return this.list_spell_per_day_conv[rank-1];
        } catch (Exception e){ return 0;}
    }

    public boolean checkAnyconvertible_available() {
        int n_all_conv=0;
        for (int i=0;i<list_spell_per_day_conv.length;i++){
            if (list_spell_per_day_conv[i]>0){n_all_conv+=list_spell_per_day_conv[i];}
        }
        if (n_all_conv>0){
            return true;
        }else {
            return false;
        }
    }

    public boolean checkAnyconvertible_available(Context mC) {
        int n_all_conv=0;
        for (int i=0;i<list_spell_per_day_conv.length;i++){
            if (list_spell_per_day_conv[i]>0){n_all_conv+=list_spell_per_day_conv[i];}
        }
        if (n_all_conv>0){
            return true;
        }else {
            String descr="Il n'y a pas d'emplacement de sort convertible de disponible...";
            Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            return false;
        }
    }

    public boolean checkConvertible_available(int rank) {
        if (list_spell_per_day_conv[rank-1]>0){
            return true;
        }else {
            return false;
        }
    }

    public boolean checkConvertible_available(int rank,Context mC) {
        if (list_spell_per_day_conv[rank-1]>0){
            return true;
        }else {
            String descr="Il n'y a pas d'emplacement de sort convertible du rang "+rank+" de disponible...";
            Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            return false;
        }
    }
}
