import models.Problem;
import models.Rating;
import topsis.Topsis;
import utils.DataGenerator;
import utils.JSONReader;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        boolean generateData = true;
        boolean showLoadedResources = false;

        List<Problem> problems;
        List<Rating> ratings;


        if (generateData) {
            DataGenerator.generateData(8, 4, 10);
            problems = JSONReader.readProblems("generatedData/generatedProblems.json");
            ratings = JSONReader.readRatings("generatedData/generatedRatings.json");
        } else {
            problems = JSONReader.readProblems("problems.json");
            ratings = JSONReader.readRatings("ratings.json");
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
            Topsis.run(problem, ratings);
        }
    }
}
