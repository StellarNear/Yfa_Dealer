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

    private void buildSpellsLists() {


        //faire une liste des ID grille
        //construrie la liste all spell
        //appelle dans une boucle de la grille x et de la lsite x


        Integer Grid1Id=R.id.grid1;
        addSpell("Projéctile magique",Grid1Id);
        addSpell("Mains brûlantes",Grid1Id);
        addSpell("Rayon affaiblissant",Grid1Id);
        addSpell("Restauration de Cadavre",Grid1Id);
        addSpell("Contact Glacial",Grid1Id);

        Integer Grid2Id= R.id.grid2;
        addSpell("Main spectral",Grid2Id);
        addSpell("Simulacre de Vie",Grid2Id);
        addSpell("Souffle de feu",Grid2Id);
        addSpell("Ténèbre",Grid2Id);
        addSpell("Sphère de feu",Grid2Id);

        Integer Grid3Id= R.id.grid3;
        addSpell("Eclair",Grid3Id);
        addSpell("Mur de vent",Grid3Id);
        addSpell("Boule de feu",Grid3Id);
        addSpell("Baiser du vampire",Grid3Id);
        addSpell("Vol de soins",Grid3Id);

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

    private void addSpell(String Name,Integer GridId) {
        LinearLayout grid= (LinearLayout) this.findViewById(GridId);
        CheckBox spell=new CheckBox(getApplicationContext());
        spell.setText(Name);
        spell.setTextColor(Color.GRAY);
        grid.addView(spell);
    }
}
