package stellarnear.yfa_companion.VersionCheck;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import stellarnear.yfa_companion.BuildConfig;


public class GetVersionData {
    private ProgressDialog dialog;
    private Activity mA;
    private List<VersionData> versionDataList;
    private OnDataRecievedEventListener mListener;
    private OnDataFailEventListener mListenerFail;

    public GetVersionData(Activity mA) {
        this.mA = mA;
        String googleSheetTargetId = "18yDl6Fd72H2lJbdw9Ivgqdn83LDZdeQtFba6B0hk0Ps";
        String sheetName = BuildConfig.APPLICATION_ID.replace("stellarnear.","");

        new JsonTask().execute("https://script.google.com/macros/s/AKfycbxOLElujQcy1-ZUer1KgEvK16gkTLUqYftApjNCM_IRTL3HSuDk/exec?id="+googleSheetTargetId+"&sheet="+sheetName);
    }

    public List<VersionData> getVersionDataList() {
        return versionDataList;
    }

    public void setOnDataRecievedEventListener(OnDataRecievedEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnDataRecievedEventListener {
        void onEvent();
    }

    public void setOnDataFailEventListener(OnDataFailEventListener eventListener) {
        mListenerFail = eventListener;
    }

    public interface OnDataFailEventListener {
        void onEvent();
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mA);
            dialog.setMessage("Verification de MAJ disponible...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            try {
                JSONObject allObjs = new JSONObject(result);
                JSONArray listVersionData = (JSONArray) allObjs.get(BuildConfig.APPLICATION_ID.replace("stellarnear.",""));


                Gson gson = new Gson();
                versionDataList = new ArrayList<>();
                for (int i = 0; i < listVersionData.length(); i++) {
                    versionDataList.add(gson.fromJson(listVersionData.get(i).toString(), VersionData.class));
                }
                if (mListener != null) {
                    mListener.onEvent();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (mListenerFail != null) {
                    mListenerFail.onEvent();
                }
            }
        }
    }
}