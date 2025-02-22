package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Alternative {
    private String alternativeName;
    private String problemName;

    @JsonCreator
    public Alternative(
            @JsonProperty("alternativeName") String alternativeName,
            @JsonProperty("problemName") String problemName) {
        this.alternativeName = alternativeName;
        this.problemName = problemName;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public String getProblemName() {
        return problemName;
    }

    public String formatAlternative() {
        return "Alternative { " +
                "alternativeName: '" + alternativeName + "', " +
                "problemName: '" + problemName + "' }";
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Ошибка сериализации в JSON\"}";
        }
    }
}
