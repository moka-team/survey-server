package site.mokaform.surveyserver.repository.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mokaform.surveyserver.domain.answer.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long>, AnswerCustomRepository {
}
