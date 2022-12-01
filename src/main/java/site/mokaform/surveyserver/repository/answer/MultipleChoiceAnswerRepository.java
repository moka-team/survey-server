package site.mokaform.surveyserver.repository.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mokaform.surveyserver.domain.answer.MultipleChoiceAnswer;

import java.util.Optional;

public interface MultipleChoiceAnswerRepository extends JpaRepository<MultipleChoiceAnswer, Long> {

    @Query("SELECT mca FROM MultipleChoiceAnswer mca WHERE mca.answer.answerId = :answerId")
    Optional<MultipleChoiceAnswer> findByAnswerId(@Param("answerId") Long answerId);
}
