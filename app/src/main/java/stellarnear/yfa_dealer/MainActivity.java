package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormatSymbols;
import java.util.Iterator;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Spells.BuildSpellList;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;

public class MainActivity extends AppCompatActivity {
    private SpellList selectedSpells=new SpellList();
    private boolean shouldExecuteOnResume;
    private Targets targets;
    public static Perso yfa;
    private Tools tools=new Tools();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldExecuteOnResume = false;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setBackgroundResource(R.drawable.banner_background);
        setSupportActionBar(toolbar);
        yfa=new Perso(getApplicationContext());
        targets = Targets.getInstance();

        buildPage1();

        ImageButton fab = (ImageButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedSpells.isEmpty()){
                    targets.clearTargets();
                    testSpellSelection();
                } else {
                    Toast toast =  Toast.makeText(getApplicationContext(), "Sélectionnes au moins un sort ...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        ((TextView) findViewById(R.id.mythic_pts_txt)).setText(String.valueOf(yfa.getResourceValue("mythic_points")));

        ((FrameLayout) findViewById(R.id.mythic_pts)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if( yfa.getResourceValue("mythic_points")>0) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Demande de confirmation")
                            .setMessage("Confirmes-tu la dépense d'un point mythique ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    yfa.getAllResources().getResource("mythic_points").spend(1);
                                    refreshMythicPoints();
                                    tools.customToast(getApplicationContext(),"Il te reste "+yfa.getResourceValue("mythic_points")+" point(s) mythique(s)","center");
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    tools.customToast(getApplicationContext(),"Tu n'as plus de point mythique","center");
                }
                return true;
            }
        });
    }

    private void refreshMythicPoints() {
        ((TextView) findViewById(R.id.mythic_pts_txt)).setText(String.valueOf(yfa.getResourceValue("mythic_points")));
    }

    @Override
    protected void onResume() {
        super.onResume();
        yfa.refresh();
        if(shouldExecuteOnResume){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(R.anim.infromleft,R.anim.nothing);
        } else{
            shouldExecuteOnResume = true;
        }
    }

    private void buildPage1() {
        final SpellList listAllSpell = new BuildSpellList(getApplicationContext(),"").getSpellList();
        final SpellList listAllMythicSpell = new BuildSpellList(getApplicationContext(),"Mythic").getSpellList();

        int max_tier=0;
        for(int i=0;i<=19;i++){
            try{
                if (yfa.getAllResources().getResource("spell_rank_"+i).getCurrent()>0) {max_tier=i;}
            } catch (Exception e){ }
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

            String titre_tier=tier_txt +" ["+ yfa.getResourceValue("spell_rank_"+i)+" restant(s)]";
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
            final TextView side_txt=new TextView(this);
            side_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            side_txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));

            int orientation = getResources().getConfiguration().orientation;  //1 pour portrait et 2 paysage
            side_txt.setTextColor(Color.DKGRAY);
            if (orientation==1) {
                side_txt.setText("T" + i + "\n(" + yfa.getResourceValue("spell_rank_"+i) + ")");
                if (i==0){side_txt.setText("T"+i+"\n("+ DecimalFormatSymbols.getInstance().getInfinity()+")");}
                else if (yfa.getAllResources().checkConvertibleAvailable(i)) {
                    String n_spell_conv_txt="T" + i + "\n(" + yfa.getResourceValue("spell_rank_"+i)+","+ yfa.getResourceValue("spell_conv_rank_"+i) + ")";
                    String before_conv="T" + i + "\n(" + yfa.getResourceValue("spell_rank_"+i)+",";
                    SpannableString n_spell_conv=  new SpannableString(n_spell_conv_txt);
                    n_spell_conv.setSpan(new ForegroundColorSpan(getColor(R.color.conversion)),before_conv.length(),before_conv.length()+yfa.getResourceValue("spell_conv_rank_"+i).toString().length(), 0);// set color2
                    side_txt.setText(n_spell_conv);
                }
            } else {
                side_txt.setText("T" + i + " (" + yfa.getResourceValue("spell_rank_"+i) + ")");
                if (i==0){side_txt.setText("T"+i+" ("+ DecimalFormatSymbols.getInstance().getInfinity()+")");}
                if (yfa.getAllResources().checkConvertibleAvailable(i)) {
                    String n_spell_conv_txt="T" + i + " (" + yfa.getResourceValue("spell_rank_"+i)+","+ yfa.getResourceValue("spell_conv_rank_"+i) + ")";
                    String before_conv="T" + i + " (" + yfa.getResourceValue("spell_rank_"+i)+",";
                    SpannableString n_spell_conv=  new SpannableString(n_spell_conv_txt);
                    n_spell_conv.setSpan(new ForegroundColorSpan(getColor(R.color.conversion)),before_conv.length(),before_conv.length()+yfa.getResourceValue("spell_conv_rank_"+i).toString().length(), 0);// set color2
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
            final int rank=i;
            side_txt.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(yfa.getResourceValue("mythic_points")>0) {

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Arcane libre")
                                .setMessage("Veux tu lancer utiliser arcane libre pour lancer un sort de rang " + rank + " ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        Snackbar.make(side_txt,"Arcane libre de rang " + rank + " lancé.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        yfa.getAllResources().getResource("mythic_points").spend(1);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Il te reste " + yfa.getResourceValue("mythic_points") + " point(s) mythique(s)", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }
                                })
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).show();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Tu n'as plus de point mythique ...", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                    return true;
                }
            });

            side.addView(side_txt);


            View h_sep = new View(this);
            h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep);
            SpellList rank_list= listAllSpell.filterByRank(i).filterDisplayable();
            if (rank_list.size()==0){ continue;}

            for(final Spell spell : rank_list.asList()){
                final CheckBox checkbox=new CheckBox(getApplicationContext());
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            if(spell.getNSubSpell()>0){
                                Spell parentSpellToBind=null;
                                for (int i=1;i<=spell.getNSubSpell();i++){
                                    SpellList subSpellN= listAllSpell.getSpellByID(spell.getID()+"_sub");
                                    subSpellN.setSubName(i);
                                    if(subSpellN.asList().size()>0 && parentSpellToBind==null){parentSpellToBind=subSpellN.asList().get(0);}
                                    if(parentSpellToBind!=null){
                                        subSpellN.bindTo(parentSpellToBind);
                                    }
                                    selectedSpells.add(subSpellN);
                                }
                            } else {
                                selectedSpells.add(new Spell(spell));
                            }
                            current_spell_display(getApplicationContext());
                        }

                        if (!isChecked) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu tu lancer lancer une nouvelle fois le sort "+spell.getName()+" ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            checkbox.setChecked(true);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            removeSpellFromSelection(spell);
                                        }}).show();
                        }
                    }
                });

                setCheckBoxColor(checkbox,spell);
                Tiers.addView(checkbox);
                if (listAllMythicSpell.hasSpellID(spell.getName())){
                    checkbox.setText(spell.getName());
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_embrassed_energy), null);
                    checkbox.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if(event.getRawX() <= checkbox.getLeft() + Math.round(TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()))){
                                    checkbox.setChecked(!checkbox.isChecked());

                                } else if (event.getRawX() >= checkbox.getRight() - Math.round(TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()))) {
                                    // your action for drawable click event
                                    if(yfa.getResourceValue("mythic_points")>0) {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Demande de confirmation")
                                                .setMessage("Point(s) mythique(s) actuel(s) : " + yfa.getResourceValue("mythic_points") + "\n" +
                                                        "\nVeux tu lancer la version mythique du sort " + spell.getName() + " (cout : 1pt) ?")
                                                .setIcon(android.R.drawable.ic_menu_help)
                                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        if(spell.getNSubSpell()>0){
                                                            Spell parentSpellToBind=null;
                                                            for (int i=1;i<=spell.getNSubSpell();i++){
                                                                SpellList subSpellN=listAllMythicSpell.getSpellByID(spell.getID()+"_sub");
                                                                subSpellN.setSubName(i);
                                                                if(subSpellN.asList().size()>0 && parentSpellToBind==null){parentSpellToBind=subSpellN.asList().get(0);}
                                                                if(parentSpellToBind!=null){
                                                                    subSpellN.bindTo(parentSpellToBind);
                                                                }
                                                                selectedSpells.add(subSpellN);
                                                            }
                                                        } else {
                                                            selectedSpells.add(listAllMythicSpell.getSpellByID(spell.getName()));
                                                        }
                                                        yfa.getAllResources().getResource("mythic_points").spend(1);
                                                        refreshMythicPoints();
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Il te reste " + yfa.getResourceValue("mythic_points") + " point(s) mythique(s)", Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                        toast.show();
                                                        current_spell_display(getApplicationContext());
                                                    }
                                                })
                                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                    }
                                                }).show();
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Tu n'as plus de point mythique ...", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }
                                    return true;
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), spell.getDescr(), Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                }
                            }
                            return true;
                        }
                    });
                } else {
                    checkbox.setText(spell.getName());
                    checkbox.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (event.getRawX() <= checkbox.getLeft() + Math.round(TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()))) {
                                    checkbox.setChecked(!checkbox.isChecked());

                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), spell.getDescr(), Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                }
                            }
                            return true;
                        }
                    });
                }
            }
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.BLACK);
            Tiers.addView(h_sep2);
        }
    }

    private void removeSpellFromSelection(Spell spell) {
        Iterator <Spell> s = selectedSpells.iterator();
        while(s.hasNext()){
            Spell spellList=s.next();
            boolean spellInList = spellList.getName().equalsIgnoreCase(spell.getName());
            boolean subSpellInList = spell.getNSubSpell()>0 && spellList.getID().equalsIgnoreCase(spell.getName()+"_sub");
            if(spellInList || subSpellInList){
                s.remove();
            }
        }
    }

    private void current_spell_display(Context mC) {
        String display="Sort séléctionnés :\n";
        for(Spell spell : selectedSpells.asList()){
            display+=spell.getName()+"\n";
        }
        display = display.substring(0, display.length() - 1);
        Toast toast =  Toast.makeText(mC, display, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    private void testSpellSelection() {
        if (!selectedSpells.isEmpty()) {
            askNTarget();
        } else { startActivity(new Intent(this, MainActivity.class));}
    }

    private void askNTarget() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View mainView = inflater.inflate(R.layout.target_naming,null);
        CustomAlertDialog targetDialog = new CustomAlertDialog(this,getApplicationContext(),mainView);
        final LinearLayout listName = mainView.findViewById(R.id.target_list_names);

        targetDialog.setPermanent(true);
        targetDialog.clickToHide(mainView.findViewById(R.id.target_frame));
        targetDialog.addConfirmButton("Valider");
        targetDialog.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                if(listName.getChildCount()>1) {
                    for (int index = 0; index < listName.getChildCount(); index++) {
                        String nameTar = ((EditText) listName.getChildAt(index)).getText().toString();
                        if (nameTar.equalsIgnoreCase("")) {
                            nameTar=((EditText) listName.getChildAt(index)).getHint().toString();
                        }
                        targets.addTarget(nameTar);
                    }
                    showTargetDragAndDrop();
                } else { //on assigne tout à mainTarget
                    String nameTar = ((EditText) listName.getChildAt(0)).getText().toString();
                    if (nameTar.equalsIgnoreCase("")) {
                        nameTar=((EditText) listName.getChildAt(0)).getHint().toString();
                    }
                    targets.assignAllToMain(nameTar,selectedSpells);
                    buildPage2();
                }
            }
        });

        Button addTarget = mainView.findViewById(R.id.addTarget);
        addTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit = new EditText(getApplicationContext());
                edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                int i = listName.getChildCount();
                edit.setHint("Cible "+i);
                edit.setMinEms(7);
                listName.addView(edit);
            }
        });

        targetDialog.showAlert();
    }

    private void showTargetDragAndDrop() {
        MyDragAndDrop myDragAndDrop = new MyDragAndDrop(getApplicationContext());

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View mainView = inflater.inflate(R.layout.target_drag_drop,null);

        CustomAlertDialog targetDialog = new CustomAlertDialog(this,getApplicationContext(),mainView);
        targetDialog.setPermanent(true);
        targetDialog.clickToHide(mainView.findViewById(R.id.target_frame));


        LinearLayout spellListLin = mainView.findViewById(R.id.target_list_spells);
        LinearLayout targetLin = mainView.findViewById(R.id.target_list_targets);

        for (Spell spell : selectedSpells.asList()){
            TextView t = new TextView(getApplicationContext());
            t.setText(spell.getName());
            t.setTextSize(18);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            myDragAndDrop.setTouchListner(t,spell);
            spellListLin.addView(t);
        }

        for (String tar : targets.getTargetList()){
            LinearLayout fram = new LinearLayout(getApplicationContext());
            fram.setGravity(Gravity.CENTER);
            fram.setOrientation(LinearLayout.VERTICAL);
            fram.setPadding(5,50,5,50);
            fram.setBackground(getDrawable(R.drawable.target_basic_gradient));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT );
            params.setMargins(0,5,0,0);
            fram.setLayoutParams(params);
            TextView t = new TextView(getApplicationContext());
            t.setText(tar);
            t.setTextColor(Color.DKGRAY);
            t.setTextSize(20);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fram.addView(t);
            myDragAndDrop.setDragListner(fram,tar); //à faire un truc ennemi
            targetLin.addView(fram);
        }
        targetDialog.addConfirmButton("Valider");
        targetDialog.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                if (targets.anySpellAssigned()){
                    buildPage2();
                } else {
                    Toast toast =  Toast.makeText(getApplicationContext(), "Aucun sort n'est attribué à une cible ...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        targetDialog.showAlert();
    }
    private void buildPage2(){
        Intent intent = new Intent(this, SpellCastActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.infromright,R.anim.nothing);
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

        if (spell.getDmg_type().equals("aucun")) {centre=R.color.aucun_dark;bord=R.color.aucun;}
        else if (spell.getDmg_type().equals("feu")) {centre=R.color.feu_dark;bord=R.color.feu;}
        else if (spell.getDmg_type().equals("foudre")) {centre=R.color.foudre_dark;bord=R.color.foudre;}
        else if (spell.getDmg_type().equals("froid")) {centre=R.color.froid_dark;bord=R.color.froid;}
        else if (spell.getDmg_type().equals("acide")) {centre=R.color.acide_dark;bord=R.color.acide;}
        else {centre=R.color.white;bord=R.color.white;}

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {getColor(bord),getColor(centre)});  //pour V2 effet passer en TOP_BOTTOM et mettre getColor(bord),getColor(centre),getColor(bord)
        gd.setCornerRadius(0f);

        checkbox.setTextColor(Color.BLACK);
        int[] colorClickBox=new int[]{Color.BLACK,Color.BLACK};

        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },colorClickBox

        );
        checkbox.setButtonTintList(colorStateList);
        checkbox.setBackground(gd);
    }
}

