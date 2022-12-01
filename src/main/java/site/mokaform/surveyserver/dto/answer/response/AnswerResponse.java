package site.mokaform.surveyserver.dto.answer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Getter
public class AnswerResponse {

    private final Long surveyId;

    private final Long surveyeeId;

    public AnswerResponse(Long surveyId, Long surveyeeId) {
        this.surveyId = surveyId;
        this.surveyeeId = surveyeeId;
    }
}
