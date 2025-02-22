package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Problem {
    private final String problemName;
    private final List<Criterion> criteria;

    @JsonCreator
    public Problem(
            @JsonProperty("problemName") String problemName,
            @JsonProperty("criteria") List<Criterion> criteria) {
        this.problemName = problemName;
        this.criteria = criteria;
    }

    public String getProblemName() {
        return problemName;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Ошибка сериализации в JSON\"}";
        }
    }
}
