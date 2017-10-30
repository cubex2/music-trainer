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
    private TextView tvCorrectCount;
    private StatDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        dbHelper = new StatDbHelper(this);

        String loadingString = getString(R.string.stat_loading);

        tvStatCount = (TextView) findViewById(R.id.count_tv);
        tvStatCount.setText(getString(R.string.stat_count, loadingString));

        tvCorrectCount = (TextView) findViewById(R.id.correct_tv);
        tvCorrectCount.setText(getString(R.string.correct_count, loadingString));

        new ReadDbTask().execute();
    }

    private class ReadDbTask extends AsyncTask<Void, Integer, List<StatEntry>>
    {
        @Override
        protected List<StatEntry> doInBackground(Void... voids)
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    StatContract.StatEntry.COLUMN_NAME_TIMESTAMP,
                    StatContract.StatEntry.COLUMN_NAME_CORRECT
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
                entries.add(readEntry(cursor));
            }

            cursor.close();

            return entries;
        }

        private StatEntry readEntry(Cursor cursor)
        {
            long timeStamp = cursor.getLong(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_TIMESTAMP));
            boolean wasCorrect = cursor.getInt(cursor.getColumnIndex(StatContract.StatEntry.COLUMN_NAME_CORRECT)) == 1;

            return new StatEntry(timeStamp, wasCorrect);
        }

        @Override
        protected void onPostExecute(List<StatEntry> statEntries)
        {
            int finished = statEntries.size();
            int correct = getCorrectCount(statEntries);

            int percentCorrect = (int) (correct / (float) finished * 100);

            tvStatCount.setText(getString(R.string.stat_count, String.valueOf(finished)));
            tvCorrectCount.setText(getString(R.string.correct_count, correct + " (" + percentCorrect + "%)"));
        }

        private int getCorrectCount(List<StatEntry> statEntries)
        {
            int count = 0;

            for (StatEntry entry : statEntries)
            {
                if (entry.isWasCorrect())
                    count++;
            }

            return count;
        }
    }
}
