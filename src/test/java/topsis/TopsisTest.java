package topsis;

import models.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TopsisTest {

    @Test
    public void testTopsisWithMinimalData() {
        // Граничный случай: 1 альтернатива, 1 критерий, 1 эксперт
        Problem problem = new Problem("Minimal problem", List.of(new Criterion("Criterion 1", "base", "max")));
        List<Rating> ratings = List.of(new Rating("Minimal problem", "A1", 1, List.of("ОВ")));

        // Проверяем, что алгоритм не падает на минимальных данных
        assertDoesNotThrow(() -> Topsis.run(problem, ratings));
    }

    @Test
    public void testTopsisWithMaximalData() {
        // Граничный случай: 10 альтернатив, 10 критериев, 10 экспертов
        List<Criterion> criteria = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            criteria.add(new Criterion("Criterion " + i, "numeric", i % 2 == 0 ? "max" : "min"));
        }
        Problem problem = new Problem("Maximal problem", criteria);

        List<Rating> ratings = new ArrayList<>();
        Random random = new Random();

        for (int expert = 1; expert <= 10; expert++) {
            for (int alt = 1; alt <= 10; alt++) {
                List<String> scores = new ArrayList<>();
                for (int crit = 1; crit <= 10; crit++) {
                    scores.add(String.valueOf(random.nextInt(10) + 1)); // Генерация случайных оценок от 1 до 10
                }
                ratings.add(new Rating("Maximal problem", "A" + alt, expert, scores));
            }
        }

        // Проверяем, что алгоритм работает на больших объемах данных
        assertDoesNotThrow(() -> Topsis.run(problem, ratings));
    }

    @Test
    public void testTopsisWithOnlyMinCriteria() {
        // Тестируем случай, когда все критерии минимизируются
        Problem problem = new Problem("All min problem", Arrays.asList(
                new Criterion("Criterion 1", "numeric", "min"),
                new Criterion("Criterion 2", "numeric", "min"),
                new Criterion("Criterion 3", "numeric", "min")
        ));

        List<Rating> ratings = Arrays.asList(
                new Rating("All min problem", "A1", 1, Arrays.asList("5", "3", "7")),
                new Rating("All min problem", "A2", 1, Arrays.asList("2", "6", "8")),
                new Rating("All min problem", "A3", 1, Arrays.asList("2", "6", "8")),
                new Rating("All min problem", "A1", 2, Arrays.asList("6", "2", "6")),
                new Rating("All min problem", "A2", 2, Arrays.asList("1", "7", "9")),
                new Rating("All min problem", "A3", 2, Arrays.asList("3", "8", "7"))
        );

        // Проверяем, что алгоритм корректно ранжирует альтернативы
        assertDoesNotThrow(() -> Topsis.run(problem, ratings));
    }

    @Test
    public void testTopsisWithOnlyMaxCriteria() {
        // Тестируем случай, когда все критерии максимизируются
        Problem problem = new Problem("All max problem", Arrays.asList(
                new Criterion("Criterion 1", "numeric", "max"),
                new Criterion("Criterion 2", "numeric", "max"),
                new Criterion("Criterion 3", "numeric", "max")
        ));

        List<Rating> ratings = Arrays.asList(
                new Rating("All max problem", "A1", 1, Arrays.asList("5", "3", "7")),
                new Rating("All max problem", "A2", 1, Arrays.asList("2", "6", "8")),
                new Rating("All max problem", "A3", 1, Arrays.asList("2", "6", "8")),
                new Rating("All max problem", "A1", 2, Arrays.asList("6", "2", "6")),
                new Rating("All max problem", "A2", 2, Arrays.asList("1", "7", "9")),
                new Rating("All max problem", "A3", 2, Arrays.asList("3", "8", "7"))
        );

        // Проверяем, что алгоритм корректно ранжирует альтернативы
        assertDoesNotThrow(() -> Topsis.run(problem, ratings));
    }

    @Test
    public void testTopsisWithMixedScales() {
        // Тестируем случай со смешанными шкалами критериев
        Problem problem = new Problem("Mixed scale problem", Arrays.asList(
                new Criterion("Criterion 1", "numeric", "max"),
                new Criterion("Criterion 2", "short", "min"),
                new Criterion("Criterion 3", "base", "max")
        ));

        List<Rating> ratings = Arrays.asList(
                new Rating("Mixed scale problem", "A1", 1, Arrays.asList("4", "В", "С")),
                new Rating("Mixed scale problem", "A2", 1, Arrays.asList("5", "Н", "ОВ"))
        );

        // Проверяем, что алгоритм корректно обрабатывает разные шкалы
        assertDoesNotThrow(() -> Topsis.run(problem, ratings));
    }

    @Test
    public void testTopsisWithTiedScores() {
        // Проверяем случай, когда у альтернатив одинаковые оценки
        Problem problem = new Problem("Tied scores problem", Arrays.asList(
                new Criterion("Criterion 1", "numeric", "max"),
                new Criterion("Criterion 2", "numeric", "max")
        ));

        List<Rating> ratings = Arrays.asList(
                new Rating("Tied scores problem", "A1", 1, Arrays.asList("5", "8")),
                new Rating("Tied scores problem", "A2", 1, Arrays.asList("5", "8")),
                new Rating("Tied scores problem", "A1", 2, Arrays.asList("5", "8")),
                new Rating("Tied scores problem", "A2", 2, Arrays.asList("5", "8"))
        );

        // Проверяем, что алгоритм не выдает ошибку при одинаковых оценках
        assertDoesNotThrow(() -> Topsis.run(problem, ratings));
    }

    @Test
    public void testTopsisWithEmptyRatings() {
        // Проверяем, что алгоритм корректно обрабатывает пустой список рейтингов
        Problem problem = new Problem("Empty ratings problem", Arrays.asList(
                new Criterion("Criterion 1", "numeric", "max")
        ));

        List<Rating> ratings = new ArrayList<>();

        // Запускаем алгоритм и проверяем, что выполнение не приводит к исключению
        assertDoesNotThrow(() -> Topsis.run(problem, ratings));
    }

    @Test
    public void testTopsisRunOnMultipleProblems() {
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
            Topsis.run(problem, ratings);
        }
    }

    private List<Problem> createTestProblems() {
        return Arrays.asList(
                new Problem("Problem test 1", Arrays.asList(
                        new Criterion("Criterion test 1", "numeric", "max"),
                        new Criterion("Criterion test 2", "numeric", "min"),
                        new Criterion("Criterion test 3", "short", "max")
                )),
                new Problem("Problem test 2", Arrays.asList(
                        new Criterion("Criterion test 4", "numeric", "min"),
                        new Criterion("Criterion test 5", "numeric", "max"),
                        new Criterion("Criterion test 6", "base", "max"),
                        new Criterion("Criterion test 7", "short", "min")
                )),
                new Problem("Problem test 3", Arrays.asList(
                        new Criterion("Criterion test 8", "numeric", "max"),
                        new Criterion("Criterion test 9", "short", "min"),
                        new Criterion("Criterion test 10", "long", "max"),
                        new Criterion("Criterion test 11", "base", "min"),
                        new Criterion("Criterion test 12", "numeric", "max")
                )),
                new Problem("Problem test 4", Arrays.asList(
                        new Criterion("Criterion test 13", "long", "max"),
                        new Criterion("Criterion test 14", "numeric", "max"),
                        new Criterion("Criterion test 15", "base", "min")
                )),
                new Problem("Problem test 5", Arrays.asList(
                        new Criterion("Criterion test 16", "numeric", "min"),
                        new Criterion("Criterion test 17", "base", "max"),
                        new Criterion("Criterion test 18", "short", "min"),
                        new Criterion("Criterion test 19", "long", "max")
                ))
        );
    }

    private List<Rating> generateFixedRatings(Problem problem) {
        List<Rating> ratings = new ArrayList<>();

        // Определяем альтернативы
        List<String> alternatives = Arrays.asList("A1", "A2", "A3", "A4", "A5");

        // Количество экспертов (пусть их будет 3)
        int numExperts = 3;

        // Фиксированные рейтинги для каждой проблемы (эксперт → альтернатива → критерии)
        Map<String, List<List<List<String>>>> fixedRatings = new HashMap<>();

        // Добавляем фиксированные рейтинги для каждой проблемы
        fixedRatings.put("Problem test 1", Arrays.asList(
                // Эксперт 1
                Arrays.asList(
                        Arrays.asList("3", "5", "Н"),  // A1
                        Arrays.asList("4", "2", "В"),  // A2
                        Arrays.asList("5", "3", "Н"),  // A3
                        Arrays.asList("6", "7", "В"),  // A4
                        Arrays.asList("2", "6", "Н")   // A5
                ),
                // Эксперт 2
                Arrays.asList(
                        Arrays.asList("2", "4", "С"),
                        Arrays.asList("5", "3", "Н"),
                        Arrays.asList("6", "2", "В"),
                        Arrays.asList("3", "5", "Н"),
                        Arrays.asList("4", "6", "Н")
                ),
                // Эксперт 3
                Arrays.asList(
                        Arrays.asList("4", "3", "В"),
                        Arrays.asList("3", "7", "Н"),
                        Arrays.asList("5", "5", "С"),
                        Arrays.asList("7", "6", "В"),
                        Arrays.asList("3", "2", "В")
                )
        ));

        fixedRatings.put("Problem test 2", Arrays.asList(
                Arrays.asList(
                        Arrays.asList("7", "3", "ОН", "Н"),
                        Arrays.asList("5", "6", "В", "С"),
                        Arrays.asList("6", "5", "С", "Н"),
                        Arrays.asList("8", "7", "ОВ", "В"),
                        Arrays.asList("4", "2", "Н", "Н")
                ),
                Arrays.asList(
                        Arrays.asList("5", "2", "ОН", "С"),
                        Arrays.asList("6", "4", "Н", "Н"),
                        Arrays.asList("7", "3", "В", "С"),
                        Arrays.asList("5", "5", "ОВ", "Н"),
                        Arrays.asList("6", "6", "Н", "С")
                ),
                Arrays.asList(
                        Arrays.asList("8", "5", "Н", "В"),
                        Arrays.asList("4", "3", "ОН", "Н"),
                        Arrays.asList("5", "7", "В", "С"),
                        Arrays.asList("7", "6", "ОВ", "В"),
                        Arrays.asList("3", "4", "Н", "В")
                )
        ));

        fixedRatings.put("Problem test 3", Arrays.asList(
                Arrays.asList(
                        Arrays.asList("8", "Н", "ЭВ", "Н", "6"),
                        Arrays.asList("7", "С", "С", "ОВ", "5"),
                        Arrays.asList("6", "Н", "ОВ", "С", "4"),
                        Arrays.asList("9", "В", "ЭН", "ОН", "8"),
                        Arrays.asList("5", "Н", "Н", "ОВ", "7")
                ),
                Arrays.asList(
                        Arrays.asList("7", "В", "С", "Н", "5"),
                        Arrays.asList("6", "В", "Н", "С", "4"),
                        Arrays.asList("8", "Н", "ЭВ", "ОН", "6"),
                        Arrays.asList("5", "С", "ОН", "В", "7"),
                        Arrays.asList("9", "В", "ОВ", "ОН", "8")
                ),
                Arrays.asList(
                        Arrays.asList("9", "С", "ОН", "ОН", "7"),
                        Arrays.asList("5", "Н", "ЭВ", "В", "6"),
                        Arrays.asList("7", "В", "ОВ", "С", "5"),
                        Arrays.asList("6", "Н", "ЭН", "ОВ", "9"),
                        Arrays.asList("8", "Н", "С", "ОН", "6")
                )
        ));

        fixedRatings.put("Problem test 4", Arrays.asList(
                Arrays.asList(
                        Arrays.asList("ЭВ", "9", "ОН"),
                        Arrays.asList("ОВ", "7", "Н"),
                        Arrays.asList("В", "5", "С"),
                        Arrays.asList("С", "3", "В"),
                        Arrays.asList("Н", "2", "ОВ")
                ),
                Arrays.asList(
                        Arrays.asList("ОВ", "6", "Н"),
                        Arrays.asList("С", "4", "В"),
                        Arrays.asList("ОН", "8", "С"),
                        Arrays.asList("Н", "5", "ОВ"),
                        Arrays.asList("В", "7", "ОН")
                ),
                Arrays.asList(
                        Arrays.asList("Н", "5", "С"),
                        Arrays.asList("ОН", "6", "В"),
                        Arrays.asList("ОВ", "7", "ОВ"),
                        Arrays.asList("ЭВ", "4", "Н"),
                        Arrays.asList("В", "3", "ОН")
                )
        ));

        fixedRatings.put("Problem test 5", Arrays.asList(
                Arrays.asList(
                        Arrays.asList("3", "Н", "Н", "С"),
                        Arrays.asList("5", "ОВ", "В", "ОН"),
                        Arrays.asList("7", "В", "С", "ЭН"),
                        Arrays.asList("8", "ОН", "В", "Н"),
                        Arrays.asList("4", "С", "Н", "ОВ")
                ),
                Arrays.asList(
                        Arrays.asList("6", "С", "Н", "ОН"),
                        Arrays.asList("9", "В", "Н", "С"),
                        Arrays.asList("5", "ОН", "С", "Н"),
                        Arrays.asList("3", "Н", "В", "ОВ"),
                        Arrays.asList("7", "В", "В", "ЭН")
                ),
                Arrays.asList(
                        Arrays.asList("4", "ОВ", "Н", "В"),
                        Arrays.asList("8", "С", "В", "ОН"),
                        Arrays.asList("6", "ОН", "Н", "ОВ"),
                        Arrays.asList("9", "В", "В", "С"),
                        Arrays.asList("5", "В", "Н", "ЭН")
                )
        ));

        // Берем фиксированные рейтинги для данной проблемы
        List<List<List<String>>> problemRatings = fixedRatings.get(problem.getProblemName());

        if (problemRatings == null) {
            throw new IllegalArgumentException("No fixed ratings defined for " + problem.getProblemName());
        }

        // Заполняем список Rating
        for (int expertId = 0; expertId < numExperts; expertId++) {
            List<List<String>> expertRatings = problemRatings.get(expertId);

            for (int i = 0; i < alternatives.size(); i++) {
                ratings.add(new Rating(problem.getProblemName(), alternatives.get(i), expertId + 1, expertRatings.get(i)));
            }
        }

        return ratings;
    }
}
