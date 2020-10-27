package stellarnear.yfa_companion.Log;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import stellarnear.yfa_companion.BuildConfig;
import stellarnear.yfa_companion.R;

public class CustomLog {
    private static Set<LogMsg> allLogs=new LinkedHashSet<>();
    private static SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);

    private Class currentLoggedClass;

    public CustomLog(Class<?> clazz) {
        this.currentLoggedClass=clazz;
    }

    public void info(String msg){
        allLogs.add(new LogMsg(Level.INFO,msg));
    }

    public void warn(String msg){
        allLogs.add(new LogMsg(Level.WARN,msg));
    }

    public void warn(String msg,Exception e){
        allLogs.add(new LogMsg(Level.WARN,msg,e));
        Log.w("WARN",msg,e);
    }

    public void err(String msg,Exception e){
        allLogs.add(new LogMsg(Level.ERROR,msg,e));
        Log.e("ERROR",msg,e);
    }

    public void fatal(final Activity mA,String msg,Exception e){
        allLogs.add(new LogMsg(Level.FATAL_ERROR,msg,e));
        new AlertDialog.Builder(mA)
                .setTitle("Erreur fatale détectée")
                .setMessage("Veux tu envoyer un rapport d'erreur ?")
                .setIcon(R.drawable.ic_baseline_error_24)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sendEmail(mA);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void sendEmail(Activity mA){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"jeremie.chatron@free.fr"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Report Crash " + BuildConfig.APPLICATION_ID.replace("stellarnear.","") +" " +formater.format(new Date()));

        String bodyHtml="";
        for(LogMsg msg : allLogs){
            bodyHtml+=msg.getHtmlString()+"<br>";
        }
        i.putExtra(Intent.EXTRA_TEXT   , Html.fromHtml(bodyHtml));
        i.putExtra(Intent.EXTRA_HTML_TEXT   , bodyHtml);
        try {
            mA.startActivity(Intent.createChooser(i, "Envoi d'mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mA, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private class LogMsg{
        private String prefix = currentLoggedClass.getName();
        private Level level;
        private String timeStamp;
        private String msg;
        private Exception exception;
        private LogMsg(Level level,String msg){
            this.level=level;
            this.timeStamp= formater.format(new Date());
            this.msg=msg;
        }
        private LogMsg(Level level,String msg,Exception e){
            this.level=level;
            this.timeStamp= formater.format(new Date());
            this.msg=msg;
            this.exception=e;
        }

        public String getHtmlString() {
            String lineHtml = "";
            lineHtml+="<font color=#808080>" + prefix + "</font>";
            lineHtml+="<font color=#00b359> (" + timeStamp + ")</font>";

            switch (level){
                case INFO:
                lineHtml+="<font color=#4db8ff> [" + level + "]</font>";
                    break;
                case WARN:
                    lineHtml+="<font color=#ffdd99> [" + level + "]</font>";
                    break;
                case ERROR:
                    lineHtml+="<font color=#ff4d4d> [" + level + "]</font>";
                    break;
                case FATAL_ERROR:
                    lineHtml+="<font color=#e60000> <b>[" + level + "]</b></font>";
                    break;
            }
            lineHtml+="<font color=#262626> : " + msg + "</font>";

            if(exception!=null){
                lineHtml+="<br><font color=#ff4d4d>Error stacktrace : "+exception.getMessage()+"</font>";
                for(StackTraceElement elem: exception.getStackTrace()){
                    lineHtml+="<br><tab>-"+elem.toString();
                }
                lineHtml+="<br>";
            }

            return lineHtml;
        }
    }

    private enum Level{
        INFO,
        WARN,
        ERROR,
        FATAL_ERROR
    }
}
