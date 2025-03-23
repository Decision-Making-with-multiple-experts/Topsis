package topsis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Criterion;
import models.NormalizedRating;
import models.Problem;
import models.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    private Problem problem;
    private List<Rating> ratings;
    private List<NormalizedRating> expectedNormalizedRatings;
    private Map<String, double[]> expectedAverages;
    private List<Boolean> expectedIsPositiveCriteria;
    private double[] expectedIdeal;
    private double[] expectedAntiIdeal;
    private Map<String, double[]> expectedEuclidDistances;
    private Map<String, Double> expectedCloseness;
    private List<List<Map.Entry<String, Double>>> possibleRankings;

    @BeforeEach
    public void setUp() {
        problem = new Problem("TestProblem", Arrays.asList(
                new Criterion("Criterion 1", "base", "max"),
                new Criterion("Criterion 2", "numeric", "min"),
                new Criterion("Criterion 3", "short", "min")
        ));

        ratings = Arrays.asList(
                new Rating("TestProblem", "A1", 1, Arrays.asList("ОВ", "3", "В")),
                new Rating("TestProblem", "A2", 1, Arrays.asList("Н", "6", "В")),
                new Rating("TestProblem", "A3", 1, Arrays.asList("С", "6", "Н")),
                new Rating("TestProblem", "A1", 2, Arrays.asList("В", "2", "С")),
                new Rating("TestProblem", "A2", 2, Arrays.asList("С", "7", "Н")),
                new Rating("TestProblem", "A3", 2, Arrays.asList("ОН", "8", "Н"))
        );

        expectedNormalizedRatings = Arrays.asList(
                new NormalizedRating("TestProblem", "A1", 1, Arrays.asList(9.0, 3.0, 9.0)),
                new NormalizedRating("TestProblem", "A2", 1, Arrays.asList(3.0, 6.0, 9.0)),
                new NormalizedRating("TestProblem", "A3", 1, Arrays.asList(5.0, 6.0, 1.0)),
                new NormalizedRating("TestProblem", "A1", 2, Arrays.asList(7.0, 2.0, 5.0)),
                new NormalizedRating("TestProblem", "A2", 2, Arrays.asList(5.0, 7.0, 1.0)),
                new NormalizedRating("TestProblem", "A3", 2, Arrays.asList(1.0, 8.0, 1.0))
        );

        expectedAverages = new HashMap<>();
        expectedAverages.put("A1", new double[]{0.82, 0.26, 0.83});
        expectedAverages.put("A2", new double[]{0.43, 0.66, 0.45});
        expectedAverages.put("A3", new double[]{0.29, 0.70, 0.14});

        expectedIsPositiveCriteria = Arrays.asList(true, false, false);

        expectedIdeal = new double[]{0.82, 0.26, 0.14};

        expectedAntiIdeal = new double[]{0.29, 0.70, 0.83};

        expectedEuclidDistances = new HashMap<>();
        expectedEuclidDistances.put("A1", new double[]{0.69, 0.69});
        expectedEuclidDistances.put("A2", new double[]{0.64, 0.41});
        expectedEuclidDistances.put("A3", new double[]{0.69, 0.69});

        expectedCloseness = new HashMap<>();
        expectedCloseness.put("A1", 0.5);
        expectedCloseness.put("A2", 0.39);
        expectedCloseness.put("A3", 0.5);

        possibleRankings = Arrays.asList(
                List.of(
                        new AbstractMap.SimpleEntry<>("A1", 0.5),
                        new AbstractMap.SimpleEntry<>("A3", 0.5),
                        new AbstractMap.SimpleEntry<>("A2", 0.39)
                ),
                List.of(
                        new AbstractMap.SimpleEntry<>("A3", 0.5),
                        new AbstractMap.SimpleEntry<>("A1", 0.5),
                        new AbstractMap.SimpleEntry<>("A2", 0.39)
                )
        );
    }

    @Test
    public void testNormalize() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<NormalizedRating> normalizedRatings = Topsis.normalize(ratings, problem);
        assertNotNull(normalizedRatings);
        assertEquals(objectMapper.writeValueAsString(expectedNormalizedRatings),
                objectMapper.writeValueAsString(normalizedRatings));
    }

    @Test
    public void testGroupAndAverageRatings() {
        Map<String, double[]> result = Topsis.groupAndAverageRatings(expectedNormalizedRatings);
        assertNotNull(result);
        for (String alternative : expectedAverages.keySet()) {
            assertArrayEquals(expectedAverages.get(alternative), result.get(alternative), 0.01);
        }
    }

    @Test
    public void testExtractIsPositiveCriteria() {
        List<Boolean> result = Topsis.extractIsPositiveCriteria(problem);
        assertEquals(expectedIsPositiveCriteria, result);
    }

    @Test
    public void testCalculateIdealAndAntiIdealPoints() {
        Map<String, double[]> result = Topsis.calculateIdealAndAntiIdealPoints(expectedAverages, expectedIsPositiveCriteria);
        assertNotNull(result);
        assertArrayEquals(expectedIdeal, result.get("ideal"));
        assertArrayEquals(expectedAntiIdeal, result.get("antiIdeal"));
    }

    @Test
    public void testCalculateEuclidDistances() {
        Map<String, double[]> result = Topsis.calculateEuclidDistances(expectedAverages, expectedIdeal, expectedAntiIdeal);
        assertNotNull(result);
        for (String alternative : expectedEuclidDistances.keySet()) {
            assertArrayEquals(expectedEuclidDistances.get(alternative), result.get(alternative), 0.01);
        }
    }

    @Test
    public void testCalculateRelativeCloseness() {
        Map<String, Double> result = Topsis.calculateRelativeCloseness(expectedEuclidDistances);
        assertNotNull(result);
        for (String alternative : expectedCloseness.keySet()) {
            assertEquals(expectedCloseness.get(alternative), result.get(alternative), 0.01);
        }
    }

    @Test
    public void testRankAlternatives() {
        List<Map.Entry<String, Double>> result = Topsis.rankAlternatives(expectedCloseness);
        assertNotNull(result);
        assertTrue(possibleRankings.contains(result), "The ranking does not match the expected options.");
    }

    @Test
    public void testRun() {
        List<List<String>> expectedRankings = Arrays.asList(
                List.of("A1", "A3", "A2"),
                List.of("A3", "A1", "A2")
        );
        List<Map.Entry<String, Double>> result = Topsis.run(problem, ratings);
        List<String> actualRanking = result.stream()
                .map(Map.Entry::getKey)
                .toList();
        assertNotNull(result);
        assertTrue(expectedRankings.contains(actualRanking), "The ranking does not match the expected options.");
    }
}
