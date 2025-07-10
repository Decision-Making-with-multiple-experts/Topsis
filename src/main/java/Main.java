import methods.GroupPreference;
import methods.Rampa;
import models.Problem;
import models.Rating;
import methods.Topsis;
import utils.DataGenerator;
import utils.JSONReader;
import utils.OutputWriter;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        boolean generateData = false;
        boolean showLoadedResources = false;

        List<Problem> problems;
        List<Rating> ratings;

        if (generateData) {
            DataGenerator.generateData(5, 8, 10);
            String latestDirectory = getLatestGeneratedDataDirectory();
            OutputWriter.writeResourcePath(latestDirectory + "\\generatedRatings.json");
            problems = JSONReader.readProblems(latestDirectory + "/generatedProblems.json");
            ratings = JSONReader.readRatings(latestDirectory + "/generatedRatings.json");
        } else {
            String problemPath = "testProblems.json";
            String ratingPath = "testRatings.json";
            OutputWriter.writeResourcePath(ratingPath);
            problems = JSONReader.readProblems(problemPath);
            ratings = JSONReader.readRatings(ratingPath);
        }

        if (showLoadedResources) {
            System.out.println("Loaded problems:");
            for (Problem problem : problems) {
                System.out.println(problem.toJson());
            }
            System.out.println();

            System.out.println("Loaded ratings:");
            for (Rating rating : ratings) {
                System.out.println(rating.toJson());
            }
            System.out.println();
        }

        // Выполнение алгоритма TOPSIS для каждой проблемы
        for (Problem problem : problems) {
            OutputWriter.writeResults(Topsis.run(problem, ratings), problem);
        }

        // Выполнение алгоритма GroupPreference для каждой проблемы
        for (Problem problem : problems) {
            GroupPreference.run(problem, ratings);
        }

        // Выполнение алгоритма РАМПА для каждой проблемы
        for (Problem problem : problems) {
            Rampa.run(problem, ratings);
        }
        OutputWriter.writeEnd();

        long endTime = System.nanoTime(); // Конец измерения времени

        System.out.println("\n\nTopsis execution time: " + (endTime - startTime) / 1_000_000.0 + " ms");
    }

    /**
     * Находит последнюю сгенерированную папку в "generatedData/".
     */
    private static String getLatestGeneratedDataDirectory() {
        File baseDir = new File("src/main/resources/generatedData");

        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return null;
        }

        File[] directories = baseDir.listFiles(File::isDirectory);
        if (directories == null || directories.length == 0) {
            return null;
        }

        // Сортируем папки по имени (timestamp) в порядке убывания (новейшие первые)
        Arrays.sort(directories, Comparator.comparing(File::getName).reversed());

        String relativePath = directories[1].getPath().replaceFirst("src[/\\\\]main[/\\\\]resources[/\\\\]?", "");

        return relativePath;
    }
}
