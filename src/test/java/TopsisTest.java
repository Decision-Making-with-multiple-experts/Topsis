import models.*;
import org.junit.jupiter.api.Test;
import topsis.Topsis;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TopsisTest {

    @Test
    void testTopsisRunOnMultipleProblems() {
        // 5 фиксированных тестовых проблем
        List<Problem> problems = createTestProblems();

        // Цикл по всем проблемам
        for (Problem problem : problems) {
            System.out.println("\nRunning TOPSIS for problem: " + problem.getProblemName());

            // Фиксированные альтернативы и рейтинги
            List<Rating> ratings = generateFixedRatings(problem);

            // Проверяем, что данные загружены корректно
            assertNotNull(problem);
            assertNotNull(ratings);
            assertFalse(ratings.isEmpty());

            // Запуск TOPSIS
            System.out.println(problem);
            System.out.println(ratings);
            Topsis.run(problem, ratings);
        }
    }

    private List<Problem> createTestProblems() {
        return Arrays.asList(
                new Problem("Problem 1", Arrays.asList(
                        new Criterion("Criterion 1", "numeric", "max"),
                        new Criterion("Criterion 2", "numeric", "min"),
                        new Criterion("Criterion 3", "short", "max")
                )),
                new Problem("Problem 2", Arrays.asList(
                        new Criterion("Criterion 1", "numeric", "min"),
                        new Criterion("Criterion 2", "numeric", "max"),
                        new Criterion("Criterion 3", "base", "max"),
                        new Criterion("Criterion 4", "short", "min")
                )),
                new Problem("Problem 3", Arrays.asList(
                        new Criterion("Criterion 1", "numeric", "max"),
                        new Criterion("Criterion 2", "short", "min"),
                        new Criterion("Criterion 3", "long", "max"),
                        new Criterion("Criterion 4", "base", "min"),
                        new Criterion("Criterion 5", "numeric", "max")
                )),
                new Problem("Problem 4", Arrays.asList(
                        new Criterion("Criterion 1", "long", "max"),
                        new Criterion("Criterion 2", "numeric", "max"),
                        new Criterion("Criterion 3", "base", "min")
                )),
                new Problem("Problem 5", Arrays.asList(
                        new Criterion("Criterion 1", "numeric", "min"),
                        new Criterion("Criterion 2", "base", "max"),
                        new Criterion("Criterion 3", "short", "min"),
                        new Criterion("Criterion 4", "long", "max")
                ))
        );
    }

    private List<Rating> generateFixedRatings(Problem problem) {
        Map<String, List<List<String>>> fixedRatings = new HashMap<>();

        // Фиксированные рейтинги для каждой проблемы
        fixedRatings.put("Problem 1", Arrays.asList(
                Arrays.asList("3", "5", "Н"),
                Arrays.asList("4", "2", "В"),
                Arrays.asList("5", "3", "Н"),
                Arrays.asList("6", "7", "В"),
                Arrays.asList("2", "6", "Н")
        ));

        fixedRatings.put("Problem 2", Arrays.asList(
                Arrays.asList("7", "3", "ОН", "Н"),
                Arrays.asList("5", "6", "В", "С"),
                Arrays.asList("6", "5", "С", "Н"),
                Arrays.asList("8", "7", "ОВ", "В"),
                Arrays.asList("4", "2", "Н", "Н")
        ));

        fixedRatings.put("Problem 3", Arrays.asList(
                Arrays.asList("9", "С", "ЭН", "ОН", "5"),
                Arrays.asList("7", "В", "С", "В", "4"),
                Arrays.asList("6", "Н", "ОВ", "ОН", "6"),
                Arrays.asList("5", "С", "Н", "С", "8"),
                Arrays.asList("4", "ОН", "ЭВ", "ОВ", "7")
        ));

        fixedRatings.put("Problem 4", Arrays.asList(
                Arrays.asList("ЭВ", "9", "ОН"),
                Arrays.asList("ОВ", "7", "Н"),
                Arrays.asList("В", "5", "С"),
                Arrays.asList("С", "3", "В"),
                Arrays.asList("Н", "2", "ОВ")
        ));

        fixedRatings.put("Problem 5", Arrays.asList(
                Arrays.asList("2", "ОН", "С", "ЭН"),
                Arrays.asList("5", "Н", "ОН", "ОВ"),
                Arrays.asList("8", "С", "В", "С"),
                Arrays.asList("6", "ОВ", "ОН", "Н"),
                Arrays.asList("3", "В", "С", "ЭВ")
        ));

        List<Rating> ratings = new ArrayList<>();
        List<List<String>> problemRatings = fixedRatings.get(problem.getProblemName());

        if (problemRatings == null) {
            throw new IllegalArgumentException("No ratings defined for " + problem.getProblemName());
        }

        List<String> alternatives = Arrays.asList("A1", "A2", "A3", "A4", "A5");
        int expertId = 1;

        for (int i = 0; i < problemRatings.size(); i++) {
            ratings.add(new Rating(problem.getProblemName(), alternatives.get(i), expertId, problemRatings.get(i)));
            expertId++;
        }

        return ratings;
    }
}
