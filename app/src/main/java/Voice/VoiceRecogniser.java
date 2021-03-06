package Voice;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;


/**
 * Created by dan on 24/05/15.
 */
public class VoiceRecogniser {

    private SpeechRecognizer speechRecognizer;
    private Intent i;
    private VoiceScore voiceScore;

    private final int MAX_RESULTS = 10;

    /*
     * Called with the application context, string we are searching for, language
     * and VoiceCallback
     * Speech computation  usually takes around 500ms
     * Lang must be locale, eg. en-US or ru-RU
     * Usually looks something like this:
     * VoiceRecogniser vr =
     *      new VoiceRecogniser(this, "МЕНЯ", "ru-RU", (VoiceRecogniser.VoiceCallback) this);
     */
    public VoiceRecogniser(Context c, String targetString, String lang, VoiceCallback vc) {
        voiceScore = new VoiceScore();

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        VoiceListener listener = new VoiceListener(targetString, voiceScore, lang, vc);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(c);

        speechRecognizer.setRecognitionListener(listener);

    }

    /*
     * Recogniser will start listening for results
     */
    public void startListening() {
        speechRecognizer.startListening(i);
    }

    /*
     * Recogniser will stop listening for results
     */
    public void stopListening() {
        speechRecognizer.stopListening();
    }

    /*
     * Returns the voiceScore object which will hold the score once computed
     */
    public VoiceScore getVoiceScore() {
        return voiceScore;
    }

    /*
     * This may be useful if optimisation is an issue
     */
    public void destroyVoiceRecogniser() { speechRecognizer.destroy(); }

    public interface VoiceCallback {
        public void updateScore(float a);
    }

}

