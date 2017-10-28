package cubex2.musictrainer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import cubex2.musictrainer.config.Settings;
import cubex2.musictrainer.data.*;
import cubex2.musictrainer.stats.StatContract;
import cubex2.musictrainer.stats.StatDbHelper;

import java.util.HashSet;
import java.util.Set;

public class QuizActivity extends AppCompatActivity implements SoundPlayer.OnFinishListener
{
    private Button btnPlay;
    private Button btnSubmit;
    private GridView gridView;

    private SoundPlayer player;
    private final int sampleRate = 44100;

    private Quiz quiz;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quiz = createQuiz();

        player = createSoundPlayer();

        btnPlay = (Button) findViewById(R.id.play_button);
        btnPlay.setOnClickListener(view -> {
            // Use a new tread as this can take a while
            btnPlay.setEnabled(false);
            final Thread thread = new Thread(() -> handler.post(this::playSound));
            thread.start();
        });

        btnSubmit = (Button) findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(view -> submit());

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new BaseAdapter()
        {

            @Override
            public int getCount()
            {
                return quiz.getNumTones();
            }

            @Override
            public Object getItem(int i)
            {
                return null;
            }

            @Override
            public long getItemId(int i)
            {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup)
            {
                CheckBox cb;
                if (view == null)
                {
                    cb = new CheckBox(QuizActivity.this);
                    cb.setPadding(8, 8, 8, 8);
                } else
                {
                    cb = (CheckBox) view;
                }

                cb.setText(String.valueOf(i + 1));

                return cb;
            }
        });
    }

    private Quiz createQuiz()
    {
        ToneSequence sequence;
        int maxErrors = Settings.getMaxErrors(this);
        boolean scales = Settings.useScales(this);
        boolean arpeggios = Settings.useArpeggios(this);
        boolean durationErrors = Settings.useDurationErrors(this);
        boolean frequencyErrors = Settings.useFrequencyErrors(this);

        int minTone = Settings.getMinimumStartToneKey(this);
        int maxTone = Settings.getMaximumStartToneKey(this);
        int startTone = Util.randomInRange(minTone, maxTone);

        if (scales && (!arpeggios || Util.randomBoolean()))
        {
            sequence = Scale.major(Tone.forKeyNumber(startTone));
        } else
        {
            sequence = Arpeggio.major(Tone.forKeyNumber(startTone));
        }

        return new Quiz(sequence, maxErrors, durationErrors, frequencyErrors);
    }

    private SoundPlayer createSoundPlayer()
    {
        SoundGenerator generator = new SoundGenerator(sampleRate);
        quiz.addTones(generator);

        byte[] sound = generator.generate();

        SoundPlayer player = new SoundPlayer(sound, sampleRate);
        player.addOnFinishListener(this);

        return player;
    }

    private void submit()
    {
        Set<Integer> selectedTones = new HashSet<>();
        for (int i = 0; i < quiz.getNumTones(); i++)
        {
            CheckBox cb = (CheckBox) gridView.getChildAt(i);
            if (cb.isChecked())
            {
                selectedTones.add(i);
            }
        }

        AlertDialog dialog = createDialog(quiz.createReport(selectedTones));
        dialog.show();
    }

    private AlertDialog createDialog(Quiz.Report report)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (!report.hasMistakes())
        {
            builder.setMessage(R.string.quiz_correct);
        } else
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < report.allErrors.length; i++)
            {
                if (i > 0)
                    sb.append(", ");

                sb.append(report.allErrors[i] + 1);
            }

            builder.setMessage(getResources().getString(R.string.quiz_incorrect, sb.toString()));
        }

        builder.setPositiveButton(R.string.button_ok, (dialogInterface, i) -> nextQuiz());

        return builder.create();
    }

    private void updateStats()
    {
        StatDbHelper helper = new StatDbHelper(this);

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StatContract.StatEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());

        db.insert(StatContract.StatEntry.TABEL_NAME, null, values);

        helper.close();
    }

    private void nextQuiz()
    {
        updateStats();

        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
        finish();
    }

    void playSound()
    {
        player.play();
    }

    @Override
    public void finished(SoundPlayer player)
    {
        btnPlay.setEnabled(true);
        btnSubmit.setEnabled(true);
    }
}
