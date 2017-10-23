package cubex2.musictrainer;

import java.util.Arrays;
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

}
