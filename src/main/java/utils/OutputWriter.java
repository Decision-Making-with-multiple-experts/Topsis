package utils;

import models.Problem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OutputWriter {
    private static final String OUTPUT_FILE = "src/main/resources/output.txt";

    /**
     * Записывает путь к файлу ресурсов перед вычислениями.
     */
    public static void writeResourcePath(String resourcePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE), true))) {
            writer.write("resources path: " + resourcePath);
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing resource path: " + e.getMessage());
        }
    }

    /**
     * Записывает результаты ранжирования в файл.
     */
    public static void writeResults(List<Map.Entry<String, Double>> results, Problem problem) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE), true))) {
            writer.write("Ranked Alternatives for " + problem.getProblemName() +" (Descending h(Ai)):");
            writer.newLine();

            for (int i = 0; i < results.size(); i++) {
                String formattedValue = String.format("%.2f", results.get(i).getValue()).replace(".", ","); // Округляем до сотых
                String line = (i + 1) + ". " + results.get(i).getKey() + " -> h(Ai): " + formattedValue;
                writer.write(line);
                writer.newLine();
            }
            writer.newLine();
            System.out.println("Results successfully saved to " + OUTPUT_FILE);

        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }

    public static void writeEnd() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE), true))) {
            writer.write("----------------------------------------------------------------");
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }
}
