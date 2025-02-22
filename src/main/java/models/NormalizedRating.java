package models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class NormalizedRating {
    private String problemName;
    private String alternativeName;
    private int expertId;
    private List<Double> normalizedRatings;

    public NormalizedRating(String problemName, String alternativeName, int expertId, List<Double> normalizedRatings) {
        this.problemName = problemName;
        this.alternativeName = alternativeName;
        this.expertId = expertId;
        this.normalizedRatings = normalizedRatings;
    }

    public String getProblemName() {
        return problemName;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public int getExpertId() { return expertId; }

    public List<Double> getNormalizedRatings() {
        return normalizedRatings;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Ошибка сериализации в JSON\"}";
        }
    }
}

