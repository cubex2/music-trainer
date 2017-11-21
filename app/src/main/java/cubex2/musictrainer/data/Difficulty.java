package cubex2.musictrainer.data;

import java.util.List;

public interface Difficulty
{
    int getNumTones();

    int getMaxErrors();

    boolean useScales();

    boolean useArpeggios();

    List<ErrorType> getErrorTypes();

    List<Float> getDurationErrors();
}
