import models.Alternative;
import models.Problem;
import models.Rating;
import topsis.Topsis;
import utils.JSONReader;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // Загрузка данных из ресурсов
        List<Alternative> alternatives = JSONReader.readAlternatives("alternatives.json");
        List<Problem> problems = JSONReader.readProblems("problems.json");
        List<Rating> ratings = JSONReader.readRatings("ratings.json");

        boolean showLoadedResources = false;

        if (showLoadedResources) {
            System.out.println("Loaded problems:");
            for (Problem problem : problems) {
                System.out.println(problem.toJson());
            }
            System.out.println();

            System.out.println("Loaded alternatives:");
            for (Alternative alt : alternatives) {
                System.out.println(alt.toJson());
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
