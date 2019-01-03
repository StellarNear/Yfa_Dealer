package stellarnear.yfa_dealer;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import stellarnear.yfa_dealer.Spells.Spell;

public class MyDragAndDrop {
    private Context mC;
    public MyDragAndDrop(Context mC){
        this.mC=mC;
    }

    public void setTouchListner(View v,Spell spell){
        v.setOnTouchListener(new MyTouchListner(spell));
    }

    public void setDragListner(View v,){  //faire un truc ennemi
        v.setOnDragListener(new MyDragListener());
    }


    class MyTouchListner implements View.OnTouchListener {

        private MyTouchListner(Spell spell){

        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable enterShape = mC.getResources().getDrawable(R.drawable.target_select_gradient);
        Drawable normalShape = mC.getResources().getDrawable(R.drawable.target_basic_gradient);

        private MyDragListener( ){

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
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);
                default:
                    break;
            }
            return true;
        }
    }
}
