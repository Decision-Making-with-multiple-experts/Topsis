package utils;

import java.util.HashMap;
import java.util.Map;

public class ScaleConverter {
    private static final Map<String, Map<String, Double>> scales = new HashMap<>();

    static {
        // SHORT
        Map<String, Double> shortScale = new HashMap<>();
        shortScale.put("Н", 1.0);
        shortScale.put("С", 5.0);
        shortScale.put("В", 9.0);
        scales.put("short", shortScale);

        // BASE
        Map<String, Double> baseScale = new HashMap<>();
        baseScale.put("ОН", 1.0);
        baseScale.put("Н", 3.0);
        baseScale.put("С", 5.0);
        baseScale.put("В", 7.0);
        baseScale.put("ОВ", 9.0);
        scales.put("base", baseScale);

        // LONG
        Map<String, Double> longScale = new HashMap<>();
        longScale.put("ЭН", 1.0);
        longScale.put("ОН", 2.0);
        longScale.put("Н", 3.0);
        longScale.put("С", 5.0);
        longScale.put("В", 7.0);
        longScale.put("ОВ", 8.0);
        longScale.put("ЭВ", 9.0);
        scales.put("long", longScale);

        // NUMERIC
        Map<String, Double> numericScale = new HashMap<>();
        numericScale.put("0", 0.0);
        numericScale.put("1", 1.0);
        numericScale.put("2", 2.0);
        numericScale.put("3", 3.0);
        numericScale.put("4", 4.0);
        numericScale.put("5", 5.0);
        numericScale.put("6", 6.0);
        numericScale.put("7", 7.0);
        numericScale.put("8", 8.0);
        numericScale.put("9", 9.0);
        numericScale.put("10", 10.0);
        scales.put("numeric", numericScale);
    }

    public static double convert(String scaleType, String value) {
        return scales.getOrDefault(scaleType, new HashMap<>()).getOrDefault(value, 0.0);
    }
}
