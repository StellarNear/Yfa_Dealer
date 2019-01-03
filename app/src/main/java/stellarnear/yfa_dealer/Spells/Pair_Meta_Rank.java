package stellarnear.yfa_dealer.Spells;

/**
 * Created by jchatron on 29/11/2017.
 */

public class Pair_Meta_Rank {
    int rank;
    Meta_Check_Img meta;

    public Pair_Meta_Rank( Meta_Check_Img meta,int rank)
        {
            this.meta = meta;
            this.rank = rank;
        }


       public Meta_Check_Img getMeta(){
            return this.meta;
        }

        public int getRank(){
            return this.rank;
        }
    }

