package site.mokaform.surveyserver.dto.answer.response.stat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import site.mokaform.surveyserver.dto.answer.mapping.MultipleChoiceAnswerStatsMapping;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Getter
public class MultipleChoiceResult {

    private final String multiQuestionContent;

    private final Long multiQuestionContentCount;

    public MultipleChoiceResult(String multiQuestionContent, Long multiQuestionContentCount) {
        this.multiQuestionContent = multiQuestionContent;
        this.multiQuestionContentCount = multiQuestionContentCount;
    }

    public MultipleChoiceResult(MultipleChoiceAnswerStatsMapping stats) {
        this.multiQuestionContent = stats.getMultiQuestionContent();
        this.multiQuestionContentCount = stats.getMultiQuestionContentCount();
    }
}
