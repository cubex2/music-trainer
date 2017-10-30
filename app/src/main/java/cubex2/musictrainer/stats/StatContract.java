package cubex2.musictrainer.stats;

import android.provider.BaseColumns;

public final class StatContract
{
    private StatContract() { }

    public static class StatEntry implements BaseColumns
    {
        public static final String TABEL_NAME = "stat";
        public static final String COLUMN_NAME_TIMESTAMP = "time";
        public static final String COLUMN_NAME_CORRECT = "correct";
    }
}
