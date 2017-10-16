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
        generator.addTone(261, 0.5f);
        generator.addTone(293, 0.5f);
        generator.addTone(329, 0.5f);
        generator.addTone(349, 0.5f);
        generator.addTone(391, 0.5f);
        generator.addTone(440, 0.5f);
        generator.addTone(493, 0.5f);
        generator.addTone(523, 0.5f);

        byte[] sound = generator.generate();

        SoundPlayer player = new SoundPlayer(sound, sampleRate);
        player.play();
    }
}
