package site.mokaform.surveyserver.repository.survey;

import site.mokaform.surveyserver.dto.survey.mapping.SubmittedSurveyInfoMapping;
import site.mokaform.surveyserver.dto.survey.mapping.SurveyInfoMapping;
import site.mokaform.surveyserver.repository.enums.SurveySortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static site.mokaform.surveyserver.domain.answer.QAnswer.answer;
import static site.mokaform.surveyserver.common.util.QuerydslCustomUtils.nullSafeBooleanBuilder;
import static site.mokaform.surveyserver.domain.QQuestion.question;
import static site.mokaform.surveyserver.domain.QSurvey.survey;

@RequiredArgsConstructor
public class SurveyCustomRepositoryImpl implements SurveyCustomRepository {

    private static final String PAGEABLE_MUST_NOT_BE_NULL = "The given pageable must not be null!";
    private static final String USER_ID_MUST_NOT_BE_NULL = "The given user id must not be null!";
    private static final String SURVEY_ID_MUST_NOT_BE_NULL = "The given survey id must not be null!";

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SurveyInfoMapping> findSurveyInfos(Pageable pageable, Long userId) {
        checkPageable(pageable);

        List<SurveyInfoMapping> content = queryFactory
                .select(
                        Projections.fields(SurveyInfoMapping.class,
                                survey.surveyId,
                                survey.title,
                                survey.summary,
                                survey.startDate,
                                survey.endDate,
                                survey.isAnonymous,
                                survey.isPublic,
                                survey.sharingKey,
                                answer.userId.countDistinct().as("surveyeeCount")))
                .from(survey)
                .leftJoin(question).on(survey.surveyId.eq(question.survey.surveyId))
                .leftJoin(answer).on(question.questionId.eq(answer.question.questionId))
                .where(
                        survey.isDeleted.isFalse(),
                        filterMySurvey(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(survey.surveyId)
                .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(survey.countDistinct())
                .from(survey)
                .where(
                        survey.isDeleted.isFalse(),
                        filterMySurvey(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<SubmittedSurveyInfoMapping> findSubmittedSurveyInfos(Pageable pageable, Long userId) {
        checkPageable(pageable);
        checkUserId(userId);

        List<SubmittedSurveyInfoMapping> content = queryFactory
                .select(
                        Projections.fields(SubmittedSurveyInfoMapping.class,
                                survey.surveyId,
                                survey.title,
                                survey.summary,
                                survey.startDate,
                                survey.endDate,
                                survey.isAnonymous,
                                survey.isPublic,
                                survey.sharingKey,
                                survey.isDeleted))
                .from(answer)
                .leftJoin(question).on(answer.question.questionId.eq(question.questionId))
                .leftJoin(survey).on(question.survey.surveyId.eq(survey.surveyId))
                .where(
                        answer.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(survey.surveyId)
                .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(survey.surveyId.countDistinct())
                .from(answer)
                .leftJoin(question).on(answer.question.questionId.eq(question.questionId))
                .leftJoin(survey).on(question.survey.surveyId.eq(survey.surveyId))
                .where(
                        answer.userId.eq(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Long countSurveyee(Long surveyId) {
        checkSurveyId(surveyId);

        return queryFactory
                .select(answer.userId.countDistinct())
                .from(survey)
                .leftJoin(question).on(survey.surveyId.eq(question.survey.surveyId))
                .leftJoin(answer).on(question.questionId.eq(answer.question.questionId))
                .where(
                        survey.surveyId.eq(surveyId))
                .fetchOne();
    }

    private BooleanExpression filterMySurvey(Long userId) {
        if (Objects.isNull(userId)) {
            return survey.isPublic.isTrue()
                    .and(filterOngoingSurvey());
        } else {
            return survey.userId.eq(userId);
        }
    }

    private BooleanBuilder filterOngoingSurvey() {
        LocalDate now = LocalDate.now();
        return nullSafeBooleanBuilder(() ->
                survey.startDate.loe(now).and(survey.endDate.goe(now)));
    }

    private void checkPageable(Pageable pageable) {
        Assert.notNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
    }

    private void checkUserId(Long userId) {
        Assert.notNull(userId, USER_ID_MUST_NOT_BE_NULL);
    }

    private void checkSurveyId(Long surveyId) {
        Assert.notNull(surveyId, SURVEY_ID_MUST_NOT_BE_NULL);
    }

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return Collections.emptyList();
        }

        return pageable.getSort().stream()
                .map(order -> SurveySortType.getSortType(order.getProperty())
                        .getOrderSpecifier(order.getDirection().isAscending() ? Order.ASC : Order.DESC))
                .collect(Collectors.toList());
    }
}
