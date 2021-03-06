package com.theconnoisseur.android.Model;

import android.net.Uri;

public class InternalDbContract {

    public final static String DATABASE_NAME = "content";
    public final static String DATABASE_PATH = "/data/data/com.theconnoisseur/databases/content";

    public static final String CONTENT_AUTHORITY = "connoisseur";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String[] PROJECTION_LANGUAGES = {
            LanguageSelection.LANGUAGE_ID,
            LanguageSelection.LANGUAGE_NAME,
            LanguageSelection.LANGUAGE_HEX,
            LanguageSelection.LANGUAGE_IMAGE_URL,
            LanguageSelection.LANGUAGE_PLACEHOLDER_CONNOISSEUR,
            LanguageSelection.LANGUAGE_PLACEHOLDER_TOURIST,
            LanguageSelection.LANGUAGE_PLACEHOLDER_BARBARIAN};

    public static final String[] PROJECTION_EXERCISES = {
            ExerciseContent.WORD_ID,
            ExerciseContent.WORD,
            ExerciseContent.PHONETIC,
            ExerciseContent.IMAGE_URL,
            ExerciseContent.SOUND_RECORDING,
            ExerciseContent.WORD_DESCRIPTION,
            ExerciseContent.LANGUAGE_ID,
            ExerciseContent.LANGUAGE,
            ExerciseContent.LOCALE,
            ExerciseContent.VIEW_COMMENTS,
            ExerciseContent.THRESHOLD
    };

    public static final String[] PROJECTION_SCORES = {
            ExerciseScore.USER_ID,
            ExerciseScore.WORD_ID,
            ExerciseScore.PERCENTAGE_SCORE,
            ExerciseScore.ATTEMPTS_SCORE
    };



    public static Uri queryForLanguages() {
        return CONTENT_URI.buildUpon().appendPath(LanguageSelection.LANGUAGE_TABLE_NAME).build();
    }

    public static Uri queryForLanguages(int id) {
        return CONTENT_URI.buildUpon().appendPath(LanguageSelection.LANGUAGE_TABLE_NAME).appendPath(String.valueOf(id)).build();
    }

    public static Uri insertLanguagesUri() {
        return CONTENT_URI.buildUpon().appendPath(LanguageSelection.LANGUAGE_TABLE_NAME).build();
    }

    public static Uri insertExercisesUri() {
        return CONTENT_URI.buildUpon().appendPath(ExerciseContent.EXERICISE_TABLE_NAME).build();
    }

    public static Uri insertExerciseScoreUri() {
        return CONTENT_URI.buildUpon().appendPath(ExerciseScore.SCORE_TABLE_NAME).build();
    }

    public static Uri queryForWords(int id) {
        return CONTENT_URI.buildUpon().appendPath(ExerciseContent.EXERICISE_TABLE_NAME).appendPath(String.valueOf(id)).build();
    }

    public static Uri queryForWordId(String word) {
        return CONTENT_URI.buildUpon().appendPath(ExerciseContent.EXERICISE_TABLE_NAME).appendPath(ExerciseContent.WORD).appendPath(word).build();
    }

    public static Uri queryForAllWords() {
        return CONTENT_URI.buildUpon().appendPath(ExerciseContent.EXERICISE_TABLE_NAME).build();
    }

    public static Uri queryForExerciseScore(int word_id) {
        return CONTENT_URI.buildUpon().appendPath(ExerciseScore.SCORE_TABLE_NAME).appendPath(String.valueOf(word_id)).build();
    }

    public static Uri updateExerciseScore(int word_id) {
        return CONTENT_URI.buildUpon().appendPath(ExerciseScore.SCORE_TABLE_NAME).appendPath(String.valueOf(word_id)).build();
    }

    public static Uri deleteExerciseScore(String username) {
        return CONTENT_URI.buildUpon().appendPath(ExerciseScore.SCORE_TABLE_NAME).appendPath(ExerciseScore.DELETE).appendPath(username).build();
    }

    public static String getWordID(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static String getId(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static String getLanguageId(Uri uri) {
        return uri.getLastPathSegment();
    }

}
