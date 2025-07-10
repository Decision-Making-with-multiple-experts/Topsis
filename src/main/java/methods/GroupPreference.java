package methods;

import models.Problem;
import models.Rating;
import models.Criterion;

import java.util.*;

public class GroupPreference {

    private static final Map<String, Integer> estimates = new HashMap<>();
    private static final Map<String, Integer> estimatesMin = new HashMap<>();

    static {
        estimates.put("ЭВ", 9); estimates.put("ОВ", 9); estimates.put("В", 7);
        estimates.put("С", 5);  estimates.put("Н", 3);  estimates.put("ОН", 1); estimates.put("ЭН", 1);

        estimatesMin.put("ЭВ", 1); estimatesMin.put("ОВ", 1); estimatesMin.put("В", 3);
        estimatesMin.put("С", 5); estimatesMin.put("Н", 7); estimatesMin.put("ОН", 9); estimatesMin.put("ЭН", 9);
    }

    public static List<Map.Entry<String, Double>> run(Problem problem, List<Rating> ratings) {
        System.out.println("\n=== Расчет приоритетов на основе групповых предпочтений (строгие предпочтения) ===");

        if (problem == null || ratings == null || ratings.isEmpty()) {
            System.out.println("Недостаточно данных для расчета.");
            return Collections.emptyList();
        }

        List<Criterion> criteria = problem.getCriteria();
        Set<String> altNamesSet = new LinkedHashSet<>();
        Map<Integer, Map<String, List<Double>>> expertRatings = new HashMap<>();

        for (Rating rating : ratings) {
            if (!rating.getProblemName().equals(problem.getProblemName())) continue;

            altNamesSet.add(rating.getAlternativeName());
            List<String> rawValues = rating.getAlternativeRatings();
            List<Double> numericValues = new ArrayList<>();

            for (int i = 0; i < rawValues.size(); i++) {
                String val = rawValues.get(i);
                String direction = criteria.get(i).getOptimizationDirection();

                if (direction.equalsIgnoreCase("max")) {
                    numericValues.add(estimates.containsKey(val) ? estimates.get(val).doubleValue() : parseOrZero(val));
                } else {
                    numericValues.add(estimatesMin.containsKey(val) ? estimatesMin.get(val).doubleValue() : (10 - parseOrZero(val)));
                }
            }

            expertRatings
                    .computeIfAbsent(rating.getExpertId(), k -> new HashMap<>())
                    .put(rating.getAlternativeName(), numericValues);
        }

        List<String> alternatives = new ArrayList<>(altNamesSet);
        int n = alternatives.size();

        // матрица групповых предпочтений
        int[][] groupMatrix = new int[n][n];

        for (Map<String, List<Double>> expertData : expertRatings.values()) {
            Map<String, Double> avgRatings = new HashMap<>();

            for (String alt : expertData.keySet()) {
                List<Double> vals = expertData.get(alt);
                avgRatings.put(alt, vals.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            }

            for (int i = 0; i < n; i++) {
                String ai = alternatives.get(i);
                for (int j = 0; j < n; j++) {
                    if (i == j) continue;
                    String aj = alternatives.get(j);

                    double vi = avgRatings.getOrDefault(ai, 0.0);
                    double vj = avgRatings.getOrDefault(aj, 0.0);

                    if (vi > vj) {
                        groupMatrix[i][j]++;
                    }
                }
            }
        }

        // Считаем приоритеты по строкам
        int[] a_i = new int[n];
        int total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a_i[i] += groupMatrix[i][j];
            }
            total += a_i[i];
        }

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double priority = total > 0 ? (double) a_i[i] / total : 0.0;
            result.add(new AbstractMap.SimpleEntry<>(alternatives.get(i), priority));
        }

        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (int i = 0; i < result.size(); i++) {
            System.out.printf("%d. %s -> priority: %.3f%n", i + 1, result.get(i).getKey(), result.get(i).getValue());
        }

        return result;
    }

    private static double parseOrZero(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
