package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.SessionSummaryContent;

import java.io.File;
import java.io.IOException;

import Util.ContentDownloadHelper;
import Voice.VoiceRecogniser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseFragment extends Fragment implements VoiceRecogniser.VoiceCallback {
    private static final String TAG = ExerciseFragment.class.getSimpleName();

    private ImageView mLanguageImage;
    private TextView mLanguage;
    private TextView mProgress;
    private ImageView mWordIllustration;
    //private TextView mWord;
    //private TextView mPhoneticSpelling;
    private TextView mWordDescription;
    private ScrollView mWordDescriptionView;
    private TextView mScore;
    private ImageView mProceed;
    private LinearLayout mLivesBig;
    private LinearLayout mLivesSmall;
    private RelativeLayout mScoreFeedback;
    private ImageView mRecord;
    private ImageView mListen;
    private ImageView mBigLife1;
    private ImageView mBigLife2;
    private ImageView mBigLife3;
    private ImageView mSmallLife1;
    private ImageView mSmallLife2;
    private ImageView mSmallLife3;
    private ImageView mSideLogo;

    private MediaPlayer mMediaPlayer;
    private boolean played = false; //TESTING, Illegal state exception on second playback...
    private boolean mClicked = false;
    private boolean firstAttempt = true;

    private int mCursorPosition = -1;
    private int mSessionWord = 0;
    private int mCurrentWordBestScore;
    private int mSessionCumulativeScore = 0;

    private int mSessionWordPasses = 0;
    private int mSessionWorstScore = 100;
    private int mSessionBestScore = 0;
    private String mSessionWorstWord = "";
    private String mSessionBestWord = "";

    private String mCurrentWord;
    private String mLanguageString;
    private String mWordIllustrationUri;
    private String mLanguageFlagUri;
    private String mLanguageHex;
    private int mLanguageId;

    private boolean mPassedWord = false;
    private int mLives = ExerciseContent.MAXIMUM_LIVES;

    private OnFragmentInteractionListener mListener;
    private VoiceRecogniser mVoiceRecogniser;

    public static ExerciseFragment newInstance() {
        ExerciseFragment fragment = new ExerciseFragment();
        return fragment;
    }

    public ExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        mLanguageImage = (ImageView) view.findViewById(R.id.language_image);
        mLanguage = (TextView) view.findViewById(R.id.language);
        mProgress = (TextView) view.findViewById(R.id.progess);
        mWordIllustration = (ImageView) view.findViewById(R.id.word_illustration);
        //mWord = (TextView) view.findViewById(R.id.word);
        //mPhoneticSpelling = (TextView) view.findViewById(R.id.phonetic_spelling);
        mWordDescription = (TextView) view.findViewById(R.id.word_description);
        mWordDescriptionView = (ScrollView) view.findViewById(R.id.word_description_view);
        mScore = (TextView) view.findViewById(R.id.score);
        mProceed = (ImageView) view.findViewById(R.id.proceed);
        mLivesBig = (LinearLayout) view.findViewById(R.id.lives_section);
        mLivesSmall = (LinearLayout) view.findViewById(R.id.lives_section_small);
        mScoreFeedback = (RelativeLayout) view.findViewById(R.id.score_feedback);

        mRecord = (ImageView) view.findViewById(R.id.record_icon);
        mListen = (ImageView) view.findViewById(R.id.listen_icon);
        mBigLife1 = (ImageView) view.findViewById(R.id.heart_big_1);
        mBigLife2 = (ImageView) view.findViewById(R.id.heart_big_2);
        mBigLife3 = (ImageView) view.findViewById(R.id.heart_big_3);
        mSmallLife1 = (ImageView) view.findViewById(R.id.heart_small_1);
        mSmallLife2 = (ImageView) view.findViewById(R.id.heart_small_2);
        mSmallLife3 = (ImageView) view.findViewById(R.id.heart_small_3);
        mSideLogo = (ImageView) view.findViewById(R.id.connoisseur_side);

        setListeners();
        setInitialView();

        return view;
    }

    private void setListeners() {
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                concludeWordAttempt();
                mListener.nextExercise();
            }
        });
        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClicked = !mClicked;
                if (mClicked) {
                    mVoiceRecogniser.startListening();
                    mClicked = !mClicked;
                } else {
                    mVoiceRecogniser.stopListening();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Plays the exercise sound recording
     */
    public void playRecording() {
        if (mMediaPlayer != null && !played) {
            try {

                mMediaPlayer.prepare();
                mMediaPlayer.start();
                played = true;

            } catch (IOException e) {
                Log.d(TAG, "Unable to prepare sound recording");
                e.printStackTrace();
            }
        }
    }

    /**
     * Shifts screen to next exercise by loading the next word from the cursor
     * @param c
     */
    public void nextExercise(Cursor c) {
        if (c == null) { return; }
        mCursorPosition += 1;
        mSessionWord += 1;
        mCurrentWordBestScore = 0;

        mLives = ExerciseContent.MAXIMUM_LIVES;
        firstAttempt = true;

        //CursorHelper.toString(c); //Testing

        Log.d(TAG, "Exercise cursor position: " + String.valueOf(mCursorPosition));

        c.moveToPosition(mCursorPosition);
        if (c.isAfterLast()) { concludeSession(); getActivity().finish(); return; }

        setInitialView();

        mProgress.setText(String.valueOf(mSessionWord));

        mLanguageString = c.getString(c.getColumnIndex(ExerciseContent.LANGUAGE));
        mLanguage.setText(mLanguageString);

        mWordDescription.setText(c.getString(c.getColumnIndex(ExerciseContent.WORD_DESCRIPTION)));

        //mWord.setText(c.getString(c.getColumnIndex(ExerciseContent.WORD)));
        mCurrentWord = c.getString(c.getColumnIndex(ExerciseContent.WORD));
        //mPhoneticSpelling.setText(c.getString(c.getColumnIndex(ExerciseContent.PHONETIC)));

        mWordIllustrationUri = c.getString(c.getColumnIndex(ExerciseContent.IMAGE_URL));
        mLanguageId = c.getInt(c.getColumnIndex(ExerciseContent.LANGUAGE_ID));

        ContentDownloadHelper.loadImage(getActivity(), mWordIllustration, mWordIllustrationUri);
        setSoundFile(c.getString(c.getColumnIndex(ExerciseContent.SOUND_RECORDING)));

        String word = c.getString(c.getColumnIndex(ExerciseContent.WORD));
        String locale = c.getString(c.getColumnIndex(ExerciseContent.LOCALE));
        locale = locale == null ? "en-GB" : locale;

        mVoiceRecogniser = new VoiceRecogniser(getActivity(),  word, locale, this);
    }

    /**
     * Change the text colour of the language to given hex value stored in db
     * @param hex hex value of language text colour
     */
    public void setLanguageSpecifics(String hex, String image_path) {
        mLanguageFlagUri = image_path;
        mLanguageHex = hex;
        try {
            mLanguage.setTextColor(Color.parseColor(hex));
            mProgress.setTextColor(Color.parseColor(hex));
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Illegal Hex was provided for language - check database value!");
            e.printStackTrace();
        }

        ContentDownloadHelper.loadImage(getActivity(), mLanguageImage, image_path);
    }

    /**
     * Prepares the sound file for playing
     * @param path
     */
    private void setSoundFile(String path) {
        if (path == null) { Log.d(TAG, "SoundPath is null"); return; }

        played = false;
        path = path.replace(File.separator, "");

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getActivity().getFilesDir()+ "/" + path);
            mMediaPlayer = mediaPlayer;

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {mp.release(); Log.d(TAG, "MediaPlayer onCompletion!");
                }
            });

            Log.d(TAG, "mediaPlayer set with source: " + getActivity().getFilesDir() + path);

        } catch (IOException e) {
            Log.d(TAG, "setSoundFile - cannot setDataSource: --getActivity().getFilesDir()-- " + path);
            e.printStackTrace();
        }
    }

    //UI change as a result of a successful recording attempt
    private void onSuccess() {
        mLivesBig.setVisibility(View.GONE);
        mLivesSmall.setVisibility(View.VISIBLE);
        mWordDescriptionView.setVisibility(View.VISIBLE);;
    }

    //UI alterations after first recording attempt by user
    private void afterFirstAttempt() {
        Log.d(TAG, "afterFirstAttempt");
        mScoreFeedback.setVisibility(View.VISIBLE);
        mProceed.setVisibility(View.VISIBLE);
    }

    //Initial UI settings (see story board)
    private void setInitialView() {
        mLivesBig.setVisibility(View.VISIBLE);
        mLivesSmall.setVisibility(View.INVISIBLE);
        mWordDescriptionView.setVisibility(View.GONE);
        mScoreFeedback.setVisibility(View.INVISIBLE);
        mProceed.setVisibility(View.INVISIBLE);
        mRecord.setVisibility(View.VISIBLE);

        updateLives();
    }

    private void updateLives() {

        mBigLife1.setImageResource(R.drawable.heart_green_black);
        mBigLife2.setImageResource(R.drawable.heart_green_black);
        mBigLife3.setImageResource(R.drawable.heart_green_black);
        mSmallLife1.setImageResource(R.drawable.heart_green_black);
        mSmallLife2.setImageResource(R.drawable.heart_green_black);
        mSmallLife3.setImageResource(R.drawable.heart_green_black);

        switch(mLives) {
            case 3:
                mBigLife3.setImageResource(R.drawable.heart_green_large);
                mSmallLife3.setImageResource(R.drawable.heart_green_small);
            case 2:
                mBigLife2.setImageResource(R.drawable.heart_green_large);
                mSmallLife2.setImageResource(R.drawable.heart_green_small);
            case 1:
                mBigLife1.setImageResource(R.drawable.heart_green_large);
                mSmallLife1.setImageResource(R.drawable.heart_green_small);
                break;
            case 0:
                mRecord.setVisibility(View.GONE);
        }
    }

    // House keeping as we move onto the next word in a session
    private void concludeWordAttempt() {
        //Maintain cumulative score for average calculation
        mSessionCumulativeScore += mCurrentWordBestScore;

        //Maintain best and worst scores/words
        if (mCurrentWordBestScore > mSessionBestScore) {
            mSessionBestScore = mCurrentWordBestScore;
            mSessionBestWord = mCurrentWord;
        } else if (mCurrentWordBestScore < mSessionWorstScore) {
            mSessionWorstScore = mCurrentWordBestScore;
            mSessionWorstWord = mCurrentWord;
        }

        //Main number of words passed
        if (mCurrentWordBestScore > ExerciseContent.SCORE_PASS) {
            mSessionWordPasses += 1;
        }

    }

    private void concludeSession() {
        mSessionWord -= 1; //Since we increment before we realise we have none left, correct here
        Intent intent = new Intent(getActivity(), SessionSummary.class);

        intent.putExtra(SessionSummaryContent.AVERAGE_SCORE, mSessionCumulativeScore / mSessionWord);
        intent.putExtra(SessionSummaryContent.BEST_SCORE, mSessionBestScore);
        intent.putExtra(SessionSummaryContent.BEST_WORD, mSessionBestWord);
        intent.putExtra(SessionSummaryContent.WORST_SCORE, mSessionWorstScore);
        intent.putExtra(SessionSummaryContent.WORST_WORD, mSessionWorstWord);
        intent.putExtra(SessionSummaryContent.LANGUAGE_FLAG_URI, mLanguageFlagUri);
        intent.putExtra(SessionSummaryContent.WORDS_PASSED, mSessionWordPasses);
        intent.putExtra(SessionSummaryContent.TOTAL_WORDS, mSessionWord);
        intent.putExtra(SessionSummaryContent.LANGUAGE, mLanguageString);
        intent.putExtra(SessionSummaryContent.LANGUAGE_ID, mLanguageId);
        intent.putExtra(SessionSummaryContent.LANGUAGE_HEX, mLanguageHex);

        startActivity(intent);
    }

    @Override
    public void updateScore(float a) {
        if(firstAttempt) { afterFirstAttempt(); firstAttempt = false; }

        int score = Math.round(a * 100);
        Log.d(TAG, "Pronunciation Score: " + String.valueOf(score));

        if (score == -100) {
            //TODO: retry message?
            mScore.setText("0%");
        } else {
            mScore.setText(String.valueOf(score) + "%");
        }

        mLives -= 1;

        //Updates current word best score
        mCurrentWordBestScore = score > mCurrentWordBestScore ? score : mCurrentWordBestScore;

        if (score < ExerciseContent.SCORE_PASS) {
            mPassedWord = true;
        } else if (score >= ExerciseContent.SCORE_PASS && score < ExerciseContent.SCORE_CONNOISSEUR) {
            mPassedWord = true;
            onSuccess();
        } else {
            onSuccess();
            //Connoisseurship - save score?
        }
        updateLives();

        Log.d(TAG, "Cumulative score: " + String.valueOf(mSessionCumulativeScore) + ". BestCurrent: " + String.valueOf(mCurrentWordBestScore));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        public void nextExercise();
    }


}
