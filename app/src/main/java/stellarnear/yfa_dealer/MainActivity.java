package stellarnear.yfa_dealer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Map<Spell,CheckBox> map_spell_check=new HashMap<Spell,CheckBox>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildPage1();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builPage2();

            }
        });
    }



    private void buildPage1() {

        ListSpell ListAllSpell = new ListSpell(getApplicationContext());
        List<Spell> rank_list = new ArrayList<Spell>();
        for(int i=1;i<=9;i++){
            LinearLayout Tiers=(LinearLayout) findViewById(R.id.linear1);
            TextView Tier= new TextView(this);
            Tier.setText("Tier "+i);
            Tier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            Tier.setTextColor(Color.BLACK);
            Tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            Tier.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            Tiers.addView(Tier);

            View h_sep = new View(this);
            h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep);


            HorizontalScrollView scroll_spells= new HorizontalScrollView(this);
            scroll_spells.setHorizontalScrollBarEnabled(false);
            scroll_spells.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            Tiers.addView(scroll_spells);

            LinearLayout grid=new LinearLayout(this);
            scroll_spells.addView(grid);

            View v_sep = new View(this);
            v_sep.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep.setBackgroundColor(Color.GRAY);
            grid.addView(v_sep);

            rank_list= ListAllSpell.selectRank(i);
            for(Spell spell : rank_list){
                CheckBox checkbox=new CheckBox(getApplicationContext());
                checkbox.setText(spell.getName()+" ");
                checkbox.setTextColor(Color.GRAY);
                grid.addView(checkbox);

                map_spell_check.put(spell,checkbox);

                View v_sep2 = new View(this);
                v_sep2.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
                v_sep2.setBackgroundColor(Color.GRAY);
                grid.addView(v_sep2);
            }
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep2);
        }
    }


    private void builPage2() {
        List<Spell> sel_list = new ArrayList<Spell>();
        Iterator iter = map_spell_check.keySet().iterator();
        while(iter.hasNext()) {
            Spell spell=(Spell)iter.next();
            CheckBox checkbox=(CheckBox)map_spell_check.get(spell);
            if (checkbox.isChecked()){
                sel_list.add(spell);
            }
        }
        Intent intent = new Intent(getApplicationContext(), SpellCastActivity.class);
        intent.putExtra("selected_spells", (Serializable) sel_list);
        startActivity(intent);
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
}
