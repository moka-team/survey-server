package site.mokaform.surveyserver.repository.survey;

import site.mokaform.surveyserver.domain.Question;
import site.mokaform.surveyserver.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findQuestionsBySurvey(Survey survey);
}
