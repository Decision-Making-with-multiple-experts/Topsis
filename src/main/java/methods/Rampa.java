package methods;

import models.Problem;
import models.Rating;
import models.Criterion;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Rampa {

    private static final Map<String, Integer> estimates = new HashMap<>();
    private static final Map<String, Integer> estimatesMin = new HashMap<>();

    static {
        estimates.put("ЭВ", 9); estimates.put("ОВ", 9); estimates.put("В", 7);
        estimates.put("С", 5);  estimates.put("Н", 3);  estimates.put("ОН", 1); estimates.put("ЭН", 1);

        estimatesMin.put("ЭВ", 1); estimatesMin.put("ОВ", 1); estimatesMin.put("В", 3);
        estimatesMin.put("С", 5); estimatesMin.put("Н", 7); estimatesMin.put("ОН", 9); estimatesMin.put("ЭН", 9);
    }

    public static List<Map.Entry<String, Double>> run(Problem problem, List<Rating> ratings) {
        System.out.println("\n=== Расчет приоритетов методом РАМПА ===");

        if (problem == null || ratings == null || ratings.isEmpty()) {
            System.out.println("Недостаточно данных для расчета.");
            return Collections.emptyList();
        }

        List<String> alternativeNames = ratings.stream()
                .map(Rating::getAlternativeName)
                .distinct()
                .collect(Collectors.toList());

        int numAlternatives = alternativeNames.size();
        int numCriteria = problem.getCriteria().size();

        Map<Integer, List<List<Double>>> expertMatrices = new HashMap<>();

        for (Rating rating : ratings) {
            int expertId = rating.getExpertId();
            List<String> altRatings = rating.getAlternativeRatings();
            List<Double> numeric = new ArrayList<>();

            for (int i = 0; i < altRatings.size(); i++) {
                String val = altRatings.get(i);
                String direction = problem.getCriteria().get(i).getOptimizationDirection();

                if (direction.equalsIgnoreCase("max")) {
                    numeric.add(estimates.containsKey(val) ? estimates.get(val).doubleValue() : parseOrZero(val));
                } else {
                    numeric.add(estimatesMin.containsKey(val) ? estimatesMin.get(val).doubleValue() : (10 - parseOrZero(val)));
                }
            }

            expertMatrices.computeIfAbsent(expertId, k -> initMatrix(numAlternatives, numCriteria));
            int altIndex = alternativeNames.indexOf(rating.getAlternativeName());
            for (int j = 0; j < numCriteria; j++) {
                expertMatrices.get(expertId).get(altIndex).set(j, numeric.get(j));
            }
        }

        double[] totalScores = new double[numAlternatives];

        for (Map.Entry<Integer, List<List<Double>>> expertEntry : expertMatrices.entrySet()) {
            List<List<Double>> matrix = expertEntry.getValue();
            List<List<Double>> transposed = transposeMatrix(matrix);

            double[] expertSums = new double[numAlternatives];

            for (List<Double> criterion : transposed) {
                double[][] compMatrix = calculateComparisonMatrix(criterion);
                double[] sums = sumRows(compMatrix);
                for (int i = 0; i < numAlternatives; i++) {
                    expertSums[i] += sums[i];
                    totalScores[i] += sums[i];
                }
            }
        }

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        for (int i = 0; i < numAlternatives; i++) {
            result.add(new AbstractMap.SimpleEntry<>(alternativeNames.get(i), totalScores[i]));
        }

        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (int i = 0; i < result.size(); i++) {
            System.out.printf("%d. %s -> priority: %.3f%n", i + 1, result.get(i).getKey(), result.get(i).getValue());
        }

        return result;
    }

    private static List<List<Double>> initMatrix(int rows, int cols) {
        List<List<Double>> matrix = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Double> row = new ArrayList<>(Collections.nCopies(cols, 0.0));
            matrix.add(row);
        }
        return matrix;
    }

    private static double parseOrZero(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static List<List<Double>> transposeMatrix(List<List<Double>> matrix) {
        int rows = matrix.size();
        int cols = matrix.get(0).size();
        List<List<Double>> transposed = new ArrayList<>();
        for (int j = 0; j < cols; j++) {
            List<Double> newRow = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                newRow.add(matrix.get(i).get(j));
            }
            transposed.add(newRow);
        }
        return transposed;
    }

    private static double[][] calculateComparisonMatrix(List<Double> criteria) {
        int size = criteria.size();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            double valueI = criteria.get(i);
            for (int j = 0; j < size; j++) {
                double valueJ = criteria.get(j) != 0 ? criteria.get(j) : 1;
                matrix[i][j] = Math.round((valueI / valueJ) * 1000.0) / 1000.0;
            }
        }
        return matrix;
    }

    private static double[] sumRows(double[][] matrix) {
        double[] sums = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0;
            for (double value : matrix[i]) {
                sum += value;
            }
            sums[i] = Math.round(sum * 1000.0) / 1000.0;
        }
        return sums;
    }
}