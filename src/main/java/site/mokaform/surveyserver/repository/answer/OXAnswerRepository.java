package site.mokaform.surveyserver.repository.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mokaform.surveyserver.domain.answer.OXAnswer;

import java.util.Optional;

public interface OXAnswerRepository extends JpaRepository<OXAnswer, Long> {

    @Query("SELECT oxa FROM OXAnswer oxa WHERE oxa.answer.answerId = :answerId")
    Optional<OXAnswer> findByAnswerId(@Param("answerId") Long answerId);
}
