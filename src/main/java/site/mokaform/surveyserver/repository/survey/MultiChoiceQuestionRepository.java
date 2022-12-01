package site.mokaform.surveyserver.repository.survey;

import site.mokaform.surveyserver.domain.MultipleChoiceQuestion;
import site.mokaform.surveyserver.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultiChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {

    List<MultipleChoiceQuestion> findMultipleChoiceQuestionsByQuestion(Question question);
}
