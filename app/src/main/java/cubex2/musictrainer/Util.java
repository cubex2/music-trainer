package cubex2.musictrainer;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Util
{
    public static final Random RANDOM = new Random();

    public static float randomInRange(float min, float max)
    {
        return RANDOM.nextFloat() * (max - min) + min;
    }

    public static int randomInRange(int min, int max)
    {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static boolean randomBoolean()
    {
        return RANDOM.nextBoolean();
    }

    public static int randomSign()
    {
        return RANDOM.nextBoolean() ? 1 : -1;
    }

    public static <T> T randomElement(List<T> list)
    {
        int index = randomInRange(0, list.size() - 1);

        return list.get(index);
    }

    public static int[] toSortedArray(Set<Integer> set)
    {
        int[] result = new int[set.size()];

        int i = 0;
        for (Integer integer : set)
        {
            result[i++] = integer;
        }

        Arrays.sort(result);

        return result;
    }

    public static String join(Iterable<String> values, String delimiter)
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String value : values)
        {
            if (i > 0)
                sb.append(delimiter);
            sb.append(value);
            i++;
        }
        return sb.toString();
    }

    public static void closeQuietly(@Nullable Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            } catch (IOException ignore)
            {

            }
        }
    }

    public static float[] getFloatArray(Context context, int id)
    {
        String[] strings = context.getResources().getStringArray(id);

        float[] floats = new float[strings.length];
        for (int i = 0; i < strings.length; i++)
        {
            floats[i] = Float.parseFloat(strings[i]);
        }
        return floats;
    }

}
