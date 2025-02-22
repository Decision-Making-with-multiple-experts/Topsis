package utils;

import models.Criterion;
import models.NormalizedRating;
import models.Problem;
import models.Rating;

import java.util.*;

public class RatingNormalizer {
    public static List<NormalizedRating> normalizeRatings(List<Rating> ratings, Problem problem) {
        List<NormalizedRating> normalizedRatings = new ArrayList<>();

        for (Rating rating : ratings) {
            if (!rating.getProblemName().equals(problem.getProblemName())) {
                continue; // Пропускаем рейтинги, не относящиеся к данной проблеме
            }

            List<String> alternativeRatings = rating.getAlternativeRatings();
            List<Criterion> criteria = problem.getCriteria();

            if (alternativeRatings.size() != criteria.size()) {
                System.err.println("Несоответствие количества оценок критериям для альтернативы: " + rating.getAlternativeName());
                continue;
            }

            List<Double> normalizedValues = new ArrayList<>();

            for (int i = 0; i < alternativeRatings.size(); i++) {
                String value = alternativeRatings.get(i);
                String scaleType = criteria.get(i).getCriterionScale();
                double normalizedValue = ScaleConverter.convert(scaleType, value);
                normalizedValues.add(normalizedValue);
            }

            normalizedRatings.add(new NormalizedRating(
                    rating.getProblemName(),
                    rating.getAlternativeName(),
                    rating.getExpertId(),
                    normalizedValues)
            );
        }

        return normalizedRatings;
    }
}
