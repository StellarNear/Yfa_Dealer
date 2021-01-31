package stellarnear.yfa_companion.Perso;

import stellarnear.yfa_companion.Log.CustomLog;

public abstract class SelfCustomLog {
    public transient CustomLog log = new CustomLog(this.getClass());
}
