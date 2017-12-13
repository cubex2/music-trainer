package cubex2.musictrainer.stats;

import android.provider.BaseColumns;

public final class StatContract
{
    private StatContract() { }

    public static class StatEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "stat";
        public static final String COLUMN_NAME_TIMESTAMP = "time";
        public static final String COLUMN_NAME_CORRECT = "correct";

        public static final String COLUMN_NAME_DURATION_ERROR = "duration_error";
        public static final String COLUMN_NAME_VOLUME_ERROR = "volume_error";
        public static final String COLUMN_NAME_FREQUENCY_ERROR = "frequency_error";

        public static final String COLUMN_NAME_HAS_DURATION_ERROR = "has_duration_error";
        public static final String COLUMN_NAME_HAS_VOLUME_ERROR = "has_volume_error";
        public static final String COLUMN_NAME_HAS_FREQUENCY_ERROR = "has_frequency_error";

        public static final String COLUMN_NAME_HAS_DURATION_MISTAKE = "has_duration_mistake";
        public static final String COLUMN_NAME_HAS_VOLUME_MISTAKE = "has_volume_mistake";
        public static final String COLUMN_NAME_HAS_FREQUENCY_MISTAKE = "has_frequency_mistake";
    }
}
