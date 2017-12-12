package stellarnear.yfa_dealer;

import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.ImageButton;

import java.util.Map;

/**
 * Created by jchatron on 29/11/2017.
 */

public class Meta_Check_Img extends AppCompatActivity {
    CheckBox checkbox;
    ImageButton ImgBu;

    public Meta_Check_Img( CheckBox checkbox,ImageButton ImgBu)
    {
        this.checkbox = checkbox;
        this.ImgBu = ImgBu;
    }


    public CheckBox getCheckbox(){
        return this.checkbox;
    }

    public ImageButton getImgageButton(){
        return this.ImgBu;
    }
}