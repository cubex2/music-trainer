package cubex2.musictrainer;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import cubex2.musictrainer.data.PlayableTone;
import cubex2.musictrainer.data.Tone;

import java.util.Timer;
import java.util.TimerTask;

public class SoundPlayer implements SoundPool.OnLoadCompleteListener
{
    private SoundPool soundPool;
    private int[] soundIDs;
    private int[] durations;
    private float[] volumes;
    private OnLoadCompleteListener loadListener;
    private OnPlayCompleteListener playListener;
    private Timer timer = new Timer();

    private int numLoaded = 0;

    public SoundPlayer(Context context, PlayableTone[] tones)
    {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);

        soundIDs = new int[tones.length];
        durations = new int[tones.length];
        volumes = new float[tones.length];
        for (int i = 0; i < soundIDs.length; i++)
        {
            Tone tone = tones[i].getTone();
            float duration = tones[i].getDuration();
            float volume = tones[i].getVolume();

            soundIDs[i] = soundPool.load(context, tone.getResourceId(context), 1);
            durations[i] = (int) (duration * 1000);
            volumes[i] = volume;
        }
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener loadListener)
    {
        this.loadListener = loadListener;
    }

    public void setOnPlayCompleteListener(OnPlayCompleteListener playListener)
    {
        this.playListener = playListener;
    }

    public void play(Context context)
    {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;

        int delay = 0;
        for (int i = 0; i < soundIDs.length; i++)
        {
            final int j = i;
            final float toneVolume = volume * volumes[i];

            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    soundPool.play(soundIDs[j], toneVolume, toneVolume, 1, 0, 1f);
                }
            }, delay);
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    soundPool.stop(soundIDs[j]);
                }
            }, delay + durations[i] + 50);

            delay += durations[i];
        }

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (playListener != null)
                {
                    playListener.playbackComplete();
                }
            }
        }, delay + 50);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
    {
        numLoaded++;
        if (numLoaded == soundIDs.length)
        {
            if (loadListener != null)
            {
                loadListener.onLoadComplete();
            }
        }
    }

    public interface OnLoadCompleteListener
    {
        void onLoadComplete();
    }

    public interface OnPlayCompleteListener
    {
        void playbackComplete();
    }
}
