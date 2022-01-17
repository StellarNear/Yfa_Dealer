package stellarnear.yfa_companion.Log;

import stellarnear.yfa_companion.Log.CustomLog;

public abstract class SelfCustomLog {
    public transient CustomLog log = new CustomLog(this.getClass());
}
