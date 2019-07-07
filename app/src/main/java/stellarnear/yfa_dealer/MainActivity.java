package stellarnear.yfa_dealer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private SpellList listAllSpell=null;

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

        ((FrameLayout) findViewById(R.id.mythic_pts)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    private void refreshMythicPoints() {
        ((TextView) findViewById(R.id.mythic_pts_txt)).setText(String.valueOf(yfa.getResourceValue("mythic_points")));
    }

    @SuppressLint("ClickableViewAccessibility")
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
        listAllSpell=BuildSpellList.getInstance(getApplicationContext()).getSpellList();

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
            LinearLayout.LayoutParams para= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int pixelMarging = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.general_margin);
            para.setMargins(pixelMarging,pixelMarging,pixelMarging,pixelMarging);
            Tier.setLayoutParams(para);
            Tier.setBackground(getApplicationContext().getDrawable(R.drawable.background_tier_title));

            String tier_txt="Tier "+i;

            String titre_tier=tier_txt +" ["+ yfa.getResourceValue("spell_rank_"+i)+" restant(s)]";
            if (i==0){titre_tier=tier_txt +" [illimité]";}
            SpannableString titre=  new SpannableString(titre_tier);
            titre.setSpan(new RelativeSizeSpan(0.65f), tier_txt.length(),titre_tier.length(), 0);
            Tier.setText(titre);

            Tier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            Tier.setTextColor(Color.BLACK);
            Tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            Tiers.addView(Tier);

            // side bar
            LinearLayout side=(LinearLayout) findViewById(R.id.side_bar);
            side.setElevation(10);
            side.setBackground(getApplicationContext().getDrawable(R.drawable.background_side_bar));
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

                                        Snackbar.make(side_txt,"Arcane libre de rang " + rank + " lancé.\n(+2 NLS sur ce sort)", Snackbar.LENGTH_LONG)
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


            SpellList rank_list= listAllSpell.getNormalSpells().filterByRank(i).filterDisplayable();
            if (rank_list.size()==0){ continue;}

            for(final Spell spell : rank_list.asList()){
                LinearLayout spellLine = new LinearLayout(getApplicationContext());
                spellLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast toast = Toast.makeText(getApplicationContext(), spell.getDescr(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                });
                spellLine.setOrientation(LinearLayout.HORIZONTAL);
                setSpellLineColor(spellLine,spell);
                LinearLayout.LayoutParams paraSpellLine= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paraSpellLine.setMargins(pixelMarging,pixelMarging,pixelMarging,0);
                spellLine.setLayoutParams(paraSpellLine);

                final CheckBox checkbox=new CheckBox(getApplicationContext());
                setAddingSpell(checkbox,spell);
                setCheckBoxColor(checkbox);
                spellLine.addView(checkbox);
                TextView spellName = new TextView(getApplicationContext());
                spellName.setText(spell.getName());
                spellName.setTextColor(Color.BLACK);
                spellName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkbox.setChecked(!checkbox.isChecked());
                    }
                });
                spellLine.addView(spellName);
                final Spell mythicSpell = listAllSpell.getMythicSpells().getNormalSpellFromID(spell.getID());
                if (mythicSpell!=null){
                    LinearLayout mythLine =  new LinearLayout(getApplicationContext());
                    mythLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
                    int px = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.general_margin),    getResources().getDisplayMetrics()    );
                    mythLine.setPadding(0,0,px,0);
                    mythLine.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                    final CheckBox checkMyth = new CheckBox(getApplicationContext());
                    setCheckBoxColor(checkMyth);
                    setAddingSpell(checkMyth,mythicSpell);
                    mythLine.addView(checkMyth);
                    ImageView img = new ImageView(getApplicationContext());
                    img.setImageDrawable(getDrawable(R.drawable.ic_embrassed_energy));
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkMyth.setChecked(!checkMyth.isChecked());
                        }
                    });
                    img.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            tools.customToast(getApplicationContext(),mythicSpell.getDescr(),"center");
                            return true;
                        }
                    });
                    mythLine.addView(img);
                    spellLine.addView(mythLine);
                }
                Tiers.addView(spellLine);
            }
        }
    }

    private void setAddingSpell(final CheckBox check, final Spell spell) {
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Demande de confirmation")
                            .setMessage("Veux-tu tu lancer une nouvelle fois le sort "+spell.getName()+" ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    check.setChecked(true);
                                }})
                            .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    check.setChecked(false);
                                    removeSpellFromSelection(spell);
                                }}).show();
                } else {
                    prepareSpell(check,spell);
                }
            }
        });
    }

    private void prepareSpell(final CheckBox check, final Spell spell) {
        if(spell.isMyth()){
            if(yfa.getResourceValue("mythic_points")>0) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Demande de confirmation")
                        .setMessage("Point(s) mythique(s) avant lancement des sorts : " + yfa.getResourceValue("mythic_points") + "\n" +
                                "\nVeux tu préparer la version mythique du sort " + spell.getName() + "\n(cela coûtera 1 pt) ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                check.setChecked(true);
                                addSpellToList(spell);
                            }})
                        .setNegativeButton("non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                check.setChecked(false);
                            }
                        }).show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Tu n'as plus de point mythique ...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                check.setChecked(false);
            }
        }else {
            addSpellToList(spell);
        }
    }

    private void addSpellToList(Spell spell) {
        if (spell.getNSubSpell() > 0) {
            Spell parentSpellToBind = null;
            for (int i = 1; i <= spell.getNSubSpell(); i++) {
                SpellList subSpellN = listAllSpell.getSpellByID(spell.getID() + "_sub");
                subSpellN.setSubName(i);
                if (subSpellN.asList().size() > 0 && parentSpellToBind == null) {
                    parentSpellToBind = subSpellN.asList().get(0);
                }
                if (parentSpellToBind != null) {
                    subSpellN.bindTo(parentSpellToBind);
                }
                selectedSpells.add(subSpellN);
            }
        } else {
            selectedSpells.add(new Spell(spell));
        }
        currentSelectionDisplay(getApplicationContext());
    }

    private void removeSpellFromSelection(Spell spell) {
        Iterator <Spell> s = selectedSpells.iterator();
        SpellList parentSpellRemovedList=new SpellList();
        while(s.hasNext()){
            Spell spellList=s.next();
            boolean spellInList = spellList.getID().equalsIgnoreCase(spell.getID());
            boolean subSpellInList = spell.getNSubSpell()>0 && spellList.getID().equalsIgnoreCase(spell.getID()+"_sub");

            if(spellInList || subSpellInList){
                s.remove();
                if(spellInList){
                    parentSpellRemovedList.add(spellList);
                } else if(subSpellInList && !parentSpellRemovedList.contains(spellList.getBindedParent())){
                    parentSpellRemovedList.add(spellList.getBindedParent());
                }
            }
        }
        currentSelectionDisplay(getApplicationContext());
    }

    private void currentSelectionDisplay(Context mC) {
        if(selectedSpells.isEmpty()){
            Toast toast = Toast.makeText(mC, "Aucun sort séléctionné", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            String display = "Sort séléctionnés :\n";
            for (Spell spell : selectedSpells.asList()) {
                display += spell.getName() + "\n";
            }
            display = display.substring(0, display.length() - 1);
            Toast toast = Toast.makeText(mC, display, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
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

    public void setSpellLineColor(LinearLayout line,Spell spell) {
        if (spell.getDmg_type().equals("aucun")) {
            line.setBackground(getApplicationContext().getDrawable(R.drawable.background_spell_line_noelem));
        } else if (spell.getDmg_type().equals("feu")) {
            line.setBackground(getApplicationContext().getDrawable(R.drawable.background_spell_line_fire));
        } else if (spell.getDmg_type().equals("foudre")) {
            line.setBackground(getApplicationContext().getDrawable(R.drawable.background_spell_line_shock));
        } else if (spell.getDmg_type().equals("froid")) {
            line.setBackground(getApplicationContext().getDrawable(R.drawable.background_spell_line_frost));
        } else if (spell.getDmg_type().equals("acide")) {
            line.setBackground(getApplicationContext().getDrawable(R.drawable.background_spell_line_acid));
        } else {
            //line.setBackground(getApplicationContext().getDrawable(R.drawable.background_spell_line_nodmg));
        }

    }

    public void setCheckBoxColor(CheckBox checkbox) {
        checkbox.setTextColor(Color.BLACK);
        int[] colorClickBox=new int[]{Color.BLACK,Color.BLACK};

        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },colorClickBox

        );
        checkbox.setButtonTintList(colorStateList);

    }
}

