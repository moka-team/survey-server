package site.mokaform.surveyserver.dto.answer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import site.mokaform.surveyserver.domain.answer.EssayAnswer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Getter
public class EssayAnswerResponse {

    private Long essayAnswerId;

    private Long questionId;

    private String answerContent;

    public EssayAnswerResponse(EssayAnswer essayAnswer) {
        this.essayAnswerId = essayAnswer.getEssayAnswerId();
        this.questionId = essayAnswer.getAnswer().getQuestion().getQuestionId();
        this.answerContent = essayAnswer.getAnswerContent();
    }
}
