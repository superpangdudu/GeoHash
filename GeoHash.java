import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/11/21.
 */
public class GeoHash {
    //===================================================================================
    private static class PrecisionProperty {
        private int latitudeBits;
        private int longitudeBits;
        private double latitudeError;
        private double longitudeError;
        private double KMError;

        public PrecisionProperty(int latitudeBits,
                                 int longitudeBits,
                                 double latitudeError,
                                 double longitudeError,
                                 double KMError) {
            this.latitudeBits = latitudeBits;
            this.longitudeBits = longitudeBits;
            this.latitudeError = latitudeError;
            this.longitudeError = longitudeError;
            this.KMError = KMError;
        }

        public int getLatitudeBits() {
            return latitudeBits;
        }

        public int getLongitudeBits() {
            return longitudeBits;
        }

        public double getLatitudeError() {
            return latitudeError;
        }

        public double getLongitudeError() {
            return longitudeError;
        }

        public double getKMError() {
            return KMError;
        }
    }

    //===================================================================================
    private static final String[] Base32 = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "b", "c", "d", "e", "f", "g", "h", "j", "k", "m",
            "n", "p", "q", "r", "s", "t", "u", "v", "w", "x",
            "y", "z"
    };

    private static final Map<String, String> Base32Map = new HashMap<String, String>() {{
        put("00000", "0");
        put("00001", "1");
        put("00010", "2");
        put("00011", "3");
        put("00100", "4");
        put("00101", "5");
        put("00110", "6");
        put("00111", "7");
        put("01000", "8");
        put("01001", "9");
        put("01010", "b");
        put("01011", "c");
        put("01100", "d");
        put("01101", "e");
        put("01110", "f");
        put("01111", "g");
        put("10000", "h");
        put("10001", "j");
        put("10010", "k");
        put("10011", "m");
        put("10100", "n");
        put("10101", "p");
        put("10110", "q");
        put("10111", "r");
        put("11000", "s");
        put("11001", "t");
        put("11010", "u");
        put("11011", "v");
        put("11100", "w");
        put("11101", "x");
        put("11110", "y");
        put("11111", "z");
    }};

    private static final Map<Integer, PrecisionProperty> PRECISION_PROPERTY_MAP
            = new HashMap<Integer, PrecisionProperty>() {{
        put(1, new PrecisionProperty(2, 3, 23, 23, 2500));
        put(2, new PrecisionProperty(5, 5, 2.8, 5.6, 630));
        put(3, new PrecisionProperty(7, 8, 0.7, 0.7, 78));
        put(4, new PrecisionProperty(10, 10, 0.087, 0.18, 20));
        put(5, new PrecisionProperty(12, 13, 0.022, 0.022, 2.4));
        put(6, new PrecisionProperty(15, 15, 0.0027, 0.0055, 0.61));
        put(7, new PrecisionProperty(17, 18, 0.00068, 0.00068, 0.076));
        put(8, new PrecisionProperty(20, 20, 0.00008, 0.00017, 0.019));
    }};

    private static final double MAX_LONGITUDE = 180.0000000000;
    private static final double MIN_LONGITUDE = -180.0000000000;

    private static final double MAX_LATITUDE = 90.0000000000;
    private static final double MIN_LATITUDE = -90.0000000000;

    private static String encode(double min, double max, double value, int round) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < round; n++) {
            String tmp = "1";

            double average = (min + max) / 2;
            if (value < average) {
                tmp = "0";
                max = average;
            } else
                min = average;

            //
            sb.append(tmp);
        }

        return sb.toString();
    }

    private static String combine(String longitudeCode, String latitudeCode) {
        StringBuilder sb = new StringBuilder();

        for (int n = 0; n < longitudeCode.length(); n++) {
            sb.append(longitudeCode.charAt(n));

            if (n < latitudeCode.length())
                sb.append(latitudeCode.charAt(n));
        }

        return sb.toString();
    }

    //===================================================================================
    public static String getGeoHash(double longitude, double latitude, int length) {
        if (length > PRECISION_PROPERTY_MAP.keySet().size()
                || length < 0)
            return null;

        PrecisionProperty precisionProperty = PRECISION_PROPERTY_MAP.get(length);

        //
        String longitudeCode =
                encode(MIN_LONGITUDE, MAX_LONGITUDE, longitude, precisionProperty.getLongitudeBits());
        String latitudeCode =
                encode(MIN_LATITUDE, MAX_LATITUDE, latitude, precisionProperty.getLatitudeBits());

        String code = combine(longitudeCode, latitudeCode);

        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < code.length(); n += 5) {
            String key = code.substring(n, n + 5);
            String value = Base32Map.get(key);

            sb.append(value);
        }

        return sb.toString();
    }

    //===================================================================================
    public static void main(String[] args) {
        String code = GeoHash.getGeoHash(116.389550, 39.928167, 5);
        System.out.println(code);
    }
}
