package site.mokaform.surveyserver.dto.answer.response.stat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import site.mokaform.surveyserver.dto.answer.mapping.OXAnswerStatsMapping;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Getter
public class OXStat extends QuestionInfo {

    private final Long yes;

    private final Long no;

    @Builder
    public OXStat(Long questionIndex, String title,
                  Long yes, Long no) {
        super(questionIndex, title);
        this.yes = yes;
        this.no = no;
    }

    public OXStat(OXAnswerStatsMapping stat) {
        super(stat.getQuestionId(), stat.getTitle());
        this.yes = stat.getYesCount();
        this.no = stat.getNoCount();
    }
}
