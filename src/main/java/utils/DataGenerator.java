package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataGenerator {
    private static final List<String> SCALES = Arrays.asList("long", "base", "short");
    private static final List<String> OPTIMIZATION = Arrays.asList("max", "min");

    private static final Map<String, List<String>> SCALE_VALUES = new HashMap<>() {{
        put("long", Arrays.asList("ЭВ", "ОВ", "В", "С", "Н", "ОН", "ЭН"));
        put("base", Arrays.asList("ОВ", "В", "С", "Н", "ОН"));
        put("short", Arrays.asList("В", "С", "Н"));
    }};

    private static final Random random = new Random();

    public static void generateData(int numAlternatives, int numCriteria, int numExperts) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Генерация проблем
        ObjectNode problemsJson = objectMapper.createObjectNode();
        ArrayNode problemsArray = objectMapper.createArrayNode();

        ObjectNode problemNode = objectMapper.createObjectNode();
        problemNode.put("problemName", "Problem 1");

        ArrayNode criteriaArray = objectMapper.createArrayNode();
        List<String> criteriaScales = new ArrayList<>(); // Запоминаем шкалы критериев

        for (int i = 1; i <= numCriteria; i++) {
            ObjectNode criterion = objectMapper.createObjectNode();
            String scale = getRandomElement(SCALES);
            criteriaScales.add(scale); // Сохраняем шкалу для последующего использования

            criterion.put("criterionName", "Criterion " + i + " description");
            criterion.put("criterionScale", scale);
            criterion.put("optimizationDirection", getRandomElement(OPTIMIZATION));
            criteriaArray.add(criterion);
        }
        problemNode.set("criteria", criteriaArray);
        problemsArray.add(problemNode);
        problemsJson.set("problems", problemsArray);

        // Генерация оценок с учетом шкал критериев
        ObjectNode ratingsJson = objectMapper.createObjectNode();
        ArrayNode ratingsArray = objectMapper.createArrayNode();

        for (int expertId = 1; expertId <= numExperts; expertId++) {
            for (int alt = 1; alt <= numAlternatives; alt++) {
                ObjectNode ratingNode = objectMapper.createObjectNode();
                ratingNode.put("problemName", "Problem 1");
                ratingNode.put("alternativeName", "Alternative " + alt);
                ratingNode.put("expertId", expertId);

                ArrayNode alternativeRatings = objectMapper.createArrayNode();
                for (String scale : criteriaScales) {
                    alternativeRatings.add(getRandomElement(SCALE_VALUES.get(scale))); // Генерируем оценку из соответствующей шкалы
                }
                ratingNode.set("alternativeRatings", alternativeRatings);
                ratingsArray.add(ratingNode);
            }
        }
        ratingsJson.set("ratings", ratingsArray);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        // Папка для хранения файлов с временной меткой
        String directoryPath = "src/main/resources/generatedData/" + timestamp;

        try {
            // Создаём директорию, если её нет
            Files.createDirectories(Paths.get(directoryPath));

            // Формируем уникальные имена файлов
            String problemsFilename = directoryPath + "/generatedProblems.json";
            String ratingsFilename = directoryPath + "/generatedRatings.json";

            // Запись в файлы с уникальными именами
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(problemsFilename), problemsJson);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(ratingsFilename), ratingsJson);

            System.out.println("Files generated successfully: " + problemsFilename + " and " + ratingsFilename);
        } catch (IOException e) {
            System.err.println("Error writing files: " + e.getMessage());
        }
    }

    private static String getRandomElement(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static void main(String[] args) {
        // Вызов генератора
        generateData(7, 5, 3);
    }
}
