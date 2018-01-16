package cubex2.musictrainer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cubex2.musictrainer.config.Settings;
import cubex2.musictrainer.data.*;
import cubex2.musictrainer.stats.StatContract;
import cubex2.musictrainer.stats.StatDbHelper;
import cubex2.musictrainer.stats.StatEntry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizActivity extends AppCompatActivity
{
    private Button btnPlay;
    private Button btnSubmit;
    private ListView listView;

    private Quiz quiz;
    Handler handler = new Handler();

    private SoundPlayer player;

    private boolean[] tonesChecked;
    private boolean submitted = false;
    private Quiz.Report report;

    private int playingSound = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        quiz = createQuiz();
        tonesChecked = new boolean[quiz.getNumTones()];

        player = new SoundPlayer(this, quiz.getTones());
        player.setOnLoadCompleteListener(() -> btnPlay.setEnabled(true));
        player.setOnPlayCompleteListener(() -> handler.post(() -> {
            btnPlay.setEnabled(true);
            btnSubmit.setEnabled(true);
        }));
        player.setToneListener(new SoundPlayer.ToneListener()
        {
            @Override
            public void toneStarted(int toneIndex)
            {
                playingSound = toneIndex;
                handler.post(listView::invalidateViews);
            }

            @Override
            public void toneStopped(int toneIndex)
            {
                if (toneIndex == quiz.getNumTones() - 1)
                {
                    playingSound = -1;
                    handler.post(listView::invalidateViews);
                }
            }
        });

        btnPlay = (Button) findViewById(R.id.play_button);
        btnPlay.setOnClickListener(view -> {
            // Use a new tread as this can take a while
            btnPlay.setEnabled(false);
            btnSubmit.setEnabled(false);
            final Thread thread = new Thread(() -> handler.post(this::playSound));
            thread.start();
        });
        btnPlay.setEnabled(false);

        btnSubmit = (Button) findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(view -> submit());

        listView = (ListView) findViewById(R.id.quiz_list_view);
        listView.setAdapter(new ListAdapter());
    }

    private Quiz createQuiz()
    {
        Difficulty difficulty = Settings.useDynamicDifficulty(this) ? new DifficultyDynamic(this)
                                                                    : new DifficultyFromSettings(this);

        int minTone = Settings.getMinimumStartToneKey(this);
        int maxTone = Settings.getMaximumStartToneKey(this);

        return Quiz.fromDifficulty(difficulty, minTone, maxTone);
    }

    private void submit()
    {
        Set<Integer> selectedTones = new HashSet<>();
        for (int i = 0; i < tonesChecked.length; i++)
        {
            if (tonesChecked[i])
            {
                selectedTones.add(i);
            }
        }

        submitted = true;
        btnSubmit.setText(R.string.next);
        btnSubmit.setOnClickListener(v -> nextQuiz(report));
        report = quiz.createReport(selectedTones);
        listView.invalidateViews();
    }

    private void updateStats(StatDbHelper helper, Quiz.Report report)
    {
        ContentValues values = new ContentValues();
        values.put(StatContract.StatEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
        values.put(StatContract.StatEntry.COLUMN_NAME_CORRECT, !report.hasMistakes());

        values.put(StatContract.StatEntry.COLUMN_NAME_DURATION_ERROR, quiz.difficulty.getDurationError());
        values.put(StatContract.StatEntry.COLUMN_NAME_VOLUME_ERROR, quiz.difficulty.getVolumeError());
        values.put(StatContract.StatEntry.COLUMN_NAME_FREQUENCY_ERROR, 0);

        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_ERROR, report.hasError(ErrorType.DURATION));
        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_ERROR, report.hasError(ErrorType.VOLUME));
        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_FREQUENCY_ERROR, false);

        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_MISTAKE, report.hasMistake(ErrorType.DURATION));
        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_MISTAKE, report.hasMistake(ErrorType.VOLUME));
        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_FREQUENCY_MISTAKE, false);

        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert(StatContract.StatEntry.TABLE_NAME, null, values);
    }

    private void updateDynamicDifficulty(StatDbHelper helper)
    {
        SQLiteDatabase db = helper.getReadableDatabase();

        List<StatEntry> entries;

        float currentDuration = Settings.getDynamicDurationError(this);
        entries = entriesForErrorType(helper, db, ErrorType.DURATION, currentDuration);
        float newDuration = DynamicDifficultyHelper.computeNewDurationError(currentDuration, entries);

        float currentVolume = Settings.getDynamicVolumeError(this);
        entries = entriesForErrorType(helper, db, ErrorType.VOLUME, currentVolume);
        float newVolume = DynamicDifficultyHelper.computeNewVolumeError(currentVolume, entries);

        Settings.setDynamicErrorValues(this, newDuration, newVolume);
    }

    private List<StatEntry> entriesForErrorType(StatDbHelper helper, SQLiteDatabase db, ErrorType errorType, float current)
    {
        return helper.readEntriesWithError(db, errorType, current, 10);
    }

    private void nextQuiz(Quiz.Report report)
    {
        StatDbHelper helper = new StatDbHelper(this);
        updateStats(helper, report);
        updateDynamicDifficulty(helper);
        helper.close();

        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        player.release();
    }

    void playSound()
    {
        player.play(this);
    }

    private class ListAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return quiz.getNumTones();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            LayoutInflater inflater = getLayoutInflater();
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.quiz_row, null, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            CheckBox checkBox = holder.getCheckBox();
            checkBox.setClickable(!submitted);
            checkBox.setText(quiz.getTones()[position].getTone().getName());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> tonesChecked[position] = isChecked);
            checkBox.setChecked(tonesChecked[position]);

            TextView textView = holder.getTextView();
            textView.setText("");

            if (playingSound == position)
            {
                convertView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.quizPlaying, null));
            } else if (report != null)
            {
                if (report.errors.contains(position))
                {
                    convertView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.quizRed, null));

                    if (!report.allErrors.containsKey(position))
                    {
                        textView.setText(R.string.correct_tone);
                    }
                } else
                {
                    convertView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.quizGreen, null));
                }

                if (report.allErrors.containsKey(position))
                {
                    ErrorType type = report.allErrors.get(position);
                    textView.setText(type.displayName);
                }
            } else
            {
                convertView.setBackgroundColor(android.R.color.white);
            }

            return convertView;
        }
    }

    private class ViewHolder
    {
        private View row;
        private CheckBox checkBox;
        private TextView textView;

        public ViewHolder(View row)
        {
            this.row = row;
        }

        public CheckBox getCheckBox()
        {
            if (checkBox == null)
            {
                checkBox = row.findViewById(R.id.checkBox);
            }
            return checkBox;
        }

        public TextView getTextView()
        {
            if (textView == null)
            {
                textView = row.findViewById(R.id.textView);
            }
            return textView;
        }
    }
}
