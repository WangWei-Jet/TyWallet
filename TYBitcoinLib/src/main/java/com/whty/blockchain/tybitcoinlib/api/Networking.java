package com.whty.blockchain.tybitcoinlib.api;

import android.util.Log;

import com.whty.blockchain.tybitcoinlib.entity.HttpResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import info.guardianproject.netcipher.NetCipher;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Networking {
    private static final String TAG = Networking.class.getName();

    public static Integer HTTP_LOCAL_ERROR_CODE = 999;
    public static String HTTP_LOCAL_ERROR_MSG = "HTTPLocalErrorMsg";
    public static String HTTP_ERROR_CODE = "HTTPErrorCode";
    public static String HTTP_ERROR_MSG = "HTTPErrorMsg";
    public static String HTTP_MSG = "Msg";

    public Networking() {
    }

    public HttpURLConnection getHttpURLConnection(String URL) throws Exception {
        return NetCipher.getHttpsURLConnection(URL);
        //return (HttpURLConnection)new URL(URL).openConnection(); // can use this if we stop
        // supporting  API <= 19
    }

    public JSONObject postURL(String request, String urlParameters, int requestRetry) throws
            Exception {
        return this.postURLCall(request, urlParameters, requestRetry,
                "application/x-www-form-urlencoded");
    }

    public JSONObject postURL(String request, String urlParameters) throws Exception {
        return this.postURLCall(request, urlParameters, 2, "application/x-www-form-urlencoded");
    }

    public JSONObject postURLNotJSON(String request, String urlParameters) throws Exception {
        return this.postURLCallNotJSON(request, urlParameters, 2,
                "application/x-www-form-urlencoded");
    }

    public JSONObject postURLJson(String request, String urlParameters) throws Exception {
        return this.postURLCall(request, urlParameters, 2, "application/json");
    }

    private JSONObject postURLCallNotJSON(String urlString, String urlParameters, int
            requestRetry, String contentType) throws Exception {
        Log.d(TAG, "Networking postURLCall " + urlString);
        String error = null;
        int errorCode = 0;

        for (int ii = 0; ii < requestRetry; ++ii) {

            try {
                HttpResponse httpResponse = okhttpPostURLCall(urlString, urlParameters);
                if (httpResponse.getResponseCode() == 200) {
                    //请求成功
                    JSONObject obj = new JSONObject();
                    obj.put(HTTP_MSG, httpResponse.getResponseMsg());
                    return obj;
                }
                error = httpResponse.getResponseMsg();
                errorCode = httpResponse.getResponseCode();
                if(ii < requestRetry-1){
                    Thread.sleep(3000L);
                }
            } catch (Exception var14) {
                Log.e(TAG,"异常",var14);
                throw new Exception("Network error" + var14.getMessage());
            } finally {
//                connection.disconnect();
            }
        }

        JSONObject obj = new JSONObject();
        obj.put(HTTP_ERROR_CODE, errorCode);
        obj.put(HTTP_ERROR_MSG, error);
        return obj;
    }

    private JSONObject postURLCall(String urlString, String urlParameters, int requestRetry,
                                   String contentType) throws Exception {
        String error = null;
        int errorCode = 0;

        for (int ii = 0; ii < requestRetry; ++ii) {
            HttpURLConnection connection = getHttpURLConnection(urlString);

            try {
                Log.d(TAG, "url parameters:" + urlParameters);
//                connection.setDoOutput(true);
//                connection.setDoInput(true);
//                connection.setInstanceFollowRedirects(false);
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Content-Type", contentType);
//                connection.setRequestProperty("charset", "utf-8");
//                connection.setRequestProperty("Accept", "application/json");
//                connection.setRequestProperty("Content-Length", "" + Integer.toString
//                        (urlParameters.getBytes().length));
//                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac
// OS" +
//                        " X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 " +
//                        "Safari/537.36");
//                connection.setUseCaches(false);
//                connection.setConnectTimeout('\uea60');
//                connection.setReadTimeout('\uea60');
//                connection.connect();
//                DataOutputStream e = new DataOutputStream(connection.getOutputStream());
//                e.writeBytes(urlParameters);
//                e.flush();
//                e.close();
//                connection.setInstanceFollowRedirects(false);
//                if (connection.getResponseCode() == 200) {
//                    String var10 = IOUtils.toString(connection.getInputStream(), "UTF-8");
//                    return new JSONObject(var10);
//                }
//
//                error = IOUtils.toString(connection.getErrorStream(), "UTF-8");
//                errorCode = connection.getResponseCode();
//                Thread.sleep(5000L);

                HttpResponse httpResponse = okhttpPostURLCall(urlString, urlParameters);
                if (httpResponse.getResponseCode() == 200) {
                    //请求成功
                    JSONObject obj = new JSONObject();
                    obj.put(HTTP_MSG, httpResponse.getResponseMsg());
                    return obj;
                }
                error = httpResponse.getResponseMsg();
                errorCode = httpResponse.getResponseCode();
                if(ii < requestRetry-1){
                    Thread.sleep(3000L);
                }

            } catch (Exception var14) {
                var14.printStackTrace();
                throw new Exception("Network error" + var14.getMessage());
            } finally {
                connection.disconnect();
            }
        }

        JSONObject obj = new JSONObject();
        obj.put(HTTP_ERROR_CODE, errorCode);
        obj.put(HTTP_ERROR_MSG, error);
        return obj;
    }

    public String getURL(String URL) throws Exception {
        return this.okhttpGetURLCall(URL);
    }

    public Object getURLNotJSON(String URL) throws Exception {
        return this.okhttpGetURLCall(URL);
    }

    private String okhttpGetURLCall(String urlString) {
        Log.d(TAG, "okhttpGetURLCall url: " + urlString);

        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder().url(new URL(urlString)).build();

            Response response = okHttpClient.newCall(request).execute();

            int responseCode = response.code();

            Log.d(TAG, "response code:" + responseCode);

            if (responseCode != 200) {
                return null;
            }
            String responseBody = response.body().string();
            Log.d(TAG, "response body:" + responseBody);

            return responseBody;
        } catch (IOException e) {
            Log.e(TAG, "异常", e);
            return null;
        }
    }

    private HttpResponse okhttpPostURLCall(String urlString, String urlParameters) {
        Log.d(TAG, "okhttpPostURLCall url: " + urlString);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30l, TimeUnit.SECONDS)
                .readTimeout(30l, TimeUnit.SECONDS)
                .writeTimeout(30l, TimeUnit.SECONDS)
                .build();

        HttpResponse httpResponse;
        try {
            httpResponse = new HttpResponse();
            String[] values = urlParameters.split("=");
            String key = values[0];
            String value = values[1];
            Log.d(TAG, "key:" + key + "\nvalue:" + value);

            FormBody formBody = new FormBody.Builder()
                    .add(key, value)
                    .build();

            Request request = new Request.Builder().url(new URL(urlString)).post(formBody).build();

            Response response = okHttpClient.newCall(request).execute();

            int responseCode = response.code();

            httpResponse.setResponseCode(responseCode);

            Log.d(TAG, "response code:" + responseCode);

            String responseMsg = response.message();
            String responseBody = response.body().string();
            httpResponse.setResponseMsg(responseBody);
            Log.d(TAG, "responseMsg:" + responseMsg + "\nresponse body:" + responseBody);

            return httpResponse;
        } catch (IOException e) {
            Log.e(TAG, "异常", e);
            httpResponse = new HttpResponse();
            httpResponse.setResponseCode(99);
            httpResponse.setResponseMsg("网络请求异常:"+e.getMessage());
        }
        return httpResponse;
    }
}
