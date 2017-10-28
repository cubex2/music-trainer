package cubex2.musictrainer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import cubex2.musictrainer.stats.StatContract;
import cubex2.musictrainer.stats.StatDbHelper;
import cubex2.musictrainer.stats.StatEntry;

import java.util.LinkedList;
import java.util.List;

public class StatsActivity extends AppCompatActivity
{
    private TextView tvStatCount;
    private StatDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        dbHelper = new StatDbHelper(this);

        tvStatCount = (TextView) findViewById(R.id.count_tv);
        tvStatCount.setText(getString(R.string.stat_count, "Loading..."));

        new ReadDbTask().execute();
    }

    private class ReadDbTask extends AsyncTask<Void, Integer, List<StatEntry>>
    {
        @Override
        protected List<StatEntry> doInBackground(Void... voids)
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    StatContract.StatEntry.COLUMN_NAME_TIMESTAMP
            };

            String sortOrder = StatContract.StatEntry.COLUMN_NAME_TIMESTAMP + " DESC";

            Cursor cursor = db.query(
                    StatContract.StatEntry.TABEL_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder);

            List<StatEntry> entries = new LinkedList<>();
            while (cursor.moveToNext())
            {
                long timeStamp = cursor.getLong(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_TIMESTAMP));

                entries.add(new StatEntry(timeStamp));
            }

            cursor.close();

            return entries;
        }

        @Override
        protected void onPostExecute(List<StatEntry> statEntries)
        {
            tvStatCount.setText(getString(R.string.stat_count, String.valueOf(statEntries.size())));
        }
    }
}
