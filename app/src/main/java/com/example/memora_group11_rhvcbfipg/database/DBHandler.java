package com.example.memora_group11_rhvcbfipg.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.memora_group11_rhvcbfipg.modal.FolderModal;
import com.example.memora_group11_rhvcbfipg.modal.WordModal;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "flashcard";
    private static final int DB_VERSION = 2;

    private static final String FOLDERS_TABLE_NAME = "folders";
    private static final String WORDS_TABLE_NAME = "words";
    private static final String ID_COL = "id";
    private static final String FOLDER_ID_COL = "folder_id";
    private static final String FOLDER_NAME_COL = "folder_name";
    private static final String WORD_COL = "word";
    private static final String DESCRIPTION_COL = "description";

    private static final String STATISTICS_TABLE_NAME = "folder_statistics";
    private static final String ATTEMPTS_COL = "attempts";
    private static final String CORRECT_COL = "correct";
    private static final String INCORRECT_COL = "incorrect";

    private static final String HIGHEST_SCORE_COL = "highest_score";
    private static final String AVG_CORRECT_COL = "avg_correct";
    private static final String AVG_INCORRECT_COL = "avg_incorrect";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFoldersTable(db);
        createWordsTable(db);
        createStatisticsTable(db);
    }

    private void createFoldersTable(SQLiteDatabase db) {
        String foldersQuery = "CREATE TABLE " + FOLDERS_TABLE_NAME + "(" +
                FOLDER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FOLDER_NAME_COL + " TEXT)";
        db.execSQL(foldersQuery);
    }

    private void createWordsTable(SQLiteDatabase db) {
        String wordsQuery = "CREATE TABLE " + WORDS_TABLE_NAME + "(" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WORD_COL + " TEXT," +
                DESCRIPTION_COL + " TEXT," +
                FOLDER_ID_COL + " INTEGER," +
                "FOREIGN KEY(" + FOLDER_ID_COL + ") REFERENCES " + FOLDERS_TABLE_NAME + "(" + FOLDER_ID_COL + "))";
        db.execSQL(wordsQuery);
    }

    public void addFolder(String folderName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FOLDER_NAME_COL, folderName);
        db.insert(FOLDERS_TABLE_NAME, null, cv);
        db.close();
    }

    public ArrayList<FolderModal> getFolderNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT * FROM " + FOLDERS_TABLE_NAME, null);

        ArrayList<FolderModal> folderArrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int folderId = cursor.getInt(cursor.getColumnIndexOrThrow(FOLDER_ID_COL));
                String folderName = cursor.getString(cursor.getColumnIndexOrThrow(FOLDER_NAME_COL));
                folderArrayList.add(new FolderModal(folderId, folderName));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return folderArrayList;
    }

    public ArrayList<WordModal> getWordsByFolderId(int folderId) {
        ArrayList<WordModal> wordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {ID_COL, WORD_COL, DESCRIPTION_COL};
        String selection = FOLDER_ID_COL + " = ?";
        String[] selectionArgs = {String.valueOf(folderId)};

        Cursor cursor = db.query(WORDS_TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int wordId = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(WORD_COL));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL));
                wordList.add(new WordModal(folderId, wordId, word, description));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return wordList;
    }

    public void addWord(int folderId, String word, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FOLDER_ID_COL, folderId);
        cv.put(WORD_COL, word);
        cv.put(DESCRIPTION_COL, description);
        db.insert(WORDS_TABLE_NAME, null, cv);
        db.close();
    }

    public int getTotalWordsInAFolder(int folderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalWords = 0;
        String[] projection = {ID_COL};
        String selection = FOLDER_ID_COL + " = ?";
        String[] selectionArgs = {String.valueOf(folderId)};

        Cursor cursor = db.query(WORDS_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            totalWords = cursor.getCount();
            cursor.close();
        }
        db.close();
        return totalWords;
    }

    public void updateFolderName(int folderId, String newFolderName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FOLDER_NAME_COL, newFolderName);
        db.update(FOLDERS_TABLE_NAME, values, FOLDER_ID_COL + " = ?", new String[]{String.valueOf(folderId)});
        db.close();
    }

    public void deleteFolder(int folderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteWordsInFolder(db, folderId);
        db.delete(FOLDERS_TABLE_NAME, FOLDER_ID_COL + "=?", new String[]{String.valueOf(folderId)});
        db.close();
    }
    private void deleteWordsInFolder(SQLiteDatabase db, int folderId) {
        db.delete(WORDS_TABLE_NAME, FOLDER_ID_COL + "=?", new String[]{String.valueOf(folderId)});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            // Just add the statistics table without dropping existing data
            createStatisticsTable(db);
        } else {
            // Fall back to complete rebuild for other version jumps
            db.execSQL("DROP TABLE IF EXISTS " + WORDS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + STATISTICS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + FOLDERS_TABLE_NAME);
            onCreate(db);
        }
    }

    public void updateWord(int id, String word, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(WORD_COL, word);
        values.put(DESCRIPTION_COL, description);

        db.update(WORDS_TABLE_NAME, values, ID_COL + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    private void createStatisticsTable(SQLiteDatabase db) {
        String statisticsQuery = "CREATE TABLE " + STATISTICS_TABLE_NAME + "(" +
                FOLDER_ID_COL + " INTEGER PRIMARY KEY," +
                ATTEMPTS_COL + " INTEGER DEFAULT 0," +
                CORRECT_COL + " INTEGER DEFAULT 0," +
                INCORRECT_COL + " INTEGER DEFAULT 0," +
                HIGHEST_SCORE_COL + " INTEGER DEFAULT 0," +
                AVG_CORRECT_COL + " REAL DEFAULT 0," +
                AVG_INCORRECT_COL + " REAL DEFAULT 0," +
                "FOREIGN KEY(" + FOLDER_ID_COL + ") REFERENCES " + FOLDERS_TABLE_NAME + "(" + FOLDER_ID_COL + "))";
        db.execSQL(statisticsQuery);
    }

    public void addOrUpdateStatistics(int folderId, int attempts, int correct, int incorrect,
                                      int highestScore, float avgCorrect, float avgIncorrect) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FOLDER_ID_COL, folderId);
        values.put(ATTEMPTS_COL, attempts);
        values.put(CORRECT_COL, correct);
        values.put(INCORRECT_COL, incorrect);
        values.put(HIGHEST_SCORE_COL, highestScore);
        values.put(AVG_CORRECT_COL, avgCorrect);
        values.put(AVG_INCORRECT_COL, avgIncorrect);

        int rowsAffected = db.update(STATISTICS_TABLE_NAME, values, FOLDER_ID_COL + " = ?",
                new String[]{String.valueOf(folderId)});
        if (rowsAffected == 0) {
            db.insert(STATISTICS_TABLE_NAME, null, values);
        }
        db.close();
    }

    // Keep existing method signature for backward compatibility
    public void addOrUpdateStatistics(int folderId, int attempts, int correct, int incorrect) {
        // Get current stats to preserve other fields
        Cursor cursor = getStatisticsByFolderId(folderId);
        int highestScore = 0;
        float avgCorrect = 0;
        float avgIncorrect = 0;

        if (cursor != null && cursor.moveToFirst()) {
            int highestScoreIndex = cursor.getColumnIndex(HIGHEST_SCORE_COL);
            int avgCorrectIndex = cursor.getColumnIndex(AVG_CORRECT_COL);
            int avgIncorrectIndex = cursor.getColumnIndex(AVG_INCORRECT_COL);

            if (highestScoreIndex >= 0) highestScore = cursor.getInt(highestScoreIndex);
            if (avgCorrectIndex >= 0) avgCorrect = cursor.getFloat(avgCorrectIndex);
            if (avgIncorrectIndex >= 0) avgIncorrect = cursor.getFloat(avgIncorrectIndex);

            cursor.close();
        }

        addOrUpdateStatistics(folderId, attempts, correct, incorrect, highestScore, avgCorrect, avgIncorrect);
    }

    // Method to update statistics after a new attempt
    public void updateStatisticsAfterAttempt(int folderId, int correctCount, int incorrectCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = getStatisticsByFolderId(folderId);

        int attempts = 1;
        int correct = correctCount;
        int incorrect = incorrectCount;
        int highestScore = correctCount;
        float avgCorrect = correctCount;
        float avgIncorrect = incorrectCount;

        if (cursor != null && cursor.moveToFirst()) {
            int attemptsIndex = cursor.getColumnIndex(ATTEMPTS_COL);
            int correctIndex = cursor.getColumnIndex(CORRECT_COL);
            int incorrectIndex = cursor.getColumnIndex(INCORRECT_COL);
            int highestScoreIndex = cursor.getColumnIndex(HIGHEST_SCORE_COL);
            int avgCorrectIndex = cursor.getColumnIndex(AVG_CORRECT_COL);
            int avgIncorrectIndex = cursor.getColumnIndex(AVG_INCORRECT_COL);

            if (attemptsIndex >= 0) attempts = cursor.getInt(attemptsIndex) + 1;
            if (correctIndex >= 0) correct = cursor.getInt(correctIndex) + correctCount;
            if (incorrectIndex >= 0) incorrect = cursor.getInt(incorrectIndex) + incorrectCount;

            // Update highest score if current score is higher
            if (highestScoreIndex >= 0) {
                int currentHighest = cursor.getInt(highestScoreIndex);
                highestScore = Math.max(currentHighest, correctCount);
            }

            // Calculate running average
            if (avgCorrectIndex >= 0 && attemptsIndex >= 0) {
                float currentAvgCorrect = cursor.getFloat(avgCorrectIndex);
                float oldTotal = currentAvgCorrect * (attempts - 1);
                avgCorrect = (oldTotal + correctCount) / attempts;
            }

            if (avgIncorrectIndex >= 0 && attemptsIndex >= 0) {
                float currentAvgIncorrect = cursor.getFloat(avgIncorrectIndex);
                float oldTotal = currentAvgIncorrect * (attempts - 1);
                avgIncorrect = (oldTotal + incorrectCount) / attempts;
            }

            cursor.close();
        }

        addOrUpdateStatistics(folderId, attempts, correct, incorrect, highestScore, avgCorrect, avgIncorrect);
        db.close();
    }

    public void deleteWord(int wordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WORDS_TABLE_NAME, ID_COL + "=?", new String[]{String.valueOf(wordId)});
        db.close();
    }


    public Cursor getStatisticsByFolderId(int folderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(STATISTICS_TABLE_NAME, null, FOLDER_ID_COL + " = ?", new String[]{String.valueOf(folderId)}, null, null, null);
    }
}
