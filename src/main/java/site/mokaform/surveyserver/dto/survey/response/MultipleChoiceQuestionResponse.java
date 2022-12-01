package site.mokaform.surveyserver.dto.survey.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import site.mokaform.surveyserver.domain.MultipleChoiceQuestion;
import site.mokaform.surveyserver.domain.enums.MultiQuestionType;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Getter
public class MultipleChoiceQuestionResponse {

    private final Long multiQuestionId;
    private final Long questionId;
    private final Long questionIndex;
    private final Long multiQuestionIndex;
    private final String multiQuestionContent;
    private final MultiQuestionType multiQuestionType;

    public MultipleChoiceQuestionResponse(MultipleChoiceQuestion multipleChoiceQuestion) {
        this.multiQuestionId = multipleChoiceQuestion.getMultiQuestionId();
        this.questionId = multipleChoiceQuestion.getQuestion().getQuestionId();
        this.questionIndex = multipleChoiceQuestion.getQuestion().getIndex();
        this.multiQuestionIndex = multipleChoiceQuestion.getMultiQuestionIndex();
        this.multiQuestionContent = multipleChoiceQuestion.getMultiQuestionContent();
        this.multiQuestionType = multipleChoiceQuestion.getMultiQuestionType();
    }
}
