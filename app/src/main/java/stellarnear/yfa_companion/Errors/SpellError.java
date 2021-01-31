package stellarnear.yfa_companion.Errors;

public class SpellError extends Exception {
    public SpellError(String msg, Exception e) {
        super(msg,e);
    }
    public SpellError(String msg) {
        super(msg);
    }
}
