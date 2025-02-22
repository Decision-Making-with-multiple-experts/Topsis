package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Rating {
    private final String problemName;
    private final String alternativeName;
    private final int expertId;
    private final List<String> alternativeRatings;

    @JsonCreator
    public Rating(
            @JsonProperty("problemName") String problemName,
            @JsonProperty("alternativeName") String alternativeName,
            @JsonProperty("expertId") int expertId,
            @JsonProperty("alternativeRatings") List<String> alternativeRatings) {
        this.problemName = problemName;
        this.alternativeName = alternativeName;
        this.expertId = expertId;
        this.alternativeRatings = alternativeRatings;
    }

    public String getProblemName() {
        return problemName;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public int getExpertId() {
        return expertId;
    }

    public List<String> getAlternativeRatings() {
        return alternativeRatings;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Ошибка сериализации в JSON\"}";
        }
    }
}
