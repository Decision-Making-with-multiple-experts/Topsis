package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Alternative;
import models.Problem;
import models.Rating;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class JSONReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Alternative> readAlternatives(String fileName) {
        try {
            JsonNode rootNode = readJsonFile(fileName);
            return objectMapper.readerForListOf(Alternative.class)
                    .readValue(rootNode.get("alternatives"));
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла " + fileName + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static List<Problem> readProblems(String fileName) {
        try {
            JsonNode rootNode = readJsonFile(fileName);
            return objectMapper.readerForListOf(Problem.class)
                    .readValue(rootNode.get("problems"));
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла " + fileName + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static List<Rating> readRatings(String fileName) {
        try {
            JsonNode rootNode = readJsonFile(fileName);
            return objectMapper.readerForListOf(Rating.class)
                    .readValue(rootNode.get("ratings"));
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла " + fileName + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private static JsonNode readJsonFile(String fileName) throws IOException {
        InputStream inputStream = JSONReader.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IOException("Файл " + fileName + " не найден в resources!");
        }
        return objectMapper.readTree(inputStream);
    }
}
