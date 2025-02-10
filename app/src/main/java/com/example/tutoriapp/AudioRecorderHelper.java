package com.example.tutoriapp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioRecorderHelper {

    public interface OnRecordingCompleteListener {
        void onRecordingComplete(String base64Audio);
    }

    public static void startRecording(OnRecordingCompleteListener listener) {
        int sampleRate = 16000;
        int channels = AudioFormat.CHANNEL_IN_MONO;
        int encoding = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channels, encoding) * 3;

        //Ignore the compiler warnings for this
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sampleRate, channels, encoding, bufferSize);

        byte[] audioBuffer = new byte[bufferSize];
        recorder.startRecording();

        ByteArrayOutputStream recordedStream = new ByteArrayOutputStream();

        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                int totalBytes = 0;

                while ((System.currentTimeMillis() - startTime) < 2000) { // Record for 1.5s
                    int read = recorder.read(audioBuffer, 0, audioBuffer.length);
                    if (read > 0) {
                        recordedStream.write(audioBuffer, 0, read);
                        totalBytes += read;
                    }
                }

                recorder.stop();
                recorder.release();

                byte[] recordedAudio = recordedStream.toByteArray();

                if (isMostlySilence(recordedAudio)) {
                    Log.e("AudioRecorder", "Recording was mostly silence, skipping...");
                    return;
                }

                // Convert PCM to WAV
                byte[] wavData = addWavHeader(recordedAudio, sampleRate, 1);
                String base64Audio = Base64.encodeToString(wavData, Base64.NO_WRAP);

                listener.onRecordingComplete(base64Audio);
                Log.d("SpeechToText", "Base64 Audio (first 50 chars): " + base64Audio.substring(0, 50));

            } catch (Exception e) {
                Log.e("AudioRecorder", "Error processing audio", e);
            }
        }).start();
    }

    private static boolean isMostlySilence(byte[] audioData) {
        int silentSamples = 0;
        int totalSamples = audioData.length / 2;

        for (int i = 0; i < audioData.length; i += 2) {
            short sample = ByteBuffer.wrap(audioData, i, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
            if (Math.abs(sample) < 500) {
                silentSamples++;
            }
        }

        return ((float) silentSamples / totalSamples) > 0.8;
    }

    private static byte[] addWavHeader(byte[] pcmData, int sampleRate, int channels) {
        int byteRate = sampleRate * channels * 2;
        int totalDataLen = pcmData.length + 36;
        int totalAudioLen = pcmData.length;

        ByteBuffer buffer = ByteBuffer.allocate(44 + pcmData.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put("RIFF".getBytes());
        buffer.putInt(totalDataLen);
        buffer.put("WAVE".getBytes());
        buffer.put("fmt ".getBytes());
        buffer.putInt(16);
        buffer.putShort((short) 1);
        buffer.putShort((short) channels);
        buffer.putInt(sampleRate);
        buffer.putInt(byteRate);
        buffer.putShort((short) (channels * 2));
        buffer.putShort((short) 16);
        buffer.put("data".getBytes());
        buffer.putInt(totalAudioLen);
        buffer.put(pcmData);

        return buffer.array();
    }
}
