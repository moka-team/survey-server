package site.mokaform.surveyserver.repository.answer;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import site.mokaform.surveyserver.dto.answer.mapping.AnswerInfoMapping;
import site.mokaform.surveyserver.dto.answer.mapping.EssayAnswerStatsMapping;
import site.mokaform.surveyserver.dto.answer.mapping.MultipleChoiceAnswerStatsMapping;
import site.mokaform.surveyserver.dto.answer.mapping.OXAnswerStatsMapping;

import java.util.List;

import static site.mokaform.surveyserver.domain.QQuestion.question;
import static site.mokaform.surveyserver.domain.QSurvey.survey;
import static site.mokaform.surveyserver.domain.answer.QAnswer.answer;
import static site.mokaform.surveyserver.domain.answer.QEssayAnswer.essayAnswer;
import static site.mokaform.surveyserver.domain.answer.QMultipleChoiceAnswer.multipleChoiceAnswer;
import static site.mokaform.surveyserver.domain.answer.QOXAnswer.oXAnswer;

@RequiredArgsConstructor
public class AnswerCustomRepositoryImpl implements AnswerCustomRepository {

    private static final String SURVEY_ID_MUST_NOT_BE_NULL = "The given survey id must not be null!";
    private static final String USER_ID_MUST_NOT_BE_NULL = "The given user id must not be null!";

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AnswerInfoMapping> findAnswerInfos(Long surveyId, Long userId) {
        checkSurveyId(surveyId);
        checkUserId(userId);

        return queryFactory
                .select(
                        Projections.fields(AnswerInfoMapping.class,
                                answer.answerId,
                                answer.question.questionId))
                .from(survey)
                .leftJoin(question).on(survey.surveyId.eq(question.survey.surveyId))
                .leftJoin(answer).on(question.questionId.eq(answer.question.questionId))
                .where(
                        survey.surveyId.eq(surveyId),
                        answer.userId.eq(userId))
                .fetch();
    }

    @Override
    public List<EssayAnswerStatsMapping> findEssayAnswers(Long surveyId) {
        checkSurveyId(surveyId);

        return queryFactory
                .select(
                        Projections.fields(EssayAnswerStatsMapping.class,
                                question.questionId,
                                question.index
                                        .as("questionIndex"),
                                question.title,
                                essayAnswer.answerContent))
                .from(survey)
                .leftJoin(question).on(survey.surveyId.eq(question.survey.surveyId))
                .leftJoin(answer).on(question.questionId.eq(answer.question.questionId))
                .leftJoin(essayAnswer).on(answer.answerId.eq(essayAnswer.answer.answerId))
                .where(
                        survey.surveyId.eq(surveyId),
                        essayAnswer.essayAnswerId.isNotNull())
                .fetch();
    }

    @Override
    public List<MultipleChoiceAnswerStatsMapping> findMultipleChoiceAnswers(Long surveyId) {
        checkSurveyId(surveyId);

        return queryFactory
                .select(
                        Projections.fields(MultipleChoiceAnswerStatsMapping.class,
                                question.questionId,
                                question.index
                                        .as("questionIndex"),
                                question.title,
                                multipleChoiceAnswer.multipleChoiceQuestion.multiQuestionContent,
                                multipleChoiceAnswer.multipleChoiceAnswerId
                                        .countDistinct()
                                        .as("multiQuestionContentCount")))
                .from(survey)
                .leftJoin(question).on(survey.surveyId.eq(question.survey.surveyId))
                .leftJoin(answer).on(question.questionId.eq(answer.question.questionId))
                .leftJoin(multipleChoiceAnswer).on(answer.answerId.eq(multipleChoiceAnswer.answer.answerId))
                .where(
                        survey.surveyId.eq(surveyId))
                .groupBy(question.questionId, multipleChoiceAnswer.multipleChoiceQuestion.multiQuestionId)
                .fetch();
    }

    @Override
    public List<OXAnswerStatsMapping> findOxAnswers(Long surveyId) {
        checkSurveyId(surveyId);

        return queryFactory
                .select(
                        Projections.fields(OXAnswerStatsMapping.class,
                                question.questionId,
                                question.index
                                        .as("questionIndex"),
                                question.title,
                                new CaseBuilder()
                                        .when(oXAnswer.isYes.isTrue())
                                        .then(1L)
                                        .otherwise(0L)
                                        .sum()
                                        .as("yesCount"),
                                new CaseBuilder()
                                        .when(oXAnswer.isYes.isFalse())
                                        .then(1L)
                                        .otherwise(0L)
                                        .sum()
                                        .as("noCount")))
                .from(survey)
                .leftJoin(question).on(survey.surveyId.eq(question.survey.surveyId))
                .leftJoin(answer).on(question.questionId.eq(answer.question.questionId))
                .leftJoin(oXAnswer).on(answer.answerId.eq(oXAnswer.answer.answerId))
                .where(
                        survey.surveyId.eq(surveyId),
                        oXAnswer.oxAnswerId.isNotNull())
                .groupBy(question.questionId)
                .fetch();
    }


    private void checkSurveyId(Long surveyId) {
        Assert.notNull(surveyId, SURVEY_ID_MUST_NOT_BE_NULL);
    }

    private void checkUserId(Long userId) {
        Assert.notNull(userId, USER_ID_MUST_NOT_BE_NULL);
    }

}
