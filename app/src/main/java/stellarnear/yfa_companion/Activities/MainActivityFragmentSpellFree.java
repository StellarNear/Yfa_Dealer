package stellarnear.yfa_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormatSymbols;
import java.util.Iterator;

import stellarnear.yfa_companion.CustomAlertDialog;
import stellarnear.yfa_companion.MyDragAndDrop;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.PostData;
import stellarnear.yfa_companion.PostDataElement;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Spells.BuildSpellList;
import stellarnear.yfa_companion.Spells.EchoList;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Spells.SpellList;
import stellarnear.yfa_companion.Targets;
import stellarnear.yfa_companion.Tools;

public class MainActivityFragmentSpellFree extends Fragment {
    private SpellList selectedSpells=new SpellList();
    private Targets targets;
    private Perso yfa=MainActivity.yfa;
    private View returnFragView;
    private SpellList listAllSpell=null;
    private Tools tools=Tools.getTools();

    public MainActivityFragmentSpellFree() {
        //todo les spend cast de sort libre sont que ne point myth
        //todo les calcul de meta max pour les sort libre sont different ?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView= inflater.inflate(R.layout.fragment_main_cast_free, container, false);
        targets = Targets.getInstance();

        buildPage1();

        ImageButton fab = (ImageButton) returnFragView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedSpells.isEmpty()){
                    targets.clearTargets();
                    testSpellSelection();
                } else {
                    Toast toast =  Toast.makeText(getContext(), "Sélectionnes au moins un sort ...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        returnFragView.findViewById(R.id.Titre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSpellList();
            }
        });

        ((FrameLayout) returnFragView.findViewById(R.id.back_main_from_spell)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain();
            }
        });

        return returnFragView;
    }

    private void backToMain() {
        unlockOrient();
        Fragment fragment = new MainActivityFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtorightfrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void unlockOrient() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }


    private void buildPage1() {

        listAllSpell=BuildSpellList.getInstance(getContext()).getSpellList(); //todo free list

        int max_tier=yfa.getAllResources().getRankManager().getHighestTier(); //todo max rank free list
        for(int i=0;i<=max_tier;i++){
            LinearLayout tiers=(LinearLayout) returnFragView.findViewById(R.id.linear1);
            final TextView tierTxt= new TextView(getContext());
            LinearLayout.LayoutParams para= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int pixelMarging = getContext().getResources().getDimensionPixelSize(R.dimen.general_margin);
            para.setMargins(pixelMarging,pixelMarging,pixelMarging,pixelMarging);
            tierTxt.setLayoutParams(para);
            tierTxt.setBackground(getContext().getDrawable(R.drawable.background_tier_title));

            String tier_txt="Tier "+i;
            tierTxt.setText(tier_txt);

            tierTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tierTxt.setTextColor(Color.BLACK);
            tierTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tiers.addView(tierTxt);

            SpellList rank_list= listAllSpell.getNormalSpells().filterByRank(i).filterDisplayable();
            if (rank_list.size()==0){ continue;}

            for(final Spell spell : rank_list.asList()){


                LinearLayout spellLine = new LinearLayout(getContext());
                spellLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast toast = Toast.makeText(getContext(), spell.getDescr(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                });
                spellLine.setOrientation(LinearLayout.HORIZONTAL);
                setSpellLineColor(spellLine,spell);
                LinearLayout.LayoutParams paraSpellLine= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paraSpellLine.setMargins(pixelMarging,pixelMarging,pixelMarging,0);
                spellLine.setLayoutParams(paraSpellLine);

                final CheckBox checkbox=new CheckBox(getContext());
                setAddingSpell(checkbox,spell);
                setCheckBoxColor(checkbox);
                spellLine.addView(checkbox);
                TextView spellName = new TextView(getContext());
                spellName.setText(spell.getName());
                spellName.setTextColor(Color.BLACK);
                spellName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkbox.setChecked(!checkbox.isChecked());
                    }
                });
                spellLine.addView(spellName);
                tiers.addView(spellLine);
            }
        }
    }

    private void setAddingSpell(final CheckBox check, final Spell spell) {
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    new AlertDialog.Builder(getContext())
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
            if(yfa.getResourceValue("resource_mythic_points")>0) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Demande de confirmation")
                        .setMessage("Point(s) mythique(s) avant lancement des sorts : " + yfa.getResourceValue("resource_mythic_points") + "\n" +
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
                Toast toast = Toast.makeText(getContext(), "Tu n'as plus de point mythique ...", Toast.LENGTH_LONG);
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
        currentSelectionDisplay(getContext());
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
        currentSelectionDisplay(getContext());
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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (settings.getBoolean("switch_multi_target",getContext().getResources().getBoolean(R.bool.switch_multi_target_def))) {
            askNTarget();
        } else {
            targets.assignAllToMain("Cible principale",selectedSpells);
            goToCasting();
        }
    }

    private void askNTarget() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mainView = inflater.inflate(R.layout.target_naming,null);
        CustomAlertDialog targetDialog = new CustomAlertDialog(getActivity(),getContext(),mainView);
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
                    goToCasting();
                }
            }
        });

        Button addTarget = mainView.findViewById(R.id.addTarget);
        addTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit = new EditText(getContext());
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
        MyDragAndDrop myDragAndDrop = new MyDragAndDrop(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mainView = inflater.inflate(R.layout.target_drag_drop,null);

        CustomAlertDialog targetDialog = new CustomAlertDialog(getActivity(),getContext(),mainView);
        targetDialog.setPermanent(true);
        targetDialog.clickToHide(mainView.findViewById(R.id.target_frame));

        LinearLayout spellListLin = mainView.findViewById(R.id.target_list_spells);
        LinearLayout targetLin = mainView.findViewById(R.id.target_list_targets);

        for (Spell spell : selectedSpells.asList()){
            TextView t = new TextView(getContext());
            t.setText(spell.getName());
            t.setTextSize(18);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            myDragAndDrop.setTouchListner(t,spell);
            spellListLin.addView(t);
        }

        for (String tar : targets.getTargetList()){
            LinearLayout fram = new LinearLayout(getContext());
            fram.setGravity(Gravity.CENTER);
            fram.setOrientation(LinearLayout.VERTICAL);
            fram.setPadding(5,50,5,50);
            fram.setBackground(getContext().getDrawable(R.drawable.target_basic_gradient));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT );
            params.setMargins(0,5,0,0);
            fram.setLayoutParams(params);
            TextView t = new TextView(getContext());
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
                    goToCasting();
                } else {
                    Toast toast =  Toast.makeText(getContext(), "Aucun sort n'est attribué à une cible ...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        targetDialog.showAlert();
    }


    private void goToSpellList(){
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infrombotfrag,R.animator.outfadefrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, new MainActivityFragmentSpell(),"frag_spell");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void goToCasting(){
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infromrightfrag,R.animator.outfadefrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, new MainActivityFragmentSpellCast(),"frag_spell_cast");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void setSpellLineColor(LinearLayout line,Spell spell) {
        if (spell.getDmg_type().equals("none")) {
            line.setBackground(getContext().getDrawable(R.drawable.background_spell_line_noelem));
        } else if (spell.getDmg_type().equals("fire")) {
            line.setBackground(getContext().getDrawable(R.drawable.background_spell_line_fire));
        } else if (spell.getDmg_type().equals("shock")) {
            line.setBackground(getContext().getDrawable(R.drawable.background_spell_line_shock));
        } else if (spell.getDmg_type().equals("frost")) {
            line.setBackground(getContext().getDrawable(R.drawable.background_spell_line_frost));
        } else if (spell.getDmg_type().equals("acid")) {
            line.setBackground(getContext().getDrawable(R.drawable.background_spell_line_acid));
        } else {
            //line.setBackground(getContext().getDrawable(R.drawable.background_spell_line_nodmg));
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

