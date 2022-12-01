package site.mokaform.surveyserver.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mokaform.surveyserver.common.exception.ApiException;
import site.mokaform.surveyserver.common.exception.errorcode.CommonErrorCode;
import site.mokaform.surveyserver.common.exception.errorcode.ErrorCode;
import site.mokaform.surveyserver.common.exception.errorcode.SurveyErrorCode;
import site.mokaform.surveyserver.common.response.PageResponse;
import site.mokaform.surveyserver.domain.MultipleChoiceQuestion;
import site.mokaform.surveyserver.domain.Question;
import site.mokaform.surveyserver.domain.Survey;
import site.mokaform.surveyserver.domain.SurveyCategory;
import site.mokaform.surveyserver.domain.enums.Category;
import site.mokaform.surveyserver.dto.survey.mapping.SubmittedSurveyInfoMapping;
import site.mokaform.surveyserver.dto.survey.mapping.SurveyInfoMapping;
import site.mokaform.surveyserver.dto.survey.request.SurveyCreateRequest;
import site.mokaform.surveyserver.dto.survey.request.SurveyUpdateRequest;
import site.mokaform.surveyserver.dto.survey.response.*;
import site.mokaform.surveyserver.repository.survey.MultiChoiceQuestionRepository;
import site.mokaform.surveyserver.repository.survey.QuestionRepository;
import site.mokaform.surveyserver.repository.survey.SurveyCategoryRepository;
import site.mokaform.surveyserver.repository.survey.SurveyRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final MultiChoiceQuestionRepository multiChoiceQuestionRepository;
    private final SurveyCategoryRepository surveyCategoryRepository;

    public SurveyService(SurveyRepository surveyRepository,
                         QuestionRepository questionRepository,
                         MultiChoiceQuestionRepository multiChoiceQuestionRepository,
                         SurveyCategoryRepository surveyCategoryRepository) {
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.multiChoiceQuestionRepository = multiChoiceQuestionRepository;
        this.surveyCategoryRepository = surveyCategoryRepository;
    }

    @Transactional
    public SurveyCreateResponse createSurvey(SurveyCreateRequest request, Long userId) {
        Survey savedSurvey = saveSurvey(Survey.builder()
                .userId(userId)
                .title(request.getTitle())
                .summary(request.getSummary())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isAnonymous(request.getIsAnonymous())
                .isPublic(request.getIsPublic())
                .build());

        request.getCategories().forEach(category ->
                saveSurveyCategory(new SurveyCategory(category, savedSurvey)));

        request.getQuestions()
                .forEach(question -> {
                    Question savedQuestion = saveQuestion(
                            Question.builder()
                                    .survey(savedSurvey)
                                    .title(question.getTitle())
                                    .index(question.getIndex())
                                    .type(question.getType())
                                    .isMultiAnswer(question.getIsMultipleAnswer())
                                    .build()
                    );
                    if (question.getIsMultipleAnswer()) {
                        request.getMultiQuestions()
                                .stream()
                                .filter(m ->
                                        m.getQuestionIndex() == question.getIndex())
                                .forEach(m ->
                                        saveMultiChoiceQuestion(
                                                MultipleChoiceQuestion.builder()
                                                        .question(savedQuestion)
                                                        .multiQuestionType(m.getType())
                                                        .multiQuestionContent(m.getContent())
                                                        .multiQuestionIndex(m.getIndex())
                                                        .build())
                                );
                    }
                });

        return new SurveyCreateResponse(savedSurvey.getSurveyId(), savedSurvey.getSharingKey());
    }

    @Transactional(readOnly = true)
    public SurveyDetailsResponse getSurveyDetailsById(Long surveyId) {
        Survey survey = getSurveyById(surveyId);

        return getSurveyDetails(survey);
    }

    @Transactional(readOnly = true)
    public SurveyDetailsResponse getSurveyDetailsBySharingKey(String sharingKey) {
        Survey survey = getSurveyBySharingKey(sharingKey);

        return getSurveyDetails(survey);
    }

    @Transactional(readOnly = true)
    public PageResponse<SurveyInfoResponse> getSurveyInfos(Pageable pageable, Long userId) {
        Page<SurveyInfoMapping> surveyInfos = surveyRepository.findSurveyInfos(pageable, userId);
        return new PageResponse<>(
                surveyInfos.map(surveyInfo ->
                        new SurveyInfoResponse(surveyInfo, getSurveyCategories(surveyInfo.getSurveyId()))));
    }

    @Transactional(readOnly = true)
    public PageResponse<SubmittedSurveyInfoResponse> getSubmittedSurveyInfos(Pageable pageable, Long userId) {
        Page<SubmittedSurveyInfoMapping> surveyInfos = surveyRepository.findSubmittedSurveyInfos(pageable, userId);

        if ("surveyeeCount".equals(pageable.getSort().get().findFirst().get().getProperty())) {
            Comparator<SubmittedSurveyInfoResponse> comparator = null;

            if ("ASC".equals(pageable.getSort().get().findFirst().get().getDirection().name())) {
                comparator = Comparator.comparing(SubmittedSurveyInfoResponse::getSurveyeeCount);
            } else {
                comparator = Comparator.comparing(SubmittedSurveyInfoResponse::getSurveyeeCount).reversed();
            }

            List<SubmittedSurveyInfoResponse> content = surveyInfos.stream()
                    .map(submittedSurveyInfo ->
                            SubmittedSurveyInfoResponse.builder()
                                    .surveyInfoMapping(submittedSurveyInfo)
                                    .surveyeeCount(surveyRepository.countSurveyee(submittedSurveyInfo.getSurveyId()))
                                    .surveyCategory(getSurveyCategories(submittedSurveyInfo.getSurveyId()))
                                    .build())
                    .sorted(comparator)
                    .collect(Collectors.toList());
            Page<SubmittedSurveyInfoResponse> responsePage = new PageImpl<>(content, surveyInfos.getPageable(), surveyInfos.getTotalElements());
            return new PageResponse<>(responsePage);
        }

        return new PageResponse<>(
                surveyInfos.map(submittedSurveyInfo ->
                        SubmittedSurveyInfoResponse.builder()
                                .surveyInfoMapping(submittedSurveyInfo)
                                .surveyeeCount(surveyRepository.countSurveyee(submittedSurveyInfo.getSurveyId()))
                                .surveyCategory(getSurveyCategories(submittedSurveyInfo.getSurveyId()))
                                .build()));
    }

    @Transactional
    public SurveyDeleteResponse deleteSurvey(Long surveyId, Long userId) {
        Survey survey = getSurveyById(surveyId);
        validateUserAuthority(userId, survey, SurveyErrorCode.NO_PERMISSION_TO_DELETE_SURVEY);
        survey.updateIsDeleted(true);
        return new SurveyDeleteResponse(survey.getSurveyId());
    }

    @Transactional
    public void updateSurveyInfoAndQuestions(Long surveyId, Long userId, SurveyUpdateRequest request) {
        Survey survey = getSurveyById(surveyId);
        validateUserAuthority(userId, survey, SurveyErrorCode.NO_PERMISSION_TO_UPDATE_SURVEY);

        /*
         * 설문 정보 업데이트
         */
        survey.updateTitle(request.getTitle());
        survey.updateSummary(request.getSummary());
        survey.updateStartDate(request.getStartDate());
        survey.updateEndDate(request.getEndDate());
        survey.updateAnonymous(request.getIsAnonymous());
        survey.updatePublic(request.getIsPublic());

        /*
         * 설문 카테고리 업데이트
         */
        List<Category> categoriesToUpdate = request.getCategories();
        List<SurveyCategory> existingSurveyCategories = getSurveyCategories(surveyId);
        existingSurveyCategories.forEach(category -> {
            Optional<Category> categoryToUpdate = categoriesToUpdate
                    .stream()
                    .filter(c -> c.equals(category.getCategory()))
                    .findFirst();
            categoryToUpdate.ifPresentOrElse(
                    c -> categoriesToUpdate.remove(c),
                    () -> {
                        category.unsetSurvey();
                        surveyCategoryRepository.delete(category);
                    }
            );
        });
        categoriesToUpdate.forEach(category ->
                saveSurveyCategory(new SurveyCategory(category, survey)));

        /*
         * 질문 업데이트
         */
        List<Question> existingQuestions = getQuestions(survey.getSurveyId());
        List<MultipleChoiceQuestion> existingMultiQuestions = existingQuestions.stream()
                .filter(Question::getIsMultiAnswer)
                .map(question ->
                        getMultipleChoiceQuestions(question.getQuestionId()))
                .collect(ArrayList::new, List::addAll, List::addAll);

        List<SurveyUpdateRequest.MultiQuestion> multiQuestionsToUpdate = request.getMultiQuestions();
        existingMultiQuestions.forEach(multiQuestion -> {
            Optional<SurveyUpdateRequest.MultiQuestion> multiQuestionToUpdate = multiQuestionsToUpdate
                    .stream()
                    .filter(m -> m.getMultiQuestionId() == multiQuestion.getMultiQuestionId())
                    .findFirst();
            multiQuestionToUpdate.ifPresentOrElse(
                    m -> multiQuestion.updateMultipleChoiceQuestion(m.getMultiQuestionType(), m.getMultiQuestionContent(), m.getQuestionIndex()),
                    () -> {
                        multiQuestion.unsetQuestion();
                        multiChoiceQuestionRepository.delete(multiQuestion);
                    }
            );
        });

        List<SurveyUpdateRequest.Question> questionsToUpdate = request.getQuestions();
        existingQuestions.forEach(question -> {
            Optional<SurveyUpdateRequest.Question> questionToUpdate = questionsToUpdate
                    .stream()
                    .filter(q -> q.getQuestionId() == question.getQuestionId())
                    .findFirst();
            questionToUpdate.ifPresentOrElse(
                    q -> {
                        question.updateQuestion(q.getIndex(), q.getTitle(), q.getType(), q.getIsMultipleAnswer());
                        if (question.getIsMultiAnswer()) {
                            multiQuestionsToUpdate
                                    .stream()
                                    .filter(multiQuestion ->
                                            multiQuestion.getQuestionIndex() == question.getIndex()
                                                    && multiQuestion.getMultiQuestionId() == null)
                                    .forEach(multiQuestion ->
                                            saveMultiChoiceQuestion(
                                                    MultipleChoiceQuestion.builder()
                                                            .question(question)
                                                            .multiQuestionType(multiQuestion.getMultiQuestionType())
                                                            .multiQuestionContent(multiQuestion.getMultiQuestionContent())
                                                            .multiQuestionIndex(multiQuestion.getQuestionIndex())
                                                            .build())
                                    );
                        }
                    },
                    () -> questionRepository.delete(question));
        });

        questionsToUpdate
                .stream()
                .filter(question ->
                        question.getQuestionId() == null)
                .forEach(question -> {
                    Question savedQuestion = saveQuestion(Question.builder()
                            .survey(survey)
                            .title(question.getTitle())
                            .index(question.getIndex())
                            .type(question.getType())
                            .isMultiAnswer(question.getIsMultipleAnswer())
                            .build());

                    if (question.getIsMultipleAnswer()) {
                        multiQuestionsToUpdate
                                .stream()
                                .filter(multiQuestion ->
                                        multiQuestion.getQuestionIndex() == question.getIndex()
                                                && multiQuestion.getMultiQuestionId() == null)
                                .forEach(multiQuestion ->
                                        saveMultiChoiceQuestion(
                                                MultipleChoiceQuestion.builder()
                                                        .question(savedQuestion)
                                                        .multiQuestionType(multiQuestion.getMultiQuestionType())
                                                        .multiQuestionContent(multiQuestion.getMultiQuestionContent())
                                                        .multiQuestionIndex(multiQuestion.getQuestionIndex())
                                                        .build())
                                );
                    }
                });
    }

    private SurveyDetailsResponse getSurveyDetails(Survey survey) {
        List<SurveyCategory> surveyCategories = getSurveyCategories(survey.getSurveyId());
        List<Question> questions = getQuestions(survey.getSurveyId());
        ArrayList<MultipleChoiceQuestion> multiQuestions = questions.stream()
                .filter(Question::getIsMultiAnswer)
                .map(question ->
                        getMultipleChoiceQuestions(question.getQuestionId()))
                .collect(ArrayList::new, List::addAll, List::addAll);

        return SurveyDetailsResponse.builder()
                .survey(survey)
                .surveyCategories(surveyCategories)
                .questions(questions)
                .multipleChoiceQuestions(multiQuestions)
                .build();
    }

    private Survey saveSurvey(Survey survey) {
        Survey savedSurvey = surveyRepository.save(survey);
        return savedSurvey;
    }

    private Question saveQuestion(Question question) {
        Question savedQuestion = questionRepository.save(question);
        return savedQuestion;
    }

    private void saveMultiChoiceQuestion(MultipleChoiceQuestion multipleChoiceQuestion) {
        multiChoiceQuestionRepository.save(multipleChoiceQuestion);
    }

    private void saveSurveyCategory(SurveyCategory surveyCategory) {
        surveyCategoryRepository.save(surveyCategory);
    }

    private Survey getSurveyById(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private Survey getSurveyBySharingKey(String sharingKey) {
        return surveyRepository.findBySharingKey(sharingKey)
                .orElseThrow(() -> new ApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private List<Question> getQuestions(Long surveyId) {
        Survey survey = getSurveyById(surveyId);

        return questionRepository.findQuestionsBySurvey(survey);
    }

    private List<MultipleChoiceQuestion> getMultipleChoiceQuestions(Long questionId) {
        Question question = getQuestionById(questionId);

        return multiChoiceQuestionRepository.findMultipleChoiceQuestionsByQuestion(question);
    }

    private List<SurveyCategory> getSurveyCategories(Long surveyId) {
        Survey survey = getSurveyById(surveyId);

        return surveyCategoryRepository.findSurveyCategoriesBySurvey(survey);
    }

    private void validateUserAuthority(Long userId, Survey survey, ErrorCode errorCode) {
        if (userId != survey.getUserId()) {
            throw new ApiException(errorCode);
        }
    }
}
