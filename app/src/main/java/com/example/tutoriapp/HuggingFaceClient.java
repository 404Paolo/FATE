package com.example.tutoriapp;

import android.util.Log;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class HuggingFaceClient {
    private static final String BASE_URL = "https://404paolo-tutoriowl.hf.space/gradio_api/call/predict";
    private final OkHttpClient client;

    public HuggingFaceClient() {
        this.client = new OkHttpClient();
    }

    public interface PredictionCallback {
        void onSuccess(String prediction);
        void onFailure(String error);
    }

    public void predict(String fileUrl, PredictionCallback callback) {
        try {
            JSONObject meta = new JSONObject();
            meta.put("_type", "gradio.FileData");

            JSONObject fileObject = new JSONObject();
            fileObject.put("path", fileUrl);
            fileObject.put("meta", meta);

            JSONArray dataArray = new JSONArray();
            dataArray.put(fileObject);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("data", dataArray);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure("Unexpected code " + response);
                        return;
                    }
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String eventId = jsonResponse.optString("event_id");
                        if (!eventId.isEmpty()) {
                            fetchPrediction(eventId, callback);
                        } else {
                            callback.onFailure("Event ID not found");
                        }
                    } catch (Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private void fetchPrediction(String eventId, PredictionCallback callback) {
        String url = BASE_URL + "/" + eventId;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Unexpected code " + response);
                    return;
                }
                try {
                    String responseBody = response.body().string();
                    String[] lines = responseBody.split("\\n");
                    for (String line : lines) {
                        if (line.startsWith("data: ")) {
                            String jsonString = line.substring(6).trim();
                            callback.onSuccess(jsonString);
                            return;
                        }
                    }
                    callback.onFailure("No prediction data found");
                } catch (Exception e) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }
}