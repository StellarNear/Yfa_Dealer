package stellarnear.yfa_dealer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

public class DisplayDamageDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        setContentView(R.layout.damagedetail);

        String all_dices_str = getIntent().getStringExtra("all_dices_str");
        display_dmg_detail(all_dices_str);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_damage_detail_ondetlay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }});


    }

    private void display_dmg_detail(String param_all_dices_str){
        GridLayout scroll = (GridLayout) findViewById(R.id.grid_scroll) ;

        Log.d("STATE all",param_all_dices_str);
        List<String> dices = Arrays.asList(param_all_dices_str.split(","));

        for ( String dice_str : dices) {


            Log.d("STATE indi", dice_str);
            ImageView dice = new ImageView(getApplicationContext());
            dice.setImageResource(ImageAdapter.dice_map.get(dice_str));
            scroll.addView(dice);

        }


    }

}
