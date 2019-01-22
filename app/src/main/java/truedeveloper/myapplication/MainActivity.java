package truedeveloper.myapplication;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static int TTS_DATA_CHECK = 1;
    public static int VOICE_RECOGNITION = 2;
    private TextToSpeech tts;
    public String demotext = "This is a test of the text-to-speech engine in Android.";
    EditText txtinput;
    ArrayList<String> arrl;
    ArrayAdapter<String> adapter;
    ListView listView01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtinput = (EditText)findViewById(R.id.editText1);
        listView01 = (ListView)findViewById(R.id.listview);
        arrl = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, arrl);
    }


    public void testSR(View v){
        arrl.clear();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Just speak normally into your phone");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        try {
            startActivityForResult(intent, VOICE_RECOGNITION);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void testTTS(View v) {
        demotext = txtinput.getText().toString();
        if (demotext.isEmpty()){
            Log.i("SpeechDemo", "## ERROR 02: Field is Empty");
            demotext = "The field is empty. Type something first!";
        }

        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, TTS_DATA_CHECK);
        Toast.makeText(getBaseContext(), "Testing Text to Speech", Toast.LENGTH_SHORT).show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TTS_DATA_CHECK) {
            Log.i("SpeechDemo", "## INFO 01: RequestCode TTS_DATA_CHECK = " + requestCode);
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                Log.i("SpeechDemo", "## INFO 03: CHECK_VOICE_DATA_PASS");
                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                    @Override
                    public void onInit(int arg0) {
                        // TODO Auto-generated method stub -- status
                        if (tts.isLanguageAvailable(Locale.US) >= 0) {
                            tts.setLanguage(Locale.US);
                            tts.setPitch(1.0f);
                            tts.setSpeechRate(1.0f);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ttsGreater21(demotext);
                            } else {
                                ttsUnder20(demotext);
                            }

                        }
                    }

                });

            } else {
                Log.i("SpeechDemo", "## INFO 04: CHECK_VOICE_DATA_FAILED, resultCode = " + resultCode);
                Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installVoice);

            }


        } else if (requestCode == VOICE_RECOGNITION) {
            Log.i("SpeechDemo", "## INFO 02: RequestCode VOICE_RECOGNITION = " + requestCode);
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                for (int i = 0; i < results.size(); i++) {
                    final String result = results.get(i);
                    Log.i("SpeechDemo", "## INFO 05: Result: " + result );
                    arrl.add(result);
                }
                listView01.setAdapter(adapter);
            }




        } else {
            Log.i("SpeechDemo", "## ERROR 01: Unexpected RequestCode = " + requestCode);
        }
    }


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) tts.shutdown();
    }
}
