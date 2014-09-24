package ma10.megusurin.lib.web;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class YodaAPIAccesser {

    private static final String TAG = YodaAPIAccesser.class.getSimpleName();

    private static final String API_MAGIC_URL = "http://megusurin.herokuapp.com/megusurin/api/magic";

    private static final String API_ENCOUNT_URL = "http://megusurin.herokuapp.com/megusurin/api/encount";

    private static final String KEY_MAGIC = "magic";

    private static final String KEY_ENCOUNT = "encount";

    private String mRequestURL;

    public YodaAPIAccesser(final boolean isEncount) {
        if (isEncount) {
            mRequestURL = new String(API_ENCOUNT_URL);
        } else {
            mRequestURL = new String(API_MAGIC_URL);
        }
    }

    public void postMagicType(final int magicType) {
        try {
            URL url = new URL(mRequestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put(KEY_MAGIC, String.valueOf(magicType));

            OutputStream out = new DataOutputStream(connection.getOutputStream());
            OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
            outWriter.write(jsonParam.toString());
            outWriter.flush();
            outWriter.close();

            int HttpResult = connection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(),"UTF-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());

            }else{
                System.out.println(connection.getResponseMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getMagicType() {

        int magicType = -1;

        try {
            URL url = new URL(mRequestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            InputStream is = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
            String data = sb.toString();
            is.close();
            Log.d(TAG, data);

            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has(KEY_MAGIC)) {
                String value = jsonObject.getString(KEY_MAGIC);
                if (!value.isEmpty()) {
                    magicType = Integer.valueOf(value);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return magicType;
    }

    public void postOccurEncount(final int enemyType) {
        try {
            URL url = new URL(mRequestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put(KEY_ENCOUNT, true);

            OutputStream out = new DataOutputStream(connection.getOutputStream());
            OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
            outWriter.write(jsonParam.toString());
            outWriter.flush();
            outWriter.close();

            int HttpResult = connection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(),"UTF-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());

            }else{
                System.out.println(connection.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isOccurEncount() {

        boolean occureEncount = false;

        try {
            URL url = new URL(mRequestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            InputStream is = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
            String data = sb.toString();
            is.close();
            Log.d(TAG, data);

            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has(KEY_ENCOUNT)) {
                occureEncount = jsonObject.getBoolean(KEY_ENCOUNT);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return occureEncount;
    }
}
