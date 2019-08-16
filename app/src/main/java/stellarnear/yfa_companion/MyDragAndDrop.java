package stellarnear.yfa_companion;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import stellarnear.yfa_companion.Spells.Spell;

public class MyDragAndDrop {
    private Context mC;
    static private Spell currentSpell;
    private Targets targets;

    public MyDragAndDrop(Context mC){
        this.mC=mC;
        this.targets=Targets.getInstance();
    }

    public void setTouchListner(View v,Spell spell){
        v.setOnTouchListener(new MyTouchListner(spell));
    }

    public void setDragListner(View v,String target){  //faire un truc ennemi
        v.setOnDragListener(new MyDragListener(target));
    }


    class MyTouchListner implements View.OnTouchListener {
        private Spell spell;
        private MyTouchListner(Spell spell){
            this.spell=spell;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                currentSpell=spell;
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable enterShape = mC.getResources().getDrawable(R.drawable.target_select_gradient);
        Drawable normalShape = mC.getResources().getDrawable(R.drawable.target_basic_gradient);
        private String target;
        private MyDragListener(String target){
            this.target=target;
            targets.addTarget(target);
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);

                    targets.addSpellToTarget(target,currentSpell);

                    toastAll();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

    private void toastAll() {
        String txt="Liste actuelle:\n";

        for (String tar : targets.getTargetList()){
            txt+=tar+":\n";
            int i=1;
            for (Spell spell : targets.getSpellListForTarget(tar).asList()){
                txt+="Spell "+i+" : "+spell.getName()+"\n";
                i++;
            }
        }
        Log.d("LIST",txt);
    }
}
