package methods;

import models.NormalizedRating;
import models.Problem;
import models.Rating;
import utils.RatingNormalizer;

import java.util.*;
import java.util.stream.Collectors;

public class Topsis {
    public static List<Map.Entry<String, Double>> run(Problem problem, List<Rating> ratings) {
        List<Map.Entry<String, Double>> result = new ArrayList<>();
        String ANSI_BLUE = "\033[34m";
        String ANSI_GREEN = "\033[32m";
        String ANSI_RESET = "\033[0m";
        System.out.println(ANSI_BLUE + "\n=== Processing problem: " + problem.getProblemName() + " ===" + ANSI_RESET);

        // Проверка, есть ли в проблеме критерии
        if (problem.getCriteria() == null || problem.getCriteria().isEmpty()) {
            System.err.println("Error: The problem is missing criteria. Unable to perform analysis.");
            return result;
        }

        // Нормализация оценок
        System.out.println(ANSI_GREEN + "\nNormalized rating" + ANSI_RESET);
        List<NormalizedRating> normalizedRatings = normalize(ratings, problem);

        // Проверка на пустые рейтинги
        if (normalizedRatings == null || normalizedRatings.isEmpty()) {
            System.err.println("Error: Ratings list is empty. Unable to perform analysis.");
            return result;
        }

        // Усреднение оценок
        System.out.println(ANSI_GREEN + "\nAveraged ratings" + ANSI_RESET);
        Map<String, double[]> averagedRatings = groupAndAverageRatings(normalizedRatings);

        // Вычисление идеальной и антиидеальной точек
        List<Boolean> isPositiveCriteria = extractIsPositiveCriteria(problem);
        Map<String, double[]> idealPoints = calculateIdealAndAntiIdealPoints(averagedRatings, isPositiveCriteria);

        // Вычисление евклидова расстояния между каждым усредненным рейтингом и идеальной/антиидеальной опорной точкой
        System.out.println(ANSI_GREEN + "\nDistances to Ideal and Anti-Ideal:" + ANSI_RESET);
        Map<String, double[]> distances = calculateEuclidDistances(averagedRatings, idealPoints.get("ideal"), idealPoints.get("antiIdeal"));

        // Вычисление удаленности от наихудшего опорного варианта
        System.out.println(ANSI_GREEN + "\nRanked Alternatives for " + problem.getProblemName() + " (Descending h(Ai)):" + ANSI_RESET);
        Map<String, Double> closeness = calculateRelativeCloseness(distances);

        result = rankAlternatives(closeness);
        return result;
    }

    public static List<NormalizedRating> normalize(List<Rating> ratings, Problem problem) {
        List<NormalizedRating> normalizedRatings = RatingNormalizer.normalizeRatings(ratings, problem);

        for (NormalizedRating normalizedRating : normalizedRatings) {
            System.out.println(normalizedRating.toJson());
        }
        return normalizedRatings;
    }

    public static Map<String, double[]> groupAndAverageRatings(List<NormalizedRating> normalizedRatings) {
        // Группируем оценки по экспертам
        Map<Integer, List<NormalizedRating>> groupedByExpert = normalizedRatings.stream()
                .collect(Collectors.groupingBy(NormalizedRating::getExpertId));

        // Усредненные оценки
        List<NormalizedRating> averagedRatings = new ArrayList<>();

        // Цикл по экспертам
        for (Map.Entry<Integer, List<NormalizedRating>> expertEntry : groupedByExpert.entrySet()) {
            List<NormalizedRating> ratingsList = expertEntry.getValue();

            // Количество критериев
            int criteriaCount = ratingsList.get(0).getNormalizedRatings().size();

            // Корень из суммы квадратов оценок эксперта по критериям
            double[] sumSquaredRatings = new double[criteriaCount];

            // Цикл по критериям
            for (int i = 0; i < criteriaCount; i++) {

                // Цикл по альтернативам, суммируем квадраты оценок
                for (NormalizedRating rating : ratingsList) {
                    List<Double> values = rating.getNormalizedRatings();
                    sumSquaredRatings[i] += Math.pow(values.get(i), 2);
                }
                sumSquaredRatings[i] = Math.sqrt(sumSquaredRatings[i]);
            }

            // Цикл по альтернативам — усредняем оценки
            for (NormalizedRating rating : ratingsList) {
                List<Double> values = rating.getNormalizedRatings();
                List<Double> averagedValues = new ArrayList<>();

                for (int i = 0; i < criteriaCount; i++) {
                    averagedValues.add(values.get(i) / sumSquaredRatings[i]);
                }

                averagedRatings.add(new NormalizedRating(
                        rating.getProblemName(),
                        rating.getAlternativeName(),
                        rating.getExpertId(),
                        averagedValues
                ));
            }
        }

        // Группируем усредненные оценки по альтернативам
        Map<String, List<double[]>> groupedByAlternative = new HashMap<>();

        for (NormalizedRating rating : averagedRatings) {
            String alternative = rating.getAlternativeName();
            groupedByAlternative
                    .computeIfAbsent(alternative, k -> new ArrayList<>())
                    .add(rating.getNormalizedRatings().stream().mapToDouble(Double::doubleValue).toArray());
        }

        // Усредняем оценки по всем экспертам
        Map<String, double[]> averagedByExpertsRatings = new HashMap<>();

        for (Map.Entry<String, List<double[]>> entry : groupedByAlternative.entrySet()) {
            String alternative = entry.getKey();
            List<double[]> ratingsList = entry.getValue();
            int numExperts = ratingsList.size();
            int criteriaCount = ratingsList.get(0).length;

            double[] sumRatings = new double[criteriaCount];

            // Суммируем оценки по каждому критерию
            for (double[] ratings : ratingsList) {
                for (int i = 0; i < criteriaCount; i++) {
                    sumRatings[i] += ratings[i];
                }
            }

            // Усредняем по количеству экспертов
            double[] averagedValues = new double[criteriaCount];
            for (int i = 0; i < criteriaCount; i++) {
                averagedValues[i] = sumRatings[i] / numExperts;
            }

            averagedByExpertsRatings.put(alternative, averagedValues);
        }

        // Выводим усреднённые оценки
        for (Map.Entry<String, double[]> entry : averagedByExpertsRatings.entrySet()) {
            System.out.println("Alternative: " + entry.getKey() + " -> " + Arrays.toString(entry.getValue()));
        }

        return averagedByExpertsRatings;
    }

