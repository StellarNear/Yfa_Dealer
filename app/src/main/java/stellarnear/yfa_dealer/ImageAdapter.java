package stellarnear.yfa_dealer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    Integer  size_height;

    // Keep all Images in array
    public List<Integer> mThumbIds;

    public static final Map<String,Integer> dice_map= new HashMap<>();
    static {
        dice_map.put("feu_d6_1",R.mipmap.feu_d6_1);
        dice_map.put("feu_d6_2",R.mipmap.feu_d6_2);
        dice_map.put("feu_d6_3",R.mipmap.feu_d6_3);
        dice_map.put("feu_d6_4",R.mipmap.feu_d6_4);
        dice_map.put("feu_d6_5",R.mipmap.feu_d6_5);
        dice_map.put("feu_d6_6",R.mipmap.feu_d6_6);
        dice_map.put("foudre_d6_1",R.mipmap.foudre_d6_1);
        dice_map.put("foudre_d6_2",R.mipmap.foudre_d6_2);
        dice_map.put("foudre_d6_3",R.mipmap.foudre_d6_3);
        dice_map.put("foudre_d6_4",R.mipmap.foudre_d6_4);
        dice_map.put("foudre_d6_5",R.mipmap.foudre_d6_5);
        dice_map.put("foudre_d6_6",R.mipmap.foudre_d6_6);
        dice_map.put("froid_d6_1",R.mipmap.froid_d6_1);
        dice_map.put("froid_d6_2",R.mipmap.froid_d6_2);
        dice_map.put("froid_d6_3",R.mipmap.froid_d6_3);
        dice_map.put("froid_d6_4",R.mipmap.froid_d6_4);
        dice_map.put("froid_d6_5",R.mipmap.froid_d6_5);
        dice_map.put("froid_d6_6",R.mipmap.froid_d6_6);
        dice_map.put("acide_d6_1",R.mipmap.acide_d6_1);
        dice_map.put("acide_d6_2",R.mipmap.acide_d6_2);
        dice_map.put("acide_d6_3",R.mipmap.acide_d6_3);
        dice_map.put("acide_d6_4",R.mipmap.acide_d6_4);
        dice_map.put("acide_d6_5",R.mipmap.acide_d6_5);
        dice_map.put("acide_d6_6",R.mipmap.acide_d6_6);
        dice_map.put("acide_d8_1",R.mipmap.acide_d8_1);
        dice_map.put("acide_d8_2",R.mipmap.acide_d8_2);
        dice_map.put("acide_d8_3",R.mipmap.acide_d8_3);
        dice_map.put("acide_d8_4",R.mipmap.acide_d8_4);
        dice_map.put("acide_d8_5",R.mipmap.acide_d8_5);
        dice_map.put("acide_d8_6",R.mipmap.acide_d8_6);
        dice_map.put("acide_d8_7",R.mipmap.acide_d8_7);
        dice_map.put("acide_d8_8",R.mipmap.acide_d8_8);
        dice_map.put("aucun_d4_1",R.mipmap.aucun_d4_1);
        dice_map.put("aucun_d4_2",R.mipmap.aucun_d4_2);
        dice_map.put("aucun_d4_3",R.mipmap.aucun_d4_3);
        dice_map.put("aucun_d4_4",R.mipmap.aucun_d4_4);
        dice_map.put("aucun_d6_1",R.mipmap.aucun_d6_1);
        dice_map.put("aucun_d6_2",R.mipmap.aucun_d6_2);
        dice_map.put("aucun_d6_3",R.mipmap.aucun_d6_3);
        dice_map.put("aucun_d6_4",R.mipmap.aucun_d6_4);
        dice_map.put("aucun_d6_5",R.mipmap.aucun_d6_5);
        dice_map.put("aucun_d6_6",R.mipmap.aucun_d6_6);
        dice_map.put("aucun_d8_1",R.mipmap.aucun_d8_1);
        dice_map.put("aucun_d8_2",R.mipmap.aucun_d8_2);
        dice_map.put("aucun_d8_3",R.mipmap.aucun_d8_3);
        dice_map.put("aucun_d8_4",R.mipmap.aucun_d8_4);
        dice_map.put("aucun_d8_5",R.mipmap.aucun_d8_5);
        dice_map.put("aucun_d8_6",R.mipmap.aucun_d8_6);
        dice_map.put("aucun_d8_7",R.mipmap.aucun_d8_7);
        dice_map.put("aucun_d8_8",R.mipmap.aucun_d8_8);
        dice_map.put("feu_d8_1",R.mipmap.feu_d8_1);
        dice_map.put("feu_d8_2",R.mipmap.feu_d8_2);
        dice_map.put("feu_d8_3",R.mipmap.feu_d8_3);
        dice_map.put("feu_d8_4",R.mipmap.feu_d8_4);
        dice_map.put("feu_d8_5",R.mipmap.feu_d8_5);
        dice_map.put("feu_d8_6",R.mipmap.feu_d8_6);
        dice_map.put("feu_d8_7",R.mipmap.feu_d8_7);
        dice_map.put("feu_d8_8",R.mipmap.feu_d8_8);
        dice_map.put("foudre_d8_1",R.mipmap.foudre_d8_1);
        dice_map.put("foudre_d8_2",R.mipmap.foudre_d8_2);
        dice_map.put("foudre_d8_3",R.mipmap.foudre_d8_3);
        dice_map.put("foudre_d8_4",R.mipmap.foudre_d8_4);
        dice_map.put("foudre_d8_5",R.mipmap.foudre_d8_5);
        dice_map.put("foudre_d8_6",R.mipmap.foudre_d8_6);
        dice_map.put("foudre_d8_7",R.mipmap.foudre_d8_7);
        dice_map.put("foudre_d8_8",R.mipmap.foudre_d8_8);
        dice_map.put("froid_d8_1",R.mipmap.froid_d8_1);
        dice_map.put("froid_d8_2",R.mipmap.froid_d8_2);
        dice_map.put("froid_d8_3",R.mipmap.froid_d8_3);
        dice_map.put("froid_d8_4",R.mipmap.froid_d8_4);
        dice_map.put("froid_d8_5",R.mipmap.froid_d8_5);
        dice_map.put("froid_d8_6",R.mipmap.froid_d8_6);
        dice_map.put("froid_d8_7",R.mipmap.froid_d8_7);
        dice_map.put("froid_d8_8",R.mipmap.froid_d8_8);
    }


    // Constructor
    public ImageAdapter(Context c, List<String> dice_list, Integer size_height_grid){
        mThumbIds = new ArrayList<>();


        for (String each : dice_list)   {
            mThumbIds.add(dice_map.get(each));
        }

        mContext = c;
        size_height=size_height_grid;
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, size_height));


        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds.get(position));
        return imageView;
    }

}