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
    private ToneListener toneListener;
    private Timer timer = new Timer();

    private int numLoaded = 0;
    private int lastStreamId = -1;
    private int lastSoundId = -1;

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

    public void setToneListener(ToneListener toneListener)
    {
        this.toneListener = toneListener;
    }

    public void play(Context context)
    {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;

        playTone(0, volume);
    }

    private void playTone(int index, float volume)
    {
        float toneVolume = volume * volumes[index];
        int streamId = soundPool.play(soundIDs[index], toneVolume, toneVolume, 1, 0, 1f);
        lastStreamId = streamId;
        lastSoundId = index;
        if (toneListener != null)
            toneListener.toneStarted(index);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (lastSoundId == -1)
                    return;

                soundPool.stop(streamId);
                if (toneListener != null)
                    toneListener.toneStopped(index);

                playTone((index + 1) % soundIDs.length, volume);
            }
        }, durations[index]);
    }

    public void stop()
    {
        if (lastSoundId >= 0)
        {
            soundPool.stop(lastStreamId);
            if (toneListener != null)
                toneListener.toneStopped(lastSoundId);
            lastStreamId = -1;
            lastSoundId = -1;
            if (playListener != null)
                playListener.playbackComplete();
        }
    }

    public void release()
    {
        for (int soundID : soundIDs)
        {
            soundPool.unload(soundID);
        }

        soundPool.release();
        soundPool = null;
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

    public interface ToneListener
    {
        void toneStarted(int toneIndex);

        void toneStopped(int toneIndex);
    }
}