    public static List<Boolean> extractIsPositiveCriteria(Problem problem) {
        return problem.getCriteria().stream()
                .map(criterion -> criterion.getOptimizationDirection().equalsIgnoreCase("max"))
                .collect(Collectors.toList());
    }

    public static Map<String, double[]> calculateIdealAndAntiIdealPoints(Map<String, double[]> averagedRatings, List<Boolean> isPositiveCriteria) {
        String ANSI_GREEN = "\033[32m";
        String ANSI_RESET = "\033[0m";
        int criteriaCount = averagedRatings.values().iterator().next().length;

        // Берём первую альтернативу как начальную точку
        Iterator<double[]> iterator = averagedRatings.values().iterator();
        double[] firstRatings = iterator.next().clone();

        double[] idealPoint = firstRatings.clone();       // y+ (лучшие оценки)
        double[] antiIdealPoint = firstRatings.clone();   // y- (худшие оценки)

        // Проход по всем альтернативам и критериям
        for (double[] ratings : averagedRatings.values()) {
            for (int i = 0; i < criteriaCount; i++) {
                if (isPositiveCriteria.get(i)) {
                    // Для положительных критериев
                    idealPoint[i] = Math.max(idealPoint[i], ratings[i]);
                    antiIdealPoint[i] = Math.min(antiIdealPoint[i], ratings[i]);
                } else {
                    // Для отрицательных критериев
                    idealPoint[i] = Math.min(idealPoint[i], ratings[i]);
                    antiIdealPoint[i] = Math.max(antiIdealPoint[i], ratings[i]);
                }
            }
        }

        System.out.println(ANSI_GREEN + "\ny+: " + ANSI_RESET + Arrays.toString(idealPoint));
        System.out.println(ANSI_GREEN + "y-: " + ANSI_RESET + Arrays.toString(antiIdealPoint));

        Map<String, double[]> idealPoints = new HashMap<>();
        idealPoints.put("ideal", idealPoint);
        idealPoints.put("antiIdeal", antiIdealPoint);

        return idealPoints;
    }

    public static Map<String, double[]> calculateEuclidDistances(
            Map<String, double[]> averagedRatings,
            double[] idealPoint,
            double[] antiIdealPoint) {

        Map<String, double[]> distances = new HashMap<>();

        for (Map.Entry<String, double[]> entry : averagedRatings.entrySet()) {
            String alternativeName = entry.getKey();
            double[] ratings = entry.getValue();

            double sumSquaredToIdeal = 0.0;
            double sumSquaredToAntiIdeal = 0.0;

            for (int q = 0; q < idealPoint.length; q++) {
                sumSquaredToIdeal += Math.pow(ratings[q] - idealPoint[q], 2);
                sumSquaredToAntiIdeal += Math.pow(ratings[q] - antiIdealPoint[q], 2);
            }

            double distanceToIdeal = Math.sqrt(sumSquaredToIdeal);
            double distanceToAntiIdeal = Math.sqrt(sumSquaredToAntiIdeal);

            // Сохраняем в Map, используя название альтернативы
            distances.put(alternativeName, new double[]{distanceToIdeal, distanceToAntiIdeal});
        }

        // Вывод результатов
        distances.forEach((alt, dists) ->
                System.out.println(alt + " -> d_2^+: " + dists[0] + ", d_2^-: " + dists[1])
        );

        return distances;
    }

    public static Map<String, Double> calculateRelativeCloseness(Map<String, double[]> distances) {
        Map<String, Double> closeness = new HashMap<>();

        for (Map.Entry<String, double[]> entry : distances.entrySet()) {
            String alternative = entry.getKey();
            double dPlus = entry.getValue()[0];   // d_2^+
            double dMinus = entry.getValue()[1];  // d_2^-

            // Вычисляем h(A_i)
            double hAi = dMinus / (dPlus + dMinus);
            closeness.put(alternative, hAi);
        }

        return closeness;
    }

    public static List<Map.Entry<String, Double>> rankAlternatives(Map<String, Double> closeness) {
        // Сортируем список альтернатив по убыванию h(Ai)
        List<Map.Entry<String, Double>> sortedAlternatives = new ArrayList<>(closeness.entrySet());
        sortedAlternatives.sort((a, b) -> Double.compare(b.getValue(), a.getValue())); // По убыванию

        // Вывод результатов с округлением до сотых
        for (int i = 0; i < sortedAlternatives.size(); i++) {
            String formattedValue = String.format("%.3f", sortedAlternatives.get(i).getValue());
            System.out.println((i + 1) + ". " + sortedAlternatives.get(i).getKey() + " -> h(Ai): " + formattedValue);
        }

        return sortedAlternatives;
    }
}
