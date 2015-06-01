package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Model.ExerciseContent;

import Util.CursorHelper;
import Util.ImageDownloadHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseFragment extends Fragment {
    private static final String TAG = ExerciseFragment.class.getSimpleName();

    private ImageView mLanguageImage;
    private TextView mLanguage;
    private TextView mProgress;
    private ImageView mWordIllustration;
    private TextView mWord;
    private TextView mPhoneticSpelling;
    private TextView mWordDescription;

    private ImageView mRecord;
    private ImageView mListen;

    private int mCursorPosition = -1;

    private OnFragmentInteractionListener mListener;

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
        mWord = (TextView) view.findViewById(R.id.word);
        mPhoneticSpelling = (TextView) view.findViewById(R.id.phonetic_spelling);
        mWordDescription = (TextView) view.findViewById(R.id.word_description);

        mRecord = (ImageView) view.findViewById(R.id.record_icon);
        mListen = (ImageView) view.findViewById(R.id.listen_icon);

        setListeners();

        return view;
    }

    private void setListeners() {
        mListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.nextExercise();
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
     * Shifts screen to next exercise by loading the next word from the cursor
     * @param c
     */
    public void nextExercise(Cursor c) {
        if (c == null) { return; }
        mCursorPosition += 1;

        //CursorHelper.toString(c); //Testing

        Log.d(TAG, "Exercise cursor position: " + String.valueOf(mCursorPosition));

        c.moveToPosition(mCursorPosition);
        if (c.isAfterLast()) { return; }

        mLanguage.setText(c.getString(c.getColumnIndex(ExerciseContent.LANGUAGE)));
        mWord.setText(c.getString(c.getColumnIndex(ExerciseContent.WORD)));
        mWordDescription.setText(c.getString(c.getColumnIndex(ExerciseContent.WORD_DESCRIPTION)));
        mPhoneticSpelling.setText(c.getString(c.getColumnIndex(ExerciseContent.PHONETIC)));

        ImageDownloadHelper.loadImage(getActivity(), mWordIllustration, c.getString(c.getColumnIndex(ExerciseContent.IMAGE_URL)));
    }

    /**
     * Change the text colour of the language to given hex value stored in db
     * @param hex hex value of language text colour
     */
    public void setLanguageSpecifics(String hex, String image_path) {
        try {
            mLanguage.setTextColor(Color.parseColor(hex));
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Illegal Hex was provided for language - check database value!");
        }

        ImageDownloadHelper.loadImage(getActivity(), mLanguageImage, image_path);
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