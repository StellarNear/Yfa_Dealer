package stellarnear.yfa_dealer.Spells;

/**
 * Created by jchatron on 29/11/2017.
 */

public class _oldPair_Meta_Rank {
    int rank;
    _oldMeta_Check_Img meta;

    public _oldPair_Meta_Rank(_oldMeta_Check_Img meta, int rank)
        {
            this.meta = meta;
            this.rank = rank;
        }


       public _oldMeta_Check_Img getMeta(){
            return this.meta;
        }

        public int getRank(){
            return this.rank;
        }
    }

