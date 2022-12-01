package site.mokaform.surveyserver.repository.survey;

import site.mokaform.surveyserver.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyCustomRepository {

    Optional<Survey> findBySharingKey(String sharingKey);
}
