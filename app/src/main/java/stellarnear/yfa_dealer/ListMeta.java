package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by jchatron on 28/11/2017.
 */

public class ListMeta extends AppCompatActivity {

    List<Pair_Meta_Rank> pair_meta_rank= new ArrayList<>();
    boolean all_free=false;

    public ListMeta(final Spell spell, final TextView Spell_Title, final TextView infos,final Context mC,boolean... all_free_arg) {
        if(all_free_arg.length>0){all_free=all_free_arg[0];}
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);

        final SpellPerDay spell_per_day=new SpellPerDay(mC);
        spell_per_day.load_list_spell_per_day(mC);

        // incant rapide (+4)

        if (settings.getBoolean("rapid",mC.getResources().getBoolean(R.bool.rapid_switch_def)))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Incantation rapide (+4)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Rapid(true,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);

                        /*  Meta quickspell pas possible pour perfection
                        if(spell.isPerfect())
                        {
                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Incantation rapide ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Rapid(true);
                                            spell.setRank(spell.getRank()-4);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshTitle(Spell_Title, spell, spell_per_day, mC);
                                            refreshInfos(infos, spell,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Rapid(true);
                                            refreshTitle(Spell_Title, spell, spell_per_day, mC);
                                            refreshInfos(infos, spell,mC);
                                        }}).show();
                        }else {
                            spell.meta_Rapid(true);
                            refreshTitle(Spell_Title, spell, spell_per_day, mC);
                            refreshInfos(infos, spell,mC);
                        }*/

                    } else {
                        spell.meta_Rapid(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Rapid_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),4));
        }

        // sort amélioré +4
        if ((settings.getBoolean("ameliore",mC.getResources().getBoolean(R.bool.ameliore_switch_def)))&&(spell.getDice_typ().contains("d")))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Sort Amélioré (+4)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        if(spell.isPerfect()&&!all_free)
                        {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort Amélioré ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enhance_Spell(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enhance_Spell(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Enhance_Spell(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }
                    } else {
                        spell.meta_Enhance_Spell(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });

            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Enhance_Spell_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),4));
        }

        // sort à retardement +4

        if (settings.getBoolean("delay",mC.getResources().getBoolean(R.bool.delay_switch_def)))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Sort à retardement (+4)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect()&&!all_free)
                        {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort à retardement ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Delay(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Delay(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Delay(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }
                    } else {
                        spell.meta_Delay(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Delay_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),4));
        }

        //quintessence des sorts +3
        if ((settings.getBoolean("quintessence",mC.getResources().getBoolean(R.bool.quintessence_switch_def)))&&(spell.getDice_typ().contains("d")))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Quintessence des sorts (+3)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect()&&!all_free)
                        {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Quintessence des sorts ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Quint(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Quint(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Quint(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }

                    } else {
                        spell.meta_Quint(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Quint_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),3));
        }

        //extension d'effet +2
        if ((settings.getBoolean("extension",mC.getResources().getBoolean(R.bool.extension_switch_def)))&&(spell.getDice_typ().contains("d")))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Extension d'effet (+2)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect()&&!all_free)
                        {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Extension d'effet ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Extend(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }
                    } else {
                        spell.meta_Extend(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Extend_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),2));
        }

        //flêche naturalisée +2
        // si le level du sort est inférieur ou egal au niveau de sort max de wedge && que la méta est active
        if ((spell.getBaseRank() <= to_int(settings.getString("wedge_max_lvl_spell",mC.getResources().getString(R.string.wedge_max_lvl_spell_def)))) && (settings.getBoolean("enchant_arrow",mC.getResources().getBoolean(R.bool.enchant_arrow_switch_def))))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Flêche naturalisée (+2)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect()&&!all_free)
                        {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Flêche naturalisée ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enchant_arrow(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enchant_arrow(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Enchant_arrow(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }
                    } else {
                        spell.meta_Enchant_arrow(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Enchant_arrow_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),2));
        }

        //augementation d'intensité +1
        if (settings.getBoolean("intense",mC.getResources().getBoolean(R.bool.intense_switch_def)))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Augmentation d'intensité (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.getRank()<9) {
                            if(spell.isPerfect()&&!all_free)
                            {
                                new AlertDialog.Builder(mC)
                                        .setTitle("Demande de confirmation")
                                        .setMessage("Veux-tu utiliser ta perfection magique sur Augmentation d'intensité ?")
                                        .setIcon(android.R.drawable.ic_menu_help)
                                        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                int rank = spell.getRank();
                                                int rank_cap = 9;
                                                while (rank < rank_cap) {
                                                    spell.meta_Intense(true,true);
                                                    rank+=1;
                                                }
                                                spell.setPerfect(false);
                                                checkbox.setClickable(false);
                                                refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                            }})
                                        .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                spell.meta_Intense(true,all_free);
                                                refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                            }}).show();
                            }else {
                                spell.meta_Intense(true,all_free);
                                refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                            }
                        } else {
                            String descr="Augmentation d'intensité ne permet pas de dépasser le rang 9.";
                            Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
                            toast.show();
                        }

                    } else if (!all_free) {
                        new AlertDialog.Builder(mC)
                                .setTitle("Demande de confirmation")
                                .setMessage("Veux-tu utiliser Augmentation d'intensité une fois de plus ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        checkbox.setChecked(true);
                                    }})
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        while(!spell.getSave_val().equals(spell.getOri_Save_Val())){
                                            spell.meta_Intense(false,all_free);
                                        }
                                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                    }}).show();

                    } else {
                        spell.meta_Intense(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Intense_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),1));
        }

        //Extension de durée (+1)
        if ((settings.getBoolean("extension_dura",mC.getResources().getBoolean(R.bool.extension_dura_switch_def)))&&(!spell.getDuration(mC).equals("instant"))&&(!spell.getDuration(mC).equals("permanente"))&&(!spell.getName().equals("Arrêt du temps")))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Extension de durée (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect()&&!all_free)
                        {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Extension de durée ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend_dura(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend_dura(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Extend_dura(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }
                    } else {
                        spell.meta_Extend_dura(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Extend_dura_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),1));
        }


        //sort éloigné  +1
        String [] all_range_far_ok={"contact","courte","moyenne"};
        if ((settings.getBoolean("eloigne",mC.getResources().getBoolean(R.bool.eloigne_switch_def)))&&(Arrays.asList(all_range_far_ok).contains(spell.getRange()))) {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Sort éloigné (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        if(spell.isPerfect()&&!all_free)
                        {
                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort éloigné ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            while (!spell.getRange().equals("longue")) {
                                                spell.meta_Far(true,true);
                                            }
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Far(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Far(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }

                    } else if (!all_free) {
                        new AlertDialog.Builder(mC)
                                .setTitle("Demande de confirmation")
                                .setMessage("Veux-tu utiliser Sort éloigné une fois de plus ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        checkbox.setChecked(true);
                                    }})
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        while(!spell.getOri_Range().equals(spell.getRange())){
                                            spell.meta_Far(false,all_free);
                                        }
                                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                    }}).show();
                    } else {
                        spell.meta_Far(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Far_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),1));
        }


        //sort séléctif +1
        if ((settings.getBoolean("select",mC.getResources().getBoolean(R.bool.select_switch_def)))&&(spell.getN_dice()!=0))  {
            final CheckBox checkbox=new CheckBox(mC);
            checkbox.setText("Sort séléctif (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect()&&!all_free)
                        {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort séléctif ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Select_Spell(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Select_Spell(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }}).show();
                        }else {
                            spell.meta_Select_Spell(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }
                    } else {
                        spell.meta_Select_Spell(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image=new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Select_Spell_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),1));
        }


        //sort silencieux +1
        if ((settings.getBoolean("silence",mC.getResources().getBoolean(R.bool.silence_switch_def))) && spell.getCompo().contains("V")) {
            final CheckBox checkbox = new CheckBox(mC);
            checkbox.setText("Sort silencieux (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox = new int[]{Color.GRAY, Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked}, // unchecked
                            new int[]{android.R.attr.state_checked}  // checked
                    }, colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (spell.isPerfect()&&!all_free) {

                            new AlertDialog.Builder(mC)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort silencieux ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Silent(true,true);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }
                                    })
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Silent(true,all_free);
                                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                                        }
                                    }).show();
                        } else {
                            spell.meta_Silent(true,all_free);
                            refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                        }

                    } else {
                        spell.meta_Silent(false,all_free);
                        refreshAllTexts(Spell_Title,spell,spell_per_day,infos,mC);
                    }
                }
            });
            ImageButton image = new ImageButton(mC);
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Silent_descr(mC);
                }
            });
            pair_meta_rank.add(new Pair_Meta_Rank(new Meta_Check_Img(checkbox,image),1));
        }
    }

    public List<Pair_Meta_Rank> getAllMeta(){
        return pair_meta_rank;
    }

    public List<Pair_Meta_Rank> getMeta_Rank(int rank){
        List<Pair_Meta_Rank> selected_rank=new ArrayList<Pair_Meta_Rank>();
        for (int i=0;i<pair_meta_rank.size();i++){
            if(pair_meta_rank.get(i).getRank()<=rank){
                selected_rank.add(pair_meta_rank.get(i));
            }
        }
        return selected_rank;
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

    private void refreshAllTexts(final TextView Spell_Title, final Spell spell, final SpellPerDay spell_per_day,final TextView infos, final Context mC){
        refreshInfos(infos, spell,mC);
        refreshTitle( Spell_Title,spell, spell_per_day, mC);
    }

    private void refreshInfos(final TextView infos, final Spell spell, final Context mC) {
        String text="";
        Integer n_inf=0;
        if(!spell.getDmg_txt(mC).equals("")){
            text+="Dégats : "+spell.getDmg_txt(mC)+", ";
            n_inf+=1;
        }
        if(!spell.getDmg_type().equals("")){
            text+="Type : "+ spell.getDmg_type()+", ";
            n_inf+=1;
        }
        if(!spell.getRange_txt().equals("")){
            text+="Portée : "+spell.getRange_txt()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}
        if(!spell.getCompo().equals("")){
            text+="Compos : "+spell.getCompo()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}

        if(!spell.getCast_tim().equals("")){
            text+="Cast : "+ spell.getCast_tim()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}

        if(!spell.getDuration(mC).equals("")){
            text+="Durée : "+spell.getDuration(mC)+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}

        if(!spell.getRM().equals("")){
            text+="RM : "+spell.getRM()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";}

        String resistance;
        if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
            resistance = spell.getSave_type();

        } else {
            resistance = spell.getSave_type() + "(" + spell.getSave_val() + ")";
        }
        if(!resistance.equals("")){
            text+="Jet de sauv : "+ resistance+", ";
        }


        text = text.substring(0, text.length() - 2);
        if(spell.getDmg_txt(mC).equals("")){text+="\n";}
        infos.setText(text);
    }


    private void refreshTitle(final TextView Spell_Title, final Spell spell, final SpellPerDay spell_per_day, final Context mC) {
        String titre_texte=spell.getName()+" (rang : "+spell.getRank()+")";
        SpannableString titre=  new SpannableString(titre_texte);
        titre.setSpan(new RelativeSizeSpan(2f), 0,spell.getName().length(), 0); // set size1
        titre.setSpan(new ForegroundColorSpan(Color.BLACK), 0,spell.getName().length(), 0);// set color1

        if(spell_per_day.checkRank_available(spell.getRank(),mC)){
            titre.setSpan(new ForegroundColorSpan(Color.BLACK),spell.getName().length(),titre_texte.length(), 0);// set color2
        } else {
            titre.setSpan(new ForegroundColorSpan(mC.getColor(R.color.warning)),spell.getName().length(),titre_texte.length(), 0);// set color2
        }
        Spell_Title.setText(titre);
    }


}
