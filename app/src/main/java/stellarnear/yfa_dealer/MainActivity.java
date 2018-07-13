package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<Spell,CheckBox> map_spell_check=new LinkedHashMap<Spell,CheckBox>();
    private SpellPerDay spell_per_day;
    private boolean shouldExecuteOnResume;
    private AllEquipments allEquipments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldExecuteOnResume = false;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setBackgroundResource(R.drawable.banner_background);
        setSupportActionBar(toolbar);
        allEquipments=new AllEquipments(getApplicationContext());

        this.spell_per_day=new SpellPerDay(getApplicationContext());
        this.spell_per_day.load_list_spell_per_day(getApplicationContext());
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean compact=settings.getBoolean("compact_list",getResources().getBoolean(R.bool.compact_list_switch_def));
        if (compact) {
            buildPage1Compact();
        }else {
            buildPage1();
        }


        ImageButton fab = (ImageButton) findViewById(R.id.fab);

        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator iter = map_spell_check.keySet().iterator();
                Boolean spell_casted=false;

                while(iter.hasNext()) {
                    Spell spell = (Spell) iter.next();
                    CheckBox checkbox = map_spell_check.get(spell);
                    if (checkbox.isChecked()) {
                        spell_casted=true;
                    }
                }

                if (spell_casted){
                    builPage2();
                } else {
                    Toast toast =  Toast.makeText(getApplicationContext(), "Sélectionnes au moins un sort ...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        ImageButton fabEquip = (ImageButton) findViewById(R.id.fabEquip);
        fabEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEquipments.showEquipment(MainActivity.this);
            }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecuteOnResume){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(R.anim.infromleft,R.anim.nothing);
        } else{
            shouldExecuteOnResume = true;
        }
    }

    private void buildPage1Compact() {
        ListSpell ListAllSpell = new ListSpell(getApplicationContext());
        List<Spell> rank_list  = new ArrayList<Spell>();

        ScrollView scroll_main_layout = (ScrollView) findViewById(R.id.main_scroll_relat);
        LinearLayout.LayoutParams scroll_main_param = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,100);
        scroll_main_layout.setLayoutParams(scroll_main_param);

        LinearLayout side_bar_layout = (LinearLayout) findViewById(R.id.side_bar);
        LinearLayout.LayoutParams side_bar_param = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,0);
        side_bar_layout.setLayoutParams(side_bar_param);

        int max_tier=0;
        for(int i=0;i<=19;i++){
            try{
                if (spell_per_day.getSpell_per_day_rank(i)>0) {max_tier=i;}
            }catch (Exception e){ }
        }

        for(int i=0;i<=max_tier;i++){
            LinearLayout Tiers=(LinearLayout) findViewById(R.id.linear1);
            TextView Tier= new TextView(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {0xFF585858,0xFFE6E6E6});
            gd.setCornerRadius(0f);
            Tier.setBackground(gd);

            String tier_txt="Tier "+i;
            
            String titre_tier=tier_txt +" ["+ spell_per_day.getSpell_per_day_rank(i)+" restant(s)]";
            if (i==0){titre_tier=tier_txt +" [illimité]";}
            SpannableString titre=  new SpannableString(titre_tier);
            titre.setSpan(new RelativeSizeSpan(0.65f), tier_txt.length(),titre_tier.length(), 0);
            Tier.setText(titre);

            Tier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            Tier.setTextColor(Color.BLACK);
            Tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            Tier.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            Tiers.addView(Tier);

            View h_sep = new View(this);
            h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep);
            rank_list= ListAllSpell.selectRank(i);
            if (rank_list.size()==0){ continue;}

            HorizontalScrollView scroll_spells= new HorizontalScrollView(this);
            scroll_spells.setHorizontalScrollBarEnabled(false);
            scroll_spells.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            Tiers.addView(scroll_spells);

            LinearLayout grid=new LinearLayout(this);
            scroll_spells.addView(grid);

            View v_sep = new View(this);
            v_sep.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep.setBackgroundColor(Color.DKGRAY);
            grid.addView(v_sep);

            for(final Spell spell : rank_list){
                final CheckBox checkbox=new CheckBox(getApplicationContext());

                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu tu lancer "+spell.getName()+" une "+(spell.getN_cast()+1)+"ième fois ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            checkbox.setChecked(true);
                                            spell.setN_cast(spell.getN_cast()+1);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.setN_cast(1);
                                        }}).show();
                        }
                    }
                });


                setCheckBoxColorCompact(checkbox,spell);
                checkbox.setText(spell.getName()+" ");
                grid.addView(checkbox);

                map_spell_check.put(spell,checkbox);

                View v_sep2 = new View(this);
                v_sep2.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
                v_sep2.setBackgroundColor(Color.DKGRAY);
                grid.addView(v_sep2);
            }
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep2);
        }
    }

    private void buildPage1() {
        ListSpell ListAllSpell = new ListSpell(getApplicationContext());
        List<Spell> rank_list  = new ArrayList<Spell>();

        int max_tier=0;
        for(int i=0;i<=19;i++){
            try{
                if (spell_per_day.getSpell_per_day_rank(i)>0) {max_tier=i;}
            }catch (Exception e){ }
        }

        for(int i=0;i<=max_tier;i++){
            final ScrollView scroll_tier=(ScrollView) findViewById(R.id.main_scroll_relat);
            LinearLayout Tiers=(LinearLayout) findViewById(R.id.linear1);
            final TextView Tier= new TextView(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {0xFFd9d9d9,0xFFFFFFFF});
            gd.setCornerRadius(0f);
            Tier.setBackground(gd);

            String tier_txt="Tier "+i;

            String titre_tier=tier_txt +" ["+ spell_per_day.getSpell_per_day_rank(i)+" restant(s)]";
            if (i==0){titre_tier=tier_txt +" [illimité]";}
            SpannableString titre=  new SpannableString(titre_tier);
            titre.setSpan(new RelativeSizeSpan(0.65f), tier_txt.length(),titre_tier.length(), 0);
            Tier.setText(titre);

            Tier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            Tier.setTextColor(Color.BLACK);
            Tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            Tier.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            Tiers.addView(Tier);

            // side bar
            LinearLayout side=(LinearLayout) findViewById(R.id.side_bar);
            side.setElevation(10);
            GradientDrawable gd_side = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[] {0xFFD8D8D8,0xFFFFFFFF});
            side.setBackground(gd_side);
            TextView side_txt=new TextView(this);
            side_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            side_txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));

            int orientation = getResources().getConfiguration().orientation;  //1 pour portrait et 2 paysage
            side_txt.setTextColor(Color.DKGRAY);
            if (orientation==1) {
                side_txt.setText("T" + i + "\n(" + spell_per_day.getSpell_per_day_rank(i) + ")");
                if (i==0){side_txt.setText("T"+i+"\n("+ DecimalFormatSymbols.getInstance().getInfinity()+")");}
                if (spell_per_day.getSpell_per_day_rank_conv(i)>0) {
                    String n_spell_conv_txt="T" + i + "\n(" + spell_per_day.getSpell_per_day_rank(i)+","+ spell_per_day.getSpell_per_day_rank_conv(i) + ")";
                    String before_conv="T" + i + "\n(" + spell_per_day.getSpell_per_day_rank(i)+",";
                    SpannableString n_spell_conv=  new SpannableString(n_spell_conv_txt);
                    n_spell_conv.setSpan(new ForegroundColorSpan(getColor(R.color.conversion)),before_conv.length(),before_conv.length()+spell_per_day.getSpell_per_day_rank_conv(i).toString().length(), 0);// set color2
                    side_txt.setText(n_spell_conv);
                }
            } else {
                side_txt.setText("T" + i + " (" + spell_per_day.getSpell_per_day_rank(i) + ")");
                if (i==0){side_txt.setText("T"+i+" ("+ DecimalFormatSymbols.getInstance().getInfinity()+")");}
                if (spell_per_day.getSpell_per_day_rank_conv(i)>0) {
                    String n_spell_conv_txt="T" + i + " (" + spell_per_day.getSpell_per_day_rank(i)+","+ spell_per_day.getSpell_per_day_rank_conv(i) + ")";
                    String before_conv="T" + i + " (" + spell_per_day.getSpell_per_day_rank(i)+",";
                    SpannableString n_spell_conv=  new SpannableString(n_spell_conv_txt);
                    n_spell_conv.setSpan(new ForegroundColorSpan(getColor(R.color.conversion)),before_conv.length(),before_conv.length()+spell_per_day.getSpell_per_day_rank_conv(i).toString().length(), 0);// set color2
                    side_txt.setText(n_spell_conv);
                }
            }

            side_txt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                        scroll_tier.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll_tier.scrollTo(0, Tier.getTop());
                            }
                        });


                }
            });

            side.addView(side_txt);


            View h_sep = new View(this);
            h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep);
            rank_list= ListAllSpell.selectRank(i);
            if (rank_list.size()==0){ continue;}

            for(final Spell spell : rank_list){
                final CheckBox checkbox=new CheckBox(getApplicationContext());
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            spell.setN_cast(spell.getN_cast()+1);
                            current_spell_display(getApplicationContext());
                        }



                        if (!isChecked) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu tu lancer "+spell.getName()+" une "+(spell.getN_cast()+1)+"ième fois ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            checkbox.setChecked(true);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.setN_cast(0);
                                        }}).show();
                        }
                    }
                });


                setCheckBoxColor(checkbox,spell);
                checkbox.setText(spell.getName()+" ");
                Tiers.addView(checkbox);

                map_spell_check.put(spell,checkbox);

            }
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep2);
        }
    }

    private void current_spell_display(Context mC) {
        Iterator iter = map_spell_check.keySet().iterator();
        Boolean multi_spell=false;
        Integer n_spell=0;
        String display="Sort séléctionnés :\n";

        while(iter.hasNext()) {
            Spell spell=(Spell)iter.next();
            CheckBox checkbox= map_spell_check.get(spell);
            if (checkbox.isChecked()){
                if (spell.getN_cast()>1){
                    multi_spell=true;
                    display+=spell.getN_cast()+" x "+spell.getName()+"\n";
                    } else {
                    n_spell+=1;
                    display+=spell.getName()+"\n";
                    }
                }
            }
        if (n_spell >1) { multi_spell=true;}

        if (multi_spell){
            display = display.substring(0, display.length() - 1);
            Toast toast =  Toast.makeText(mC, display, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void builPage2() {
        List<Spell> sel_list= new ArrayList<Spell>();
        Iterator iter = map_spell_check.keySet().iterator();
        Boolean spell_casted=false;

        while(iter.hasNext()) {
            Spell spell=(Spell)iter.next();
            CheckBox checkbox= map_spell_check.get(spell);
            if (checkbox.isChecked()){
                sel_list.add(spell);
                if (spell.getN_cast()>1){
                    for (int i=1;i<spell.getN_cast();i++){
                        sel_list.add(spell);
                    }
                }
                spell_casted=true;
            } 
        }
        
        if (spell_casted) {
            Intent intent = new Intent(this, SpellCastActivity.class);
            intent.putExtra("selected_spells", (Serializable) sel_list);
            startActivity(intent);
            overridePendingTransition(R.anim.infromright,R.anim.nothing);
        } else { startActivity(new Intent(this, MainActivity.class));}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCheckBoxColorCompact(CheckBox checkbox,Spell spell) {
        int bord=0;
        int centre=0;
        boolean dmg_spell=true;
        if (spell.getDmg_type().equals("aucun")) {bord=R.color.aucun_dark;centre=R.color.aucun;}
        else if (spell.getDmg_type().equals("feu")) {bord=R.color.feu_dark;centre=R.color.feu;}
        else if (spell.getDmg_type().equals("foudre")) {bord=R.color.foudre_dark;centre=R.color.foudre;}
        else if (spell.getDmg_type().equals("froid")) {bord=R.color.froid_dark;centre=R.color.froid;}
        else if (spell.getDmg_type().equals("acide")) {bord=R.color.acide_dark;centre=R.color.acide;}
        else {bord=R.color.white;centre=R.color.white;dmg_spell=false;}

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[] {getColor(centre),getColor(bord)});  //pour V2 effet passer en TOP_BOTTOM et mettre getColor(bord),getColor(centre),getColor(bord)
        gd.setCornerRadius(0f);
        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setGradientRadius(200.0f);

        checkbox.setTextColor(Color.BLACK);
        int[] colorClickBox=new int[]{Color.DKGRAY,Color.DKGRAY};
        //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },colorClickBox

        );
        checkbox.setButtonTintList(colorStateList);
        checkbox.setBackground(gd);
    }

    public void setCheckBoxColor(CheckBox checkbox,Spell spell) {
        int bord=0;
        int centre=0;
        boolean dmg_spell=true;
        if (spell.getDmg_type().equals("aucun")) {centre=R.color.aucun_dark;bord=R.color.aucun;}
        else if (spell.getDmg_type().equals("feu")) {centre=R.color.feu_dark;bord=R.color.feu;}
        else if (spell.getDmg_type().equals("foudre")) {centre=R.color.foudre_dark;bord=R.color.foudre;}
        else if (spell.getDmg_type().equals("froid")) {centre=R.color.froid_dark;bord=R.color.froid;}
        else if (spell.getDmg_type().equals("acide")) {centre=R.color.acide_dark;bord=R.color.acide;}
        else {centre=R.color.white;bord=R.color.white;dmg_spell=false;}

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {getColor(bord),getColor(centre)});  //pour V2 effet passer en TOP_BOTTOM et mettre getColor(bord),getColor(centre),getColor(bord)
        gd.setCornerRadius(0f);
        //gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        //gd.setGradientRadius(200.0f);

        checkbox.setTextColor(Color.BLACK);
        int[] colorClickBox=new int[]{Color.BLACK,Color.BLACK};
        //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },colorClickBox

        );
        checkbox.setButtonTintList(colorStateList);
        checkbox.setBackground(gd);
    }

    public Integer to_int(String key){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            value=0;
        }
        return value;
    }
}

