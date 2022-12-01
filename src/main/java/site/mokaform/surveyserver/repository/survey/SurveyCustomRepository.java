package site.mokaform.surveyserver.repository.survey;

import site.mokaform.surveyserver.dto.survey.mapping.SubmittedSurveyInfoMapping;
import site.mokaform.surveyserver.dto.survey.mapping.SurveyInfoMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyCustomRepository {

    Page<SurveyInfoMapping> findSurveyInfos(Pageable pageable, Long userId);

    Page<SubmittedSurveyInfoMapping> findSubmittedSurveyInfos(Pageable pageable, Long userId);

    Long countSurveyee(Long surveyId);
}
