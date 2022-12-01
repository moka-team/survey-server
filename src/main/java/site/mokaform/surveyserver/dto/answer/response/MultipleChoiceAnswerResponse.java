package site.mokaform.surveyserver.dto.answer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import site.mokaform.surveyserver.domain.answer.MultipleChoiceAnswer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Getter
public class MultipleChoiceAnswerResponse {

    private final Long multipleChoiceAnswerId;

    private final Long questionId;

    private final Long multiQuestionId;

    public MultipleChoiceAnswerResponse(MultipleChoiceAnswer multipleChoiceAnswer) {
        this.multipleChoiceAnswerId = multipleChoiceAnswer.getMultipleChoiceAnswerId();
        this.questionId = multipleChoiceAnswer.getAnswer().getQuestion().getQuestionId();
        this.multiQuestionId = multipleChoiceAnswer.getMultipleChoiceQuestion().getMultiQuestionId();
    }
}
