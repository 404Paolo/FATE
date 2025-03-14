package com.example.tutoriapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FirebaseUploader {

    private static final String TAG = "FirebaseUploader";
    private static final int SAMPLE_RATE = 48000; // 48 kHz
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int RECORD_TIME_MS = 2000; // 2 seconds
    private FirebaseStorage storage;
    private Context context;

    private FirebaseFirestore db;

    public FirebaseUploader(Context context) {
        this.context = context;
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface ModulesCallback {
        void onSuccess(List<ModuleDoc> moduleDocs);
        void onFailure(String errorMessage);
    }


    // Record PCM audio, convert it to WAV, and upload to Firebase
    public void audioRecordingToURL(UploadCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User is not signed in.");
            return;
        }

        // Define file paths
        File pcmFile = new File(context.getExternalFilesDir(null), "temp_audio.pcm");
        File wavFile = new File(context.getExternalFilesDir(null), "temp_audio.wav");

        // Record PCM audio
        if (!recordPCM(pcmFile)) {
            callback.onFailure("Error recording PCM audio.");
            return;
        }
        if (!convertPCMToWAV(pcmFile, wavFile)) {
            callback.onFailure("Error converting PCM to WAV.");
            return;
        }

        uploadFileAndGetUrl(wavFile, callback);
    }

    private boolean recordPCM(File outputFile) {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        if (ActivityCompat.checkSelfPermission(context,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
        );

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[bufferSize];
            audioRecord.startRecording();
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < RECORD_TIME_MS) {
                int read = audioRecord.read(buffer, 0, buffer.length);
                if (read > 0) {
                    fos.write(buffer, 0, read);
                }
            }

            audioRecord.stop();
            audioRecord.release();
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Error recording PCM: " + e.getMessage());
            return false;
        }
    }

    // Convert PCM to WAV
    private boolean convertPCMToWAV(File pcmFile, File wavFile) {
        try (FileOutputStream fos = new FileOutputStream(wavFile)) {
            byte[] pcmData = java.nio.file.Files.readAllBytes(pcmFile.toPath());

            // Write WAV header
            fos.write(createWavHeader(pcmData.length));

            // Write PCM data
            fos.write(pcmData);
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Error converting PCM to WAV: " + e.getMessage());
            return false;
        }
    }

    // Generate a WAV file header
    private byte[] createWavHeader(int pcmDataSize) {
        int totalDataLen = pcmDataSize + 36;
        int byteRate = SAMPLE_RATE * 2; // 16-bit PCM = 2 bytes per sample

        ByteBuffer buffer = ByteBuffer.allocate(44);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // RIFF header
        buffer.put("RIFF".getBytes());
        buffer.putInt(totalDataLen);
        buffer.put("WAVE".getBytes());

        // fmt subchunk
        buffer.put("fmt ".getBytes());
        buffer.putInt(16); // Subchunk1Size (16 for PCM)
        buffer.putShort((short) 1); // AudioFormat (1 for PCM)
        buffer.putShort((short) 1); // NumChannels (1 for mono)
        buffer.putInt(SAMPLE_RATE);
        buffer.putInt(byteRate);
        buffer.putShort((short) 2); // BlockAlign
        buffer.putShort((short) 16); // BitsPerSample

        // data subchunk
        buffer.put("data".getBytes());
        buffer.putInt(pcmDataSize);

        return buffer.array();
    }

    // Upload the file to Firebase and return the URL
    private void uploadFileAndGetUrl(File audioFile, UploadCallback callback) {
        if (audioFile == null || !audioFile.exists()) {
            callback.onFailure("Invalid file.");
            return;
        }

        // Ensure the user is signed in before uploading
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User is not signed in.");
            return;
        }

        // Optionally initialize App Check if not already done
        initializeAppCheck();

        // Create a unique filename for the file
        String fileName = "audio/" + UUID.randomUUID().toString() + ".wav";
        StorageReference storageReference = storage.getReference().child(fileName);

        // Upload the file
        UploadTask uploadTask = storageReference.putFile(Uri.fromFile(audioFile));

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // File uploaded successfully, now get the download URL
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String fileUrl = uri.toString();
                Log.d(TAG, "File uploaded successfully. URL: " + fileUrl);
                callback.onSuccess(fileUrl); // Return the file URL
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting download URL: " + e.getMessage());
                callback.onFailure("Failed to get download URL.");
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading file: " + e.getMessage());
            callback.onFailure("Failed to upload file.");
        });
    }

    private void initializeAppCheck() {
        try {
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
            Log.d(TAG, "App Check initialized.");
        } catch (Exception e) {
            Log.e(TAG, "App Check initialization failed: " + e.getMessage());
        }
    }

    public interface UploadCallback {
        void onSuccess(String fileUrl);
        void onFailure(String errorMessage);
    }

    public void recordQuizAttempt(Map<String, Boolean> questionMap, float percentage, String moduleId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User is not signed in.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("students").document(currentUser.getUid());
        DocumentReference moduleRef = userRef.collection("performance").document(moduleId);
        CollectionReference attemptsRef = moduleRef.collection("attempts");

        attemptsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int nextAttempt = queryDocumentSnapshots.size() + 1;

            Map<String, Object> attemptData = new HashMap<>();
            attemptData.put("questionMap", questionMap);
            attemptData.put("percentage", percentage);
            attemptData.put("timestamp", FieldValue.serverTimestamp());

            attemptsRef.document(String.valueOf(nextAttempt)).set(attemptData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Quiz attempt uploaded successfully.");

                        moduleRef.get().addOnSuccessListener(documentSnapshot -> {
                            long totalAttempts = documentSnapshot.contains("totalAttempts") ? documentSnapshot.getLong("totalAttempts") + 1 : 1;
                            float highestScore = documentSnapshot.contains("highestScore") ? Math.max(documentSnapshot.getDouble("highestScore").floatValue(), percentage) : percentage;
                            float latestScore = percentage;

                            List<DocumentSnapshot> attemptDocs = queryDocumentSnapshots.getDocuments();
                            float sumScores = percentage;
                            for (DocumentSnapshot doc : attemptDocs) {
                                if (doc.contains("percentage")) {
                                    sumScores += doc.getDouble("percentage").floatValue();
                                }
                            }
                            float averageScore = sumScores / totalAttempts;

                            Map<String, Object> performanceData = new HashMap<>();
                            performanceData.put("moduleName", moduleId);
                            performanceData.put("totalAttempts", totalAttempts);
                            performanceData.put("highestScore", highestScore);
                            performanceData.put("latestScore", latestScore);
                            performanceData.put("averageScore", averageScore);
                            performanceData.put("latestAttemptId", nextAttempt);
                            performanceData.put("highestScoredAttemptId", (highestScore == percentage) ? nextAttempt : documentSnapshot.getLong("highestScoredAttemptId"));

                            moduleRef.set(performanceData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid2 -> {
                                        Log.d(TAG, "Performance document updated successfully.");
                                        updateLocalPerformanceFile(currentUser.getUid(), moduleId, performanceData);
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error updating performance document: " + e.getMessage()));
                        });
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error uploading quiz attempt: " + e.getMessage()));
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching attempts: " + e.getMessage()));
    }

    private void updateLocalPerformanceFile(String userId, String moduleId, Map<String, Object> performanceData, Map<String, Object> attemptData, int attemptId) {
        File file = new File(context.getFilesDir(), "performance_data.json");
        JSONObject json;

        try {
            // Load existing JSON file or create a new one
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                json = new JSONObject(content);
            } else {
                json = new JSONObject();
            }

            // Get module JSON object, or create a new one if it doesn't exist
            JSONObject moduleJson = json.optJSONObject(moduleId);
            if (moduleJson == null) {
                moduleJson = new JSONObject();
            }

            // Store performance data (module-level summary)
            for (Map.Entry<String, Object> entry : performanceData.entrySet()) {
                moduleJson.put(entry.getKey(), entry.getValue());
            }

            // Store attempt data (individual attempts)
            JSONArray attemptsArray = moduleJson.optJSONArray("attempts");
            if (attemptsArray == null) {
                attemptsArray = new JSONArray();
            }

            JSONObject attemptJson = new JSONObject();
            attemptJson.put("attemptId", attemptId);
            attemptJson.put("questionMap", new JSONObject((Map<?, ?>) attemptData.get("questionMap")));
            attemptJson.put("percentage", attemptData.get("percentage"));
            attemptJson.put("timestamp", attemptData.get("timestamp").toString());

            attemptsArray.put(attemptJson);
            moduleJson.put("attempts", attemptsArray); // Update module JSON

            json.put(moduleId, moduleJson); // Update main JSON

            // Write back to file
            Files.write(file.toPath(), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error updating local performance file: " + e.getMessage());
        }
    }


    public static List<ModuleDoc> loadPerformanceData(Context context) {
        File file = new File(context.getFilesDir(), "performance_data.json");
        if (!file.exists()) {
            Log.d("Paolo", "Shits Empty brah");
            return new ArrayList<>(); // Return empty list if no data is found
        }

        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        String json = jsonBuilder.toString();
        Gson gson = new Gson();

        Type listType = new TypeToken<List<ModuleDoc>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}