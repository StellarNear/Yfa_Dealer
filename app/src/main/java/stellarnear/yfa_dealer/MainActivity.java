package stellarnear.yfa_dealer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Map<Spell,CheckBox> map_spell_check=new LinkedHashMap<Spell,CheckBox>();
    SpellPerDay spell_per_day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setBackgroundResource(R.drawable.banner_background);
        setSupportActionBar(toolbar);

        this.spell_per_day=new SpellPerDay(getApplicationContext());

        this.spell_per_day.load_list_spell_per_day(getApplicationContext());

        buildPage1();

        ImageButton fab = (ImageButton) findViewById(R.id.fab);

        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builPage2();

            }
        });
    }



    private void buildPage1() {
        ListSpell ListAllSpell = new ListSpell(getApplicationContext());
        List<Spell> rank_list  = new ArrayList<Spell>();

        for(int i=1;i<=10;i++){
            LinearLayout Tiers=(LinearLayout) findViewById(R.id.linear1);
            TextView Tier= new TextView(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {0xFF585858,0xFFE6E6E6});
            gd.setCornerRadius(0f);
            Tier.setBackground(gd);

            String tier_txt="Tier "+i;
            String titre_tier=tier_txt +" ["+ spell_per_day.getSpell_per_day_rank(i)+" restant(s)]";
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
            v_sep.setBackgroundColor(Color.BLACK);
            grid.addView(v_sep);

            for(final Spell spell : rank_list){
                final CheckBox checkbox=new CheckBox(getApplicationContext());

                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux tu lancer ce sort une fois de plus ?")
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


                setCheckBoxColor(checkbox,spell);
                checkbox.setText(spell.getName()+" ");
                grid.addView(checkbox);

                map_spell_check.put(spell,checkbox);

                View v_sep2 = new View(this);
                v_sep2.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
                v_sep2.setBackgroundColor(Color.BLACK);
                grid.addView(v_sep2);
            }
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep2);
        }
    }


    private void builPage2() {
        List<Spell> sel_list= new ArrayList<Spell>();
        Iterator iter = map_spell_check.keySet().iterator();
        Boolean spell_casted=false;

        while(iter.hasNext()) {
            Spell spell=(Spell)iter.next();
            CheckBox checkbox=(CheckBox)map_spell_check.get(spell);
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
            Intent intent = new Intent(getApplicationContext(), SpellCastActivity.class);
            intent.putExtra("selected_spells", (Serializable) sel_list);
            startActivity(intent);
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

    public void setCheckBoxColor(CheckBox checkbox,Spell spell) {
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
                new int[] {getColor(centre),getColor(bord)});
        gd.setCornerRadius(0f);
        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setGradientRadius(200.0f);

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

