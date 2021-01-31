package stellarnear.yfa_companion.Errors;

public class AllAttributeError extends Exception {
    public AllAttributeError(String msg, Exception e) {
        super(msg,e);
    }
    public AllAttributeError(String msg) {
        super(msg);
    }
}
