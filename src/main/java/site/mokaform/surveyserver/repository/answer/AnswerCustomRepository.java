package site.mokaform.surveyserver.repository.answer;

import site.mokaform.surveyserver.dto.answer.mapping.AnswerInfoMapping;
import site.mokaform.surveyserver.dto.answer.mapping.EssayAnswerStatsMapping;
import site.mokaform.surveyserver.dto.answer.mapping.MultipleChoiceAnswerStatsMapping;
import site.mokaform.surveyserver.dto.answer.mapping.OXAnswerStatsMapping;

import java.util.List;

public interface AnswerCustomRepository {

    List<AnswerInfoMapping> findAnswerInfos(Long surveyId, Long userId);

    List<EssayAnswerStatsMapping> findEssayAnswers(Long surveyId);

    List<MultipleChoiceAnswerStatsMapping> findMultipleChoiceAnswers(Long surveyId);

    List<OXAnswerStatsMapping> findOxAnswers(Long surveyId);
}
