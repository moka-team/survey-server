package site.mokaform.surveyserver.dto.answer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import site.mokaform.surveyserver.domain.answer.OXAnswer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Getter
public class OXAnswerResponse {

    private final Long oxAnswerId;

    private final Long questionId;

    private final Boolean isYes;

    public OXAnswerResponse(OXAnswer oxAnswer) {
        this.oxAnswerId = oxAnswer.getOxAnswerId();
        this.questionId = oxAnswer.getAnswer().getQuestion().getQuestionId();
        this.isYes = oxAnswer.getIsYes();
    }
}
