package ma10.megusurin.lib.web;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarInfoGetter {

    private static final String TAG = "TOYOTA API Request";

    private static final String API = "https://api-jp-t-itc.com/GetVehicleInfo?developerkey=%1$s&responseformat=json&vid=%2$s&infoids=[Posn,VehBehvr,VehCdn]";

    private static final String API_FOR_TIME = "https://api-jp-t-itc.com/GetVehicleInfo?developerkey=%1$s&responseformat=json&vid=%2$s&searchstart=%3$s&searchend=%4$s&infoids=[Posn,VehBehvr,VehCdn]";

    // replace to YOUR Developer KEY
    private static final String DEV_KEY = "XXX";

    private static final String CAR_ID_SAMPLE = "ITCJP_VID_001";

    private static final String CAR_ID = "ITCJP_VID_042";

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String mRequestURL;

    private Date mStartTime;

    private int mCount;

    private SimpleDateFormat mSdf;

    private long mStart;

    public CarInfoGetter() {

        mSdf = new SimpleDateFormat(TIME_FORMAT);

        try {
            mStartTime = mSdf.parse("2014-09-23 14:50:00");
            mStart = System.currentTimeMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public CarInfo getCarInfo() {
        CarInfo info = null;

        try {
            long diff = System.currentTimeMillis() - mStart;

            long time = mStartTime.getTime() + diff;
            mCount++;

            Date start = new Date(time);
            Date end = new Date(time + 1000);

            String startText = mSdf.format(start);
            startText = startText.replaceAll(" ", "%20");

            String endText = mSdf.format(end);
            endText = endText.replaceAll(" ", "%20");
            mRequestURL = String.format(API_FOR_TIME, DEV_KEY, CAR_ID, startText, endText);
//            mRequestURL = String.format(API, DEV_KEY, CAR_ID_SAMPLE);

            URL url = new URL(mRequestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            int httpResult = connection.getResponseCode();
            Log.d(TAG, "HttpResult : " + httpResult + " " + connection.getResponseMessage());
            if (httpResult == 200) {

                InputStream is = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                String data = sb.toString();
                is.close();

                info = CarInfoParser.parse(data);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }
}
