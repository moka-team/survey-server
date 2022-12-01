package site.mokaform.surveyserver.repository.survey;

import site.mokaform.surveyserver.domain.Survey;
import site.mokaform.surveyserver.domain.SurveyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyCategoryRepository extends JpaRepository<SurveyCategory, Long> {

    List<SurveyCategory> findSurveyCategoriesBySurvey(Survey survey);
}
