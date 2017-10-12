package stellarnear.yfa_dealer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        buildSpellsLists();





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void buildSpellsLists() {

        ListSpell ListSpell = new ListSpell(getApplicationContext());
        List<Spell> rank_list = new ArrayList<Spell>();
        Integer [] all_GridIds={R.id.grid1,R.id.grid2,R.id.grid3,R.id.grid4,R.id.grid5};
        for (int r=1;r<=all_GridIds.length;r++){
            rank_list=ListSpell.selectRank(r);
            for(Spell spell : rank_list){
                addSpell(spell.getName(),all_GridIds[r-1]);
            }
        }
        
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addSpell(String Name,Integer GridId) {
        LinearLayout grid= (LinearLayout) this.findViewById(GridId);
        CheckBox spell=new CheckBox(getApplicationContext());
        spell.setText(Name);
        spell.setTextColor(Color.GRAY);
        grid.addView(spell);
    }
}
