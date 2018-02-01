package cubex2.musictrainer.data;

import java.util.List;

public interface Difficulty
{
    int getNumTones();

    int getMaxErrors();

    boolean useScales();

    boolean useArpeggios();

    int getToneDurationIndex();

    float getToneDuration();

    List<ErrorType> getErrorTypes();

    int getDurationErrorIndex();

    float getDurationError();

    int getVolumeErrorIndex();

    float getVolumeError();
}
