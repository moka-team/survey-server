package site.mokaform.surveyserver.repository.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mokaform.surveyserver.domain.answer.EssayAnswer;

import java.util.Optional;

public interface EssayAnswerRepository extends JpaRepository<EssayAnswer, Long> {

    @Query("SELECT ea FROM EssayAnswer ea WHERE ea.answer.answerId = :answerId")
    Optional<EssayAnswer> findByAnswerId(@Param("answerId") Long answerId);
}
