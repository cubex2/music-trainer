package cubex2.musictrainer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    private Button button;

    private final int sampleRate = 44100;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.play_button);
        button.setOnClickListener(view -> {
            // Use a new tread as this can take a while
            final Thread thread = new Thread(() -> handler.post(this::playSound));
            thread.start();
        });
    }

    void playSound()
    {
        SoundGenerator generator = new SoundGenerator(sampleRate);
        Scale scale = Scale.major(Tone.forKeyNumber(40));
        for (Tone tone : scale.getTones())
        {
            generator.addTone(tone.getFrequency(), 0.5f);
        }

        byte[] sound = generator.generate();

        SoundPlayer player = new SoundPlayer(sound, sampleRate);
        player.play();
    }
}
