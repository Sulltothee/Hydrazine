package Utility;

import java.text.DecimalFormat;

public class MiscFunctions {
    public static int incrementIndex(Object[] array, int index) {
        ++index;
        if (index >= array.length) {
            index = 0;
        }

        return index;
    }

    public static int incrementIndex(Object[] array, int index, int increments) {
        for(int i = 0; i < increments; ++i) {
            index = incrementIndex(array, index);
        }

        return index;
    }

    public static int deIncrementIndex(Object[] array, int index) {
        --index;
        if (index < 0) {
            index = array.length - 1;
        }

        return index;
    }

    public static int deIncrementIndex(Object[] array, int index, int increments) {
        for(int i = 0; i < increments; ++i) {
            index = deIncrementIndex(array, index);
        }

        return index;
    }

    public static float RoundToNDecimalPlaces(float input, int N) {
        DecimalFormat df = new DecimalFormat("#." + "#".repeat(Math.max(0, N)));
        return Float.parseFloat(df.format((double)input));
    }
}
