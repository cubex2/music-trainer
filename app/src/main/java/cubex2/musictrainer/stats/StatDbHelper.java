package cubex2.musictrainer.stats;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cubex2.musictrainer.data.ErrorType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class StatDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Stat.db";

    private static final String SQL_CREATE_STATS = "CREATE TABLE " + StatContract.StatEntry.TABLE_NAME + " (" +
                                                   StatContract.StatEntry._ID + " INTEGER PRIMARY KEY," +
                                                   StatContract.StatEntry.COLUMN_NAME_TIMESTAMP + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_DURATION_ERROR + " REAL," +
                                                   StatContract.StatEntry.COLUMN_NAME_VOLUME_ERROR + " REAL," +
                                                   StatContract.StatEntry.COLUMN_NAME_FREQUENCY_ERROR + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_ERROR + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_ERROR + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_HAS_FREQUENCY_ERROR + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_MISTAKE + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_MISTAKE + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_HAS_FREQUENCY_MISTAKE + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_CORRECT + " INTEGER)";

    private static final String SQL_DELETE_STATS = "DROP TABLE IF EXISTS " + StatContract.StatEntry.TABLE_NAME;

    private static final String WHERE_ERROR_TYPE_AND_ERROR_AND_TIME = StatContract.StatEntry.COLUMN_NAME_TIMESTAMP + ">=%1$d AND " +
                                                                      "%2$s=1 AND %3$s > %4$.3f AND %3$s < %5$.3f";

    private static final String WHERE_ERROR_TYPE_AND_NOT_ERROR = "%1$s=1 AND (%2$s < %3$.3f AND %2$s > %4$.3f)";

    private static final String WHERE_ERROR_TYPE = "%1$s=1";

    public StatDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_STATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_STATS);
        db.execSQL(SQL_CREATE_STATS);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    public List<StatEntry> readEntries(SQLiteDatabase db, int maxEntries)
    {
        return readEntries(db, null, null, maxEntries);
    }

    /**
     * Read the most recent entries with the given errorType and error. Any entries that are older than an entry with
     * the same errorType but different error are not returned.
     */
    public List<StatEntry> readEntriesWithError(SQLiteDatabase db, ErrorType errorType, float error, int maxEntries)
    {
        StatEntry mostRecent = readMostRecentEntry(db, errorType);
        if (mostRecent == null || Math.abs(mostRecent.getError(errorType) - error) > 0.01f)
            return Collections.emptyList();

        StatEntry mostRecentDifferentError = readMostRecentWithDifferentError(db, errorType, error);
        long minTime = mostRecentDifferentError != null ? mostRecentDifferentError.getTimeStamp() : 0;

        String hasErrorColumnName = columnNameForHasError(errorType);
        String errorColumnName = columnNameForError(errorType);

        float d = 0.01f;
        String where = String.format(Locale.ENGLISH, WHERE_ERROR_TYPE_AND_ERROR_AND_TIME, minTime, hasErrorColumnName, errorColumnName, error - d, error + d);
        return readEntries(db, where, null, maxEntries);
    }

    /**
     * Reads the most recent entry that has the given errorType.
     */
    private StatEntry readMostRecentEntry(SQLiteDatabase db, ErrorType errorType)
    {
        String hasErrorColumnName = columnNameForHasError(errorType);

        String where = String.format(Locale.ENGLISH, WHERE_ERROR_TYPE, hasErrorColumnName);
        List<StatEntry> entries = readEntries(db, where, null, 1);
        return entries.isEmpty() ? null : entries.get(0);
    }

    /**
     * Read the most recent entry that has the given errorType and a different error than the given one.
     */
    private StatEntry readMostRecentWithDifferentError(SQLiteDatabase db, ErrorType errorType, float error)
    {
        String hasErrorColumnName = columnNameForHasError(errorType);
        String errorColumnName = columnNameForError(errorType);

        float d = 0.01f;
        String where = String.format(Locale.ENGLISH, WHERE_ERROR_TYPE_AND_NOT_ERROR, hasErrorColumnName, errorColumnName, error - d, error + d);
        List<StatEntry> entries = readEntries(db, where, null, 1);

        return entries.isEmpty() ? null : entries.get(0);
    }

    private String columnNameForHasError(ErrorType errorType)
    {
        switch (errorType)
        {
            case DURATION:
                return StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_ERROR;
            case VOLUME:
                return StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_ERROR;
            case FREQUENCY:
                return StatContract.StatEntry.COLUMN_NAME_HAS_FREQUENCY_ERROR;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private String columnNameForError(ErrorType errorType)
    {
        switch (errorType)
        {
            case DURATION:
                return StatContract.StatEntry.COLUMN_NAME_DURATION_ERROR;
            case VOLUME:
                return StatContract.StatEntry.COLUMN_NAME_VOLUME_ERROR;
            case FREQUENCY:
                return StatContract.StatEntry.COLUMN_NAME_FREQUENCY_ERROR;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public List<StatEntry> readEntries(SQLiteDatabase db, String selection, String[] selectionArgs, int maxEntries)
    {
        String sortOrder = StatContract.StatEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor cursor = db.query(
                StatContract.StatEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                String.valueOf(maxEntries));

        List<StatEntry> entries = new LinkedList<>();
        while (cursor.moveToNext())
        {
            entries.add(readEntry(cursor));
        }

        cursor.close();

        return entries;
    }

    private StatEntry readEntry(Cursor cursor)
    {
        long timeStamp = cursor.getLong(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_TIMESTAMP));
        boolean wasCorrect = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_CORRECT)) == 1;

        float durationError = cursor.getFloat(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_DURATION_ERROR));
        float volumeError = cursor.getFloat(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_VOLUME_ERROR));
        int frequencyError = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_FREQUENCY_ERROR));

        boolean hasDurationError = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_ERROR)) == 1;
        boolean hasVolumeError = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_ERROR)) == 1;
        boolean hasFrequencyError = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_HAS_FREQUENCY_ERROR)) == 1;

        boolean hasDurationMistake = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_MISTAKE)) == 1;
        boolean hasVolumeMistake = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_MISTAKE)) == 1;
        boolean hasFrequencyMistake = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_HAS_FREQUENCY_MISTAKE)) == 1;

        return new StatEntry(timeStamp, wasCorrect,
                             durationError, volumeError, frequencyError,
                             hasDurationError, hasVolumeError, hasFrequencyError,
                             hasDurationMistake, hasVolumeMistake, hasFrequencyMistake);
    }
}
