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
import cubex2.musictrainer.data.Difficulty;
import cubex2.musictrainer.data.ErrorType;
import cubex2.musictrainer.data.Quiz;
import cubex2.musictrainer.stats.StatContract;
import cubex2.musictrainer.stats.StatDbHelper;

import java.util.HashSet;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        quiz = createQuiz(Difficulty.fromSettings(this));
        tonesChecked = new boolean[quiz.getNumTones()];

        player = new SoundPlayer(this, quiz.getTones());
        player.setOnLoadCompleteListener(() -> btnPlay.setEnabled(true));
        player.setOnPlayCompleteListener(() -> handler.post(() -> {
            btnPlay.setEnabled(true);
            btnSubmit.setEnabled(true);
        }));

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

    private Quiz createQuiz(Difficulty difficulty)
    {
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

    private void updateStats(Quiz.Report report)
    {
        StatDbHelper helper = new StatDbHelper(this);

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StatContract.StatEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
        values.put(StatContract.StatEntry.COLUMN_NAME_CORRECT, !report.hasMistakes());

        db.insert(StatContract.StatEntry.TABEL_NAME, null, values);

        helper.close();
    }

    private void nextQuiz(Quiz.Report report)
    {
        updateStats(report);

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

            if (report != null)
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
