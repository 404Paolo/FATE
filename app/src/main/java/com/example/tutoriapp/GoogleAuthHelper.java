package com.example.tutoriapp;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

public class GoogleAuthHelper {
    private static final String TAG = "GoogleAuthHelper";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    private Context context;

    public GoogleAuthHelper(Context context) {
        this.context = context;
    }

    public String getAccessToken() {
        try {
            InputStream inputStream = context.getAssets().open("service-account.json");
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String jsonContent = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            JSONObject jsonObject = new JSONObject(jsonContent);
            String privateKey = jsonObject.getString("private_key");
            String clientEmail = jsonObject.getString("client_email");

            String header = Base64.encodeToString("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);

            long now = System.currentTimeMillis() / 1000;
            JSONObject payload = new JSONObject();
            payload.put("iss", clientEmail);
            payload.put("scope", "https://www.googleapis.com/auth/cloud-platform");
            payload.put("aud", TOKEN_URL);
            payload.put("iat", now);
            payload.put("exp", now + 3600);

            String payloadBase64 = Base64.encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
            String signatureInput = header + "." + payloadBase64;

            PrivateKey key = getPrivateKey(privateKey);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key);
            signature.update(signatureInput.getBytes(StandardCharsets.UTF_8));
            byte[] signedBytes = signature.sign();
            String jwt = signatureInput + "." + Base64.encodeToString(signedBytes, Base64.NO_WRAP);

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    .add("assertion", jwt)
                    .build();

            Request request = new Request.Builder()
                    .url(TOKEN_URL)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONObject responseJson = new JSONObject(response.body().string());
                return responseJson.getString("access_token");
            } else {
                Log.e(TAG, "Failed to obtain access token: " + response.body());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating access token", e);
        }
        return null;
    }

    private PrivateKey getPrivateKey(String key) throws Exception {
        String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.decode(privateKeyPEM, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
