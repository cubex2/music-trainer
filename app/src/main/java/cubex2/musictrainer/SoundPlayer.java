package cubex2.musictrainer;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import cubex2.musictrainer.data.Quiz;
import cubex2.musictrainer.data.Tone;

import java.util.Timer;
import java.util.TimerTask;

public class SoundPlayer implements SoundPool.OnLoadCompleteListener
{
    private SoundPool soundPool;
    private int[] soundIDs;
    private int[] durations;
    private OnLoadCompleteListener loadListener;
    private OnPlayCompleteListener playListener;
    private Timer timer = new Timer();

    private int numLoaded = 0;

    public SoundPlayer(Context context, Quiz quiz)
    {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);

        soundIDs = new int[quiz.getNumTones()];
        durations = new int[quiz.getNumTones()];
        for (int i = 0; i < soundIDs.length; i++)
        {
            Tone tone = quiz.getTone(i);
            soundIDs[i] = soundPool.load(context, tone.getResourceId(context), 1);
            durations[i] = quiz.getToneDuration(i);
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

    public void play()
    {
        int delay = 0;
        for (int i = 0; i < soundIDs.length; i++)
        {
            final int j = i;
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    soundPool.play(soundIDs[j], 1f, 1f, 1, 0, 1f);
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
