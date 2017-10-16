package cubex2.musictrainer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SoundPlayer implements AudioTrack.OnPlaybackPositionUpdateListener
{
    private final byte[] sound;
    private final AudioTrack audioTrack;

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
    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack)
    {

    }
}
