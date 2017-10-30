package cubex2.musictrainer.stats;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StatDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Stat.db";

    private static final String SQL_CREATE_STATS = "CREATE TABLE " + StatContract.StatEntry.TABEL_NAME + " (" +
                                                   StatContract.StatEntry._ID + " INTEGER PRIMARY KEY," +
                                                   StatContract.StatEntry.COLUMN_NAME_TIMESTAMP + " INTEGER," +
                                                   StatContract.StatEntry.COLUMN_NAME_CORRECT + " INTEGER)";

    private static final String SQL_DELETE_STATS = "DROP TABLE IF EXISTS " + StatContract.StatEntry.TABEL_NAME;

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
}
