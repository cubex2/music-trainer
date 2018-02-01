package cubex2.musictrainer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import cubex2.musictrainer.data.DynamicDifficultyHelper;
import cubex2.musictrainer.stats.StatContract;
import cubex2.musictrainer.stats.StatDbHelper;
import cubex2.musictrainer.stats.StatEntry;

import java.util.List;

public class StatsActivity extends AppCompatActivity implements DialogInterface.OnClickListener
{
    private TextView tvStatCount;
    private TextView tvCorrectCount;
    private Button btnReset;
    private StatDbHelper dbHelper;
    private GraphView graph;

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

        btnReset = (Button) findViewById(R.id.reset_button);

        graph = (GraphView) findViewById(R.id.graph);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);

        graph.getViewport().setScrollable(true);

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
            graph.removeAllSeries();
            addSeriesToGraph(new DataPoint[0], getString(R.string.legend_duration), 0xff0077cc);
            addSeriesToGraph(new DataPoint[0], getString(R.string.legend_volume), Color.RED);
            graph.invalidate();
            btnReset.setEnabled(true);
        }
    }

    private void addSeriesToGraph(DataPoint[] points, String title, int color)
    {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
        series.setColor(color);
        series.setTitle(title);
        graph.addSeries(series);
    }

    private class ReadDbTask extends AsyncTask<Void, Integer, List<StatEntry>>
    {
        @Override
        protected List<StatEntry> doInBackground(Void... voids)
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            return dbHelper.readEntries(db, null, null, Integer.MAX_VALUE, "ASC");
        }

        @Override
        protected void onPostExecute(List<StatEntry> statEntries)
        {
            int finished = statEntries.size();
            int correct = getCorrectCount(statEntries);

            int percentCorrect = (int) (correct / (float) finished * 100);

            tvStatCount.setText(getString(R.string.stat_count, String.valueOf(finished)));
            tvCorrectCount.setText(getString(R.string.correct_count, correct + " (" + percentCorrect + "%)"));

            DataPoint[] points = new DataPoint[statEntries.size()];
            for (int i = 0; i < points.length; i++)
            {
                StatEntry entry = statEntries.get(i);
                float value = DynamicDifficultyHelper.normalizeDurationError(entry.getDurationError()) * 100f;
                points[i] = new DataPoint(i, value);
            }
            addSeriesToGraph(points, getString(R.string.legend_duration), 0xff0077cc);

            points = new DataPoint[statEntries.size()];
            for (int i = 0; i < points.length; i++)
            {
                StatEntry entry = statEntries.get(i);
                float value = DynamicDifficultyHelper.normalizeVolumeError(entry.getVolumeError()) * 100f;
                points[i] = new DataPoint(i, value);
            }
            addSeriesToGraph(points, getString(R.string.legend_volume), Color.RED);

            if (statEntries.size() > graph.getViewport().getMaxX(false))
            {
                graph.getViewport().scrollToEnd();
            }

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
