package cubex2.musictrainer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.LinkedList;
import java.util.List;

public class SoundPlayer implements AudioTrack.OnPlaybackPositionUpdateListener
{
    private final byte[] sound;
    private final AudioTrack audioTrack;
    private final List<OnFinishListener> finishListeners = new LinkedList<>();

    public SoundPlayer(byte[] sound, int sampleRate)
    {
        this.sound = sound;

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT, sound.length,
                                    AudioTrack.MODE_STATIC);
        audioTrack.setNotificationMarkerPosition(sound.length / 2 - (sampleRate / 20));
        audioTrack.setPlaybackPositionUpdateListener(this);
        writeSoundToTrack();
    }

    public void addOnFinishListener(OnFinishListener listener)
    {
        finishListeners.add(listener);
    }

    private void writeSoundToTrack()
    {
        int written = 0;
        while (written < sound.length)
        {
            written = audioTrack.write(sound, written, sound.length - written);
        }
    }

    public void play()
    {
        audioTrack.play();
    }

    public void release()
    {
        audioTrack.release();
    }

    @Override
    public void onMarkerReached(AudioTrack audioTrack)
    {
        audioTrack.stop();
        audioTrack.reloadStaticData();

        for (OnFinishListener listener : finishListeners)
        {
            listener.finished(this);
        }
    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack)
    {

    }

    interface OnFinishListener
    {
        void finished(SoundPlayer player);
    }
}
