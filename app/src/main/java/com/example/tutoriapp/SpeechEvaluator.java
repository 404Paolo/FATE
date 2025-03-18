package com.example.tutoriapp;

import android.content.Context;
import android.util.Log;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import java.util.concurrent.Executors;

public class SpeechEvaluator {
    private static final String TAG = "SpeechToText";
    private static final String BASE_URL = "https://speech.googleapis.com/v1/speech:recognize";

    private Context context;

    public interface OnTranscriptionCompleteListener {
        void onTranscriptionComplete(String pinyinText, float confidence);
        void onError(String error);
    }

    public SpeechEvaluator(Context context) {
        this.context = context;
    }

    public void transcribeAudio(String base64Audio, OnTranscriptionCompleteListener listener) {
        Log.d(TAG, "Base64 Audio (first 50 chars): " + base64Audio.substring(0, 50));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                GoogleAuthHelper authHelper = new GoogleAuthHelper(context);
                String accessToken = authHelper.getAccessToken();
                if (accessToken == null) {
                    listener.onError("Failed to get access token");
                    return;
                }

                JSONObject config = new JSONObject();
                config.put("encoding", "LINEAR16");
                config.put("sampleRateHertz", 16000);
                config.put("languageCode", "zh-CN");
                config.put("enableWordConfidence", true);

                JSONObject audio = new JSONObject();
                audio.put("content", base64Audio);

                JSONObject requestBody = new JSONObject();
                requestBody.put("config", config);
                requestBody.put("audio", audio);

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(requestBody.toString(), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Speech Response: " + responseData);

                    JSONObject result = extractTranscription(responseData);
                    String chineseText = result.getString("text");
                    float confidence = (float) result.getDouble("confidence");

                    String pinyinText = convertToPinyin(chineseText);

                    listener.onTranscriptionComplete(pinyinText, confidence);
                } else {
                    listener.onError("Response Failed: " + response.body());
                }

            } catch (Exception e) {
                listener.onError("Speech Request Failed: " + e.getMessage());
                Log.e(TAG, "Speech Request Failed", e);
            }
        });
    }

    private JSONObject extractTranscription(String responseData) throws JSONException {
        JSONObject resultData = new JSONObject();
        try {
            JSONObject jsonResponse = new JSONObject(responseData);
            JSONArray results = jsonResponse.optJSONArray("results");

            if (results != null && results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                JSONArray alternatives = firstResult.optJSONArray("alternatives");

                if (alternatives != null && alternatives.length() > 0) {
                    String transcript = alternatives.getJSONObject(0).getString("transcript");

                    // Extract the first word and confidence
                    JSONArray words = alternatives.getJSONObject(0).optJSONArray("words");
                    if (words != null && words.length() > 0) {
                        JSONObject firstWordObj = words.getJSONObject(0);
                        String firstWord = firstWordObj.getString("word");
                        float confidence = (float) firstWordObj.optDouble("confidence", 0.0);

                        resultData.put("text", transcript);
                        resultData.put("confidence", confidence);
                        return resultData;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error ");
        }

        // Default values if extraction fails
        resultData.put("text", "No transcription found");
        resultData.put("firstWord", "");
        resultData.put("confidence", 0.0);
        return resultData;
    }

    private String convertToPinyin(String chineseText) {
        StringBuilder pinyinBuilder = new StringBuilder();
        for (char c : chineseText.toCharArray()) {
            if (Character.isWhitespace(c)) {
                pinyinBuilder.append(" ");
            } else {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    pinyinBuilder.append(pinyinArray[0]).append(" ");
                } else {
                    pinyinBuilder.append(c);
                }
            }
        }
        return pinyinBuilder.toString().trim();
    }
}
