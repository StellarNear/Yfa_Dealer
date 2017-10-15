package stellarnear.yfa_dealer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Utilisateur on 15/10/2017.
 */

public class SpellCast extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell_cast);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

        Intent i = getIntent();
        List<Spell> selected_spells = (List<Spell>) i.getSerializableExtra("selected_spells");
        LinearLayout page2 = (LinearLayout) findViewById(R.id.linear2);


        for (Spell spell : selected_spells) {
            TextView Spell_Title = new TextView(this);
            Spell_Title.setText(spell.getName());
            Spell_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            Spell_Title.setTextColor(Color.BLACK);
            Spell_Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            Spell_Title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            page2.addView(Spell_Title);
        }
    }




}

