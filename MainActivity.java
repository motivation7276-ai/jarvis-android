package com.ishan.jarvis;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.speech.SpeechRecognizer;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private WebView web;
    private TextToSpeech tts;
    private SpeechRecognizer recognizer;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this, status -> {});
        web = new WebView(this);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new Bridge(), "JarvisAndroid");
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    public class Bridge {
        @JavascriptInterface public void speak(String text) {
            runOnUiThread(() -> {
                if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply");
            });
        }
        @JavascriptInterface public void listen() {
            runOnUiThread(() -> startListening());
        }
        @JavascriptInterface public void openApp(String app) {
            runOnUiThread(() -> {
                String pkg;
                switch (app) {
                    case "whatsapp": pkg = "com.whatsapp"; break;
                    case "instagram": pkg = "com.instagram.android"; break;
                    case "snapchat": pkg = "com.snapchat.android"; break;
                    case "phone":
                        try { startActivity(new Intent(Intent.ACTION_DIAL)); }
                        catch (Exception e) { Toast.makeText(MainActivity.this, "Phone app unavailable", Toast.LENGTH_SHORT).show(); }
                        return;
                    case "settings":
                        try { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); }
                        catch (Exception e) { }
                        return;
                    default: return;
                }
                Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
                if (intent != null) startActivity(intent);
                else Toast.makeText(MainActivity.this, "App is not installed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        if (recognizer != null) recognizer.destroy();
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle p) {}
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() {}
            public void onError(int e) { runOnUiThread(() -> Toast.makeText(MainActivity.this, "Couldn't hear that. Try again.", Toast.LENGTH_SHORT).show()); }
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spoken = matches.get(0);
                    runOnUiThread(() -> web.evaluateJavascript("window.onVoiceResult(" + org.json.JSONObject.quote(spoken) + ")", null));
                }
            }
            public void onPartialResults(Bundle p) {}
            public void onEvent(int t, Bundle p) {}
        });
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try { recognizer.startListening(intent); }
        catch (ActivityNotFoundException e) { Toast.makeText(this, "Speech service unavailable", Toast.LENGTH_SHORT).show(); }
    }

    @Override public void onDestroy() {
        if (recognizer != null) recognizer.destroy();
        if (tts != null) { tts.stop(); tts.shutdown(); }
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
