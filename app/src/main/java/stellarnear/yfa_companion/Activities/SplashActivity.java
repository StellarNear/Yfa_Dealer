package stellarnear.yfa_companion.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import stellarnear.yfa_companion.BuildConfig;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Tools;
import stellarnear.yfa_companion.VersionCheck.GetVersionData;
import stellarnear.yfa_companion.VersionCheck.PostConnectionVersion;
import stellarnear.yfa_companion.VersionCheck.VersionComparator;
import stellarnear.yfa_companion.VersionCheck.VersionData;


/**
 * Created by jchatron on 26/12/2017. updated with check download on 09/04/2020 and automatic download and install in 01/02/2021
 */

public class SplashActivity extends CustomActivity {
    private Tools tools = Tools.getTools();
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    @Override
    protected void doActivity() {
        new PostConnectionVersion(getApplicationContext()); //sending connexion data to apk versionning usage page

        // checking for rights
        int permission = ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    SplashActivity.this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else {
            checkUpdate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        checkUpdate();
    }

    private void checkUpdate() {
        //checking for internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean internetOk = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (internetOk && settings.getBoolean("switch_shadow_link", getApplicationContext().getResources().getBoolean(R.bool.switch_shadow_link_def))) {
            final GetVersionData getDataVersion;
            try {
                getDataVersion = new GetVersionData(SplashActivity.this);
                getDataVersion.setOnDataFailEventListener(new GetVersionData.OnDataFailEventListener() {
                    @Override
                    public void onEvent() {
                        startMainActivity();
                    }
                });

                getDataVersion.setOnDataRecievedEventListener(new GetVersionData.OnDataRecievedEventListener() {
                    @Override
                    public void onEvent() {
                        List<VersionData> listVersionData = getDataVersion.getVersionDataList();
                        if (listVersionData != null && listVersionData.size() > 0) {
                            VersionData newVersion = checkForNewVersion(listVersionData);
                            if (newVersion != null) {
                                askForUpgrade(newVersion);
                            } else {
                                startMainActivity();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                log.err("An error occured during version checking", e);
                startMainActivity();
            }
        } else {
            startMainActivity();
        }
    }

    @Override
    protected void onResumeActivity() {
        //nothing
    }

    @Override
    protected void onBackPressedActivity() {
        //nothing
    }

    @Override
    protected void onDestroyActivity() {
        //nothing
    }

    @Override
    protected boolean onOptionsItemSelectedActivity(MenuItem item) {
        return false;
    }

    @Override
    protected void onConfigurationChangedActivity() {
        //nothing
    }

    private VersionData checkForNewVersion(List<VersionData> listVersionData) {
        VersionData newestVerion = null;
        for (VersionData versionData : listVersionData) {
            if (VersionComparator.isNewer(versionData, newestVerion)) {
                newestVerion = versionData;
            }
        }
        return newestVerion;
    }

    private void askForUpgrade(final VersionData newVersion) {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.ask_for_upgrade, null);
        ((TextView) popupView.findViewById(R.id.ask_upgrade_old)).setText(BuildConfig.VERSION_NAME);
        ((TextView) popupView.findViewById(R.id.ask_upgrade_new)).setText(newVersion.getVersion_name());
        ((TextView) popupView.findViewById(R.id.ask_upgrade_new_sub)).setText("Faite le : " + newVersion.getRelease_date());
        popupView.findViewById(R.id.ask_upgrade_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("link_new_version", newVersion.getDl_link());
                clipboard.setPrimaryClip(clip);
                tools.customToast(getApplicationContext(), "Lien ajouté dans le presse papier", "center");
            }
        });

        popupView.findViewById(R.id.ask_upgrade_patch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.customToast(getApplicationContext(), newVersion.getPatch_note(), "center");
            }
        });

        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Update de l'application")
                .setView(popupView)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            startDownload(newVersion);
                        } catch (Exception e) {
                            log.err("An error occured while downloading version", e);
                            startMainActivity();
                        }
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startMainActivity();
                    }
                }).show();
    }

    private void startDownload(final VersionData newVersion) {
        new DownloadFileFromURL().execute(newVersion);
    }

    /**
     * Showing Dialog
     */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Téléchargement en cours... Patientez...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<VersionData, String, String> {

        private String savedPathApk;

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Check if we have write permission
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(VersionData... versionData) { //arg0 is the link url and arg1 is the name of the version
            int count;
            try {
                URL url = new URL(versionData[0].getDl_link());
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                savedPathApk = Environment.getExternalStorageDirectory().toString()
                        + "/" + BuildConfig.APPLICATION_ID + "_" + versionData[0].getVersion_name() + ".apk";
                OutputStream output = new FileOutputStream(savedPathApk);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                log.fatal(SplashActivity.this, "Error during the download of the file", e);
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            File apkFile = new File(savedPathApk);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", apkFile);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            finish();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}