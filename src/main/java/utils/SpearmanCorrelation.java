package utils;

import java.util.*;

public class SpearmanCorrelation {

    public static double calculateSpearman(List<String> ranking1, List<String> ranking2) {
        if (ranking1.size() != ranking2.size()) {
            throw new IllegalArgumentException("Списки должны быть одинаковой длины");
        }

        int n = ranking1.size();
        Map<String, Integer> rankMap1 = getRankMap(ranking1);
        Map<String, Integer> rankMap2 = getRankMap(ranking2);

        double sumSquaredDiffs = 0.0;
        for (String alternative : ranking1) {
            int rank1 = rankMap1.get(alternative);
            int rank2 = rankMap2.get(alternative);
            double diff = rank1 - rank2;
            sumSquaredDiffs += diff * diff;
        }

        return 1 - (6 * sumSquaredDiffs) / (n * (n * n - 1));
    }

    private static Map<String, Integer> getRankMap(List<String> ranking) {
        Map<String, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < ranking.size(); i++) {
            rankMap.put(ranking.get(i), i + 1);
        }
        return rankMap;
    }

    public static void main(String[] args) {

        // Эксперимент 1. Согласованные данные
//         List<String> rampa = Arrays.asList("Python", "Go", "Rust", "C++", "Java", "C#", "JavaScript");
//         List<String> topsis = Arrays.asList("Python", "Go", "Rust", "C++", "Java", "C#", "JavaScript");
//         List<String> aramis = Arrays.asList("Rust", "Go", "Python", "C++", "Java", "C#", "JavaScript");

        // Эксперимент 1. Не согласованные данные

//        List<String> rampa = Arrays.asList("Rust", "Python", "Java", "C++", "Go", "JavaScript", "C#");
//        List<String> topsis = Arrays.asList("Rust", "Java", "Python", "JavaScript", "C#", "C++", "Go");
//        List<String> aramis = Arrays.asList("Rust", "Python", "C++", "Java", "JavaScript", "Go", "C#");

        // Эксперимент 4. Согласованные данные

//        List<String> rampa = Arrays.asList("Python", "Go", "C++", "Rust", "Java");
//        List<String> topsis = Arrays.asList("Python", "Go", "C++", "Rust", "Java");
//        List<String> aramis = Arrays.asList("Python", "C++", "Go", "Rust", "Java");

        // Эксперимент 4. Не согласованные данные

        List<String> rampa = Arrays.asList("C++", "Go", "Python", "Rust", "Java");
        List<String> topsis = Arrays.asList("Python", "C++", "Go", "Rust", "Java");
        List<String> aramis = Arrays.asList("C++", "Python", "Go", "Java", "Rust");

        double spearmanRT = calculateSpearman(rampa, topsis);
        double spearmanRA = calculateSpearman(rampa, aramis);
        double spearmanTA = calculateSpearman(topsis, aramis);

        System.out.println("Spearman's coefficient R-T: " + spearmanRT);
        System.out.println("Spearman's coefficient R-A: " + spearmanRA);
        System.out.println("Spearman's coefficient T-A: " + spearmanTA);
    }
}
