package bias.zochiwon_suhodae.homemade_guardian_beta.Main.common;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static void sendNotification(final String regToken, final String title, final String messsage){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... parms) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", messsage);
                    dataJson.put("title", title);
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + "AAAAwOrSSfQ:APA91bG5atF_mkGFr_DdlIOTvA4BjXvZJ4cyuQOlNK_AOQNzaJyfBDeTYkhP6pVKXYrHsllc2QZNJKfx5pq46I290MMQd6wHzx5pVWJzfKFuv2FYia4sW_BQicqqDBRzQ7QcpjEyK9qT")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    Log.d("error", e+"");
                }
                return  null;
            }
        }.execute();
    }
    public static void sendCommentNotification(final String regToken, final String title, final String messsage, final String Marketmodel_Uid){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... parms) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", messsage);
                    dataJson.put("title", title);
                    dataJson.put("tag", Marketmodel_Uid);
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    json.put("tag", Marketmodel_Uid);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + "AAAAwOrSSfQ:APA91bG5atF_mkGFr_DdlIOTvA4BjXvZJ4cyuQOlNK_AOQNzaJyfBDeTYkhP6pVKXYrHsllc2QZNJKfx5pq46I290MMQd6wHzx5pVWJzfKFuv2FYia4sW_BQicqqDBRzQ7QcpjEyK9qT")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    Log.d("error", e+"");
                }
                return  null;
            }
        }.execute();
    }
}