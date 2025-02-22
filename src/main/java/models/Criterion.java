package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Criterion {
    private final String criterionName;
    private final String criterionScale;
    private final String optimizationDirection;

    @JsonCreator
    public Criterion(
            @JsonProperty("criterionName") String criterionName,
            @JsonProperty("criterionScale") String criterionScale,
            @JsonProperty("optimizationDirection") String optimizationDirection) {
        this.criterionName = criterionName;
        this.criterionScale = criterionScale;
        this.optimizationDirection = optimizationDirection;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public String getCriterionScale() {
        return criterionScale;
    }

    public String getOptimizationDirection() {
        return optimizationDirection;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Ошибка сериализации в JSON\"}";
        }
    }
}
