package cubex2.musictrainer.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class ErrorTypeTest
{

    @Test
    public void errorForIndex()
    {
        assertEquals(0.05f,ErrorType.DURATION.errorForIndex(7),0.0001f);
        assertEquals(0.40f,ErrorType.DURATION.errorForIndex(0),0.0001f);

        assertEquals(0.15f,ErrorType.VOLUME.errorForIndex(7),0.0001f);
        assertEquals(0.50f,ErrorType.VOLUME.errorForIndex(0),0.0001f);
    }
}