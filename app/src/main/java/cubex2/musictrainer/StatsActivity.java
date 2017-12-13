package cubex2.musictrainer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cubex2.musictrainer.config.Settings;
import cubex2.musictrainer.stats.StatContract;
import cubex2.musictrainer.stats.StatDbHelper;
import cubex2.musictrainer.stats.StatEntry;

import java.util.List;

public class StatsActivity extends AppCompatActivity implements DialogInterface.OnClickListener
{
    private TextView tvStatCount;
    private TextView tvCorrectCount;
    private TextView tvDuration;
    private Button btnReset;
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

        tvDuration = (TextView) findViewById(R.id.duration_tv);
        tvDuration.setText(getString(R.string.correct_count, loadingString));

        btnReset = (Button) findViewById(R.id.reset_button);

        new ReadDbTask().execute();
    }


    public void onResetClicked(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.yes, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        builder.setMessage(R.string.message_reset_stats);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetStats()
    {
        new ClearDbTask().execute();
        btnReset.setEnabled(false);
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        if (which == AlertDialog.BUTTON_POSITIVE)
        {
            resetStats();
        }
    }

    private class ClearDbTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            db.delete(StatContract.StatEntry.TABLE_NAME,
                      null,
                      null);

            db.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            tvStatCount.setText(getString(R.string.stat_count, String.valueOf(0)));
            tvCorrectCount.setText(getString(R.string.correct_count, String.valueOf(0)));
            btnReset.setEnabled(true);
        }
    }

    private class ReadDbTask extends AsyncTask<Void, Integer, List<StatEntry>>
    {
        @Override
        protected List<StatEntry> doInBackground(Void... voids)
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            return dbHelper.readEntries(db, Integer.MAX_VALUE);
        }

        @Override
        protected void onPostExecute(List<StatEntry> statEntries)
        {
            int finished = statEntries.size();
            int correct = getCorrectCount(statEntries);

            int percentCorrect = (int) (correct / (float) finished * 100);

            tvStatCount.setText(getString(R.string.stat_count, String.valueOf(finished)));
            tvCorrectCount.setText(getString(R.string.correct_count, correct + " (" + percentCorrect + "%)"));
            tvDuration.setText(getString(R.string.duration_diff, String.format(getResources().getConfiguration().locale, "%.3f", Settings.getDynamicDurationError(StatsActivity.this))));

            btnReset.setEnabled(true);
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
