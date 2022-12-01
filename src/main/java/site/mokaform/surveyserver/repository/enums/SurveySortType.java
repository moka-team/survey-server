package site.mokaform.surveyserver.repository.enums;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

import java.util.Arrays;

import static site.mokaform.surveyserver.domain.answer.QAnswer.answer;
import static site.mokaform.surveyserver.domain.QSurvey.survey;

public enum SurveySortType {

    CREATED_AT("createdAt", survey.createdAt),
    SURVEYEE_COUNT("surveyeeCount", answer.userId.countDistinct());

    private final String property;
    private final Expression target;

    SurveySortType(String property, Expression target) {
        this.property = property;
        this.target = target;
    }

    public OrderSpecifier<?> getOrderSpecifier(Order direction) {
        return new OrderSpecifier(direction, this.target);
    }

    public static SurveySortType getSortType(String property) {
        return Arrays.stream(SurveySortType.values())
                .filter(sortType -> sortType.property.equals(property))
                .findAny()
                .get();

        // TODO: exception 추가
//                .orElseThrow(() ->
//                        new ApiException(SurveyErrorCode.INVALID_SORT_TYPE));
    }
}
