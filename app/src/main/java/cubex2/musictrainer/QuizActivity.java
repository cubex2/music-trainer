package cubex2.musictrainer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
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
    private TextView tvDuration;
    private SeekBar seekBarDuration;
    private TextView tvVolume;
    private SeekBar seekBarVolume;
    private TextView tvToneDuration;
    private SeekBar seekBarToneDuration;

    private Quiz quiz;
    private Handler handler = new Handler();

    private SoundPlayer player;

    private boolean[] tonesChecked;
    private boolean submitted = false;
    private Quiz.Report report;

    private int playingSound = -1;
    private boolean nextClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        quiz = createQuiz();
        tonesChecked = new boolean[quiz.getNumTones()];

        player = new SoundPlayer(this, quiz.getTones());
        player.setOnLoadCompleteListener(() -> btnPlay.setEnabled(!nextClicked));
        player.setOnPlayCompleteListener(() -> handler.post(() -> {
            btnSubmit.setEnabled(true);
            handler.post(listView::invalidateViews);
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
                playingSound = -1;
                if (toneIndex == quiz.getNumTones() - 1 && !nextClicked)
                {
                    handler.post(() -> btnSubmit.setEnabled(true));
                }
            }
        });

        btnPlay = (Button) findViewById(R.id.play_button);
        btnPlay.setOnClickListener(view -> {
            // Use a new tread as this can take a while
            if (playingSound < 0)
            {
                startPlayBack();
            } else
            {
                stopPlayBack();
            }
        });
        btnPlay.setEnabled(false);

        btnSubmit = (Button) findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(view -> submit());

        listView = (ListView) findViewById(R.id.quiz_list_view);
        listView.setAdapter(new ListAdapter());

        tvDuration = (TextView) findViewById(R.id.duration_tv);
        seekBarDuration = (SeekBar) findViewById(R.id.duration_seekBar);
        seekBarDuration.setEnabled(false);
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tvDuration.setText(getResources().getString(R.string.quiz_duration, ErrorType.DURATION.errorForIndex(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        seekBarDuration.setProgress(quiz.difficulty.getDurationErrorIndex());

        tvVolume = (TextView) findViewById(R.id.volume_tv);
        seekBarVolume = (SeekBar) findViewById(R.id.volume_seekBar);
        seekBarVolume.setEnabled(false);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tvVolume.setText(getResources().getString(R.string.quiz_volume, Math.round(ErrorType.VOLUME.errorForIndex(progress) * 100f)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        seekBarVolume.setProgress(quiz.difficulty.getVolumeErrorIndex());

        tvToneDuration = (TextView) findViewById(R.id.tone_duration_tv);
        seekBarToneDuration = (SeekBar) findViewById(R.id.tone_duration_seekBar);
        seekBarToneDuration.setEnabled(false);
        seekBarToneDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tvToneDuration.setText(getResources().getString(R.string.quiz_tone_duration, Settings.toneDurationForIndex(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        seekBarToneDuration.setProgress(quiz.difficulty.getToneDurationIndex());
    }

    private Quiz createQuiz()
    {
        Difficulty difficulty = Settings.useDynamicDifficulty(this) ? new DifficultyDynamic(this)
                                                                    : new DifficultyFromSettings(this);

        int minTone = Settings.getMinimumStartToneKey(this);
        int maxTone = Settings.getMaximumStartToneKey(this);

        return Quiz.fromDifficulty(difficulty, minTone, maxTone);
    }

    private void startPlayBack()
    {
        btnPlay.setText(R.string.button_play_stop);

        final Thread thread = new Thread(() -> handler.post(() -> player.play(this)));
        thread.start();
    }

    private void stopPlayBack()
    {
        player.stop();
        btnPlay.setText(R.string.button_play);
    }

    private void submit()
    {
        stopPlayBack();

        if (!(quiz.difficulty instanceof DifficultyDynamic))
        {
            tvDuration.setTextColor(0xff000000);
            tvVolume.setTextColor(0xff000000);
            seekBarToneDuration.setEnabled(true);
            seekBarDuration.setEnabled(true);
            seekBarVolume.setEnabled(true);
        }

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

        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_ERROR, report.hasError(ErrorType.DURATION));
        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_ERROR, report.hasError(ErrorType.VOLUME));

        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_DURATION_MISTAKE, report.hasMistake(ErrorType.DURATION));
        values.put(StatContract.StatEntry.COLUMN_NAME_HAS_VOLUME_MISTAKE, report.hasMistake(ErrorType.VOLUME));

        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert(StatContract.StatEntry.TABLE_NAME, null, values);
    }

    private void updateDynamicDifficulty(StatDbHelper helper)
    {
        SQLiteDatabase db = helper.getReadableDatabase();

        List<StatEntry> entries;

        int currentDuration = Settings.getDynamicDurationErrorIndex(this);
        entries = entriesForErrorType(helper, db, ErrorType.DURATION, ErrorType.DURATION.errorForIndex(currentDuration));
        int newDuration = DynamicDifficultyHelper.computeNewDurationError(currentDuration, entries);

        int currentVolume = Settings.getDynamicVolumeErrorIndex(this);
        entries = entriesForErrorType(helper, db, ErrorType.VOLUME, ErrorType.VOLUME.errorForIndex(currentVolume));
        int newVolume = DynamicDifficultyHelper.computeNewVolumeError(currentVolume, entries);

        Settings.setDynamicErrorValues(this, newDuration, newVolume);
    }

    private List<StatEntry> entriesForErrorType(StatDbHelper helper, SQLiteDatabase db, ErrorType errorType, float current)
    {
        return helper.readEntriesWithError(db, errorType, current, 10);
    }

    private void nextQuiz(Quiz.Report report)
    {
        nextClicked = true;
        btnPlay.setEnabled(false);
        btnSubmit.setEnabled(false);
        stopPlayBack();
        player.release();

        if (!(quiz.difficulty instanceof DifficultyDynamic))
        {
            int newDuration = seekBarDuration.getProgress();
            Settings.setDurationError(this, newDuration);

            int newVolume = seekBarVolume.getProgress();
            Settings.setVolumeError(this, newVolume);

            int newToneDuration = seekBarToneDuration.getProgress();
            Settings.setToneDuration(this, newToneDuration);
        }

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
            checkBox.setText(String.valueOf(position + 1));
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
                    Pair<ErrorType, Integer> error = report.allErrors.get(position);
                    ErrorType type = error.first;
                    int sign = error.second;
                    textView.setText(type.getDisplayName(sign));
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
