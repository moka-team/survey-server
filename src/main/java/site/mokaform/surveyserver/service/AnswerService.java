package site.mokaform.surveyserver.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mokaform.surveyserver.common.exception.ApiException;
import site.mokaform.surveyserver.common.exception.errorcode.CommonErrorCode;
import site.mokaform.surveyserver.common.exception.errorcode.SurveyErrorCode;
import site.mokaform.surveyserver.domain.MultipleChoiceQuestion;
import site.mokaform.surveyserver.domain.Question;
import site.mokaform.surveyserver.domain.Survey;
import site.mokaform.surveyserver.domain.answer.Answer;
import site.mokaform.surveyserver.domain.answer.EssayAnswer;
import site.mokaform.surveyserver.domain.answer.MultipleChoiceAnswer;
import site.mokaform.surveyserver.domain.answer.OXAnswer;
import site.mokaform.surveyserver.dto.answer.mapping.AnswerInfoMapping;
import site.mokaform.surveyserver.dto.answer.mapping.EssayAnswerStatsMapping;
import site.mokaform.surveyserver.dto.answer.mapping.MultipleChoiceAnswerStatsMapping;
import site.mokaform.surveyserver.dto.answer.mapping.OXAnswerStatsMapping;
import site.mokaform.surveyserver.dto.answer.request.AnswerCreateRequest;
import site.mokaform.surveyserver.dto.answer.response.AnswerDetailResponse;
import site.mokaform.surveyserver.dto.answer.response.stat.*;
import site.mokaform.surveyserver.repository.answer.AnswerRepository;
import site.mokaform.surveyserver.repository.answer.EssayAnswerRepository;
import site.mokaform.surveyserver.repository.answer.MultipleChoiceAnswerRepository;
import site.mokaform.surveyserver.repository.answer.OXAnswerRepository;
import site.mokaform.surveyserver.repository.survey.MultiChoiceQuestionRepository;
import site.mokaform.surveyserver.repository.survey.QuestionRepository;
import site.mokaform.surveyserver.repository.survey.SurveyRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final EssayAnswerRepository essayAnswerRepository;
    private final MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    private final OXAnswerRepository oxAnswerRepository;
    private final QuestionRepository questionRepository;
    private final MultiChoiceQuestionRepository multiChoiceQuestionRepository;
    private final SurveyRepository surveyRepository;

    public AnswerService(AnswerRepository answerRepository,
                         EssayAnswerRepository essayAnswerRepository,
                         MultipleChoiceAnswerRepository multipleChoiceAnswerRepository,
                         OXAnswerRepository oxAnswerRepository,
                         QuestionRepository questionRepository,
                         MultiChoiceQuestionRepository multiChoiceQuestionRepository,
                         SurveyRepository surveyRepository) {
        this.answerRepository = answerRepository;
        this.essayAnswerRepository = essayAnswerRepository;
        this.multipleChoiceAnswerRepository = multipleChoiceAnswerRepository;
        this.oxAnswerRepository = oxAnswerRepository;
        this.questionRepository = questionRepository;
        this.multiChoiceQuestionRepository = multiChoiceQuestionRepository;
        this.surveyRepository = surveyRepository;
    }

    @Transactional
    public void createAnswer(AnswerCreateRequest request, Long userId) {
        request.getEssayAnswers()
                .forEach(answer -> {
                            Answer savedAnswer = saveAnswer(Answer.builder()
                                    .userId(userId)
                                    .question(getQuestion(answer.getQuestionId()))
                                    .build());

                            saveEssayAnswer(
                                    EssayAnswer.builder()
                                            .answer(savedAnswer)
                                            .answerContent(answer.getAnswerContent())
                                            .build());
                        }

                );

        request.getMultipleChoiceAnswers()
                .forEach(answer -> {
                    Answer savedAnswer = saveAnswer(Answer.builder()
                            .userId(userId)
                            .question(getQuestion(answer.getQuestionId()))
                            .build());

                    MultipleChoiceQuestion multipleChoiceQuestion = getMultipleChoiceQuestion(answer.getMultiQuestionId());

                    saveMultipleChoiceAnswer(
                            MultipleChoiceAnswer.builder()
                                    .answer(savedAnswer)
                                    .multipleChoiceQuestion(multipleChoiceQuestion)
                                    .build()
                    );
                });

        request.getOxAnswers()
                .forEach(answer -> {
                            Answer savedAnswer = saveAnswer(Answer.builder()
                                    .userId(userId)
                                    .question(getQuestion(answer.getQuestionId()))
                                    .build());

                            saveOXAnswer(
                                    OXAnswer.builder()
                                            .answer(savedAnswer)
                                            .isYes(answer.getIsYes())
                                            .build()
                            );
                        }
                );

    }

    @Transactional(readOnly = true)
    public AnswerDetailResponse getAnswerDetail(String sharingKey, Long userId) {
        Survey survey = getSurveyBySharingKey(sharingKey);

        List<AnswerInfoMapping> answerInfos = answerRepository.findAnswerInfos(survey.getSurveyId(), userId);

        List<EssayAnswer> essayAnswers = new ArrayList<>();
        List<MultipleChoiceAnswer> multipleChoiceAnswers = new ArrayList<>();
        List<OXAnswer> oxAnswers = new ArrayList<>();
        answerInfos.forEach(answerInfo -> {
            getEssayAnswer(answerInfo.getAnswerId())
                    .ifPresent(essayAnswer -> essayAnswers.add(essayAnswer));
            getMultipleChoiceAnswer(answerInfo.getAnswerId())
                    .ifPresent(multipleChoiceAnswer -> multipleChoiceAnswers.add(multipleChoiceAnswer));
            getOxAnswer(answerInfo.getAnswerId())
                    .ifPresent(oxAnswer -> oxAnswers.add(oxAnswer));
        });

        return AnswerDetailResponse.builder()
                .surveyId(survey.getSurveyId())
                .surveyeeId(userId)
                .essayAnswers(essayAnswers)
                .multipleChoiceAnswers(multipleChoiceAnswers)
                .oxAnswers(oxAnswers)
                .build();
    }

    public AnswerStatsResponse getAnswerStats(Long surveyId, Long userId) {
        Survey survey = getSurveyBySurveyId(surveyId);

        validateUserAuthority(userId, survey);

        List<EssayAnswerStatsMapping> essayAnswers = answerRepository.findEssayAnswers(surveyId);
        List<MultipleChoiceAnswerStatsMapping> multipleChoiceAnswers = answerRepository.findMultipleChoiceAnswers(surveyId);
        List<OXAnswerStatsMapping> oxAnswers = answerRepository.findOxAnswers(surveyId);

        /**
         * 질문(question)별로 Map으로 묶어줌
         */
        Map<Long, List<EssayAnswerStatsMapping>> essayStatMap = new HashMap<>();
        essayAnswers.forEach(essayAnswer -> {
            if (essayStatMap.containsKey(essayAnswer.getQuestionId())) {
                List<EssayAnswerStatsMapping> value = essayStatMap.get(essayAnswer.getQuestionId());
                value.add(essayAnswer);
                essayStatMap.replace(essayAnswer.getQuestionId(), value);
            } else {
                ArrayList<EssayAnswerStatsMapping> list = new ArrayList<>();
                list.add(essayAnswer);
                essayStatMap.put(essayAnswer.getQuestionId(), list);
            }
        });

        Map<Long, List<MultipleChoiceAnswerStatsMapping>> multipleChoiceStatMap = new HashMap<>();
        multipleChoiceAnswers.forEach(multipleChoiceAnswer -> {
            if (multipleChoiceStatMap.containsKey(multipleChoiceAnswer.getQuestionId())) {
                List<MultipleChoiceAnswerStatsMapping> value = multipleChoiceStatMap.get(multipleChoiceAnswer.getQuestionId());
                value.add(multipleChoiceAnswer);
                multipleChoiceStatMap.replace(multipleChoiceAnswer.getQuestionId(), value);
            } else {
                ArrayList<MultipleChoiceAnswerStatsMapping> list = new ArrayList<>();
                list.add(multipleChoiceAnswer);
                multipleChoiceStatMap.put(multipleChoiceAnswer.getQuestionId(), list);
            }
        });


        /**
         * Response 포맷에 맞게 Map -> List로 변형
         */
        List<EssayStat> essayStats = essayStatMap.entrySet().stream()
                .map(entry -> {
                    EssayAnswerStatsMapping questionInfo = entry.getValue().get(0);
                    return EssayStat.builder()
                            .questionIndex(questionInfo.getQuestionIndex())
                            .title(questionInfo.getTitle())
                            .answerContents(entry.getValue().stream()
                                    .map(EssayAnswerStatsMapping::getAnswerContent)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());

        List<MultipleChoiceStat> multipleChoiceStats = multipleChoiceStatMap.entrySet().stream()
                .map(entry -> {
                    MultipleChoiceAnswerStatsMapping questionInfo = entry.getValue().get(0);
                    return MultipleChoiceStat.builder()
                            .questionIndex(questionInfo.getQuestionIndex())
                            .title(questionInfo.getTitle())
                            .results(entry.getValue().stream()
                                    .map(MultipleChoiceResult::new)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());

        List<OXStat> oxStats = oxAnswers.stream().map(OXStat::new).collect(Collectors.toList());

        return AnswerStatsResponse.builder()
                .essayStats(essayStats)
                .multipleChoiceStats(multipleChoiceStats)
                .oxStats(oxStats)
                .build();
    }

    private Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new ApiException(CommonErrorCode.INVALID_PARAMETER));
    }

    private MultipleChoiceQuestion getMultipleChoiceQuestion(Long multiQuestionId) {
        return multiChoiceQuestionRepository.findById(multiQuestionId)
                .orElseThrow(() ->
                        new ApiException(CommonErrorCode.INVALID_PARAMETER));
    }

    private Answer saveAnswer(Answer answer) {
        Answer savedAnswer = answerRepository.save(answer);
        return savedAnswer;
    }

    private void saveEssayAnswer(EssayAnswer essayAnswer) {
        essayAnswerRepository.save(essayAnswer);
    }

    private void saveMultipleChoiceAnswer(MultipleChoiceAnswer multipleChoiceAnswer) {
        multipleChoiceAnswerRepository.save(multipleChoiceAnswer);
    }

    private void saveOXAnswer(OXAnswer oxAnswer) {
        oxAnswerRepository.save(oxAnswer);
    }

    private Optional<EssayAnswer> getEssayAnswer(Long answerId) {
        return essayAnswerRepository.findByAnswerId(answerId);
    }

    private Optional<MultipleChoiceAnswer> getMultipleChoiceAnswer(Long answerId) {
        return multipleChoiceAnswerRepository.findByAnswerId(answerId);
    }

    private Optional<OXAnswer> getOxAnswer(Long answerId) {
        return oxAnswerRepository.findByAnswerId(answerId);
    }

    private Survey getSurveyBySurveyId(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ApiException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }

    private Survey getSurveyBySharingKey(String sharingKey) {
        return surveyRepository.findBySharingKey(sharingKey)
                .orElseThrow(() -> new ApiException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }

    private void validateUserAuthority(Long userId, Survey survey) {
        if (userId != survey.getUserId()) {
            throw new ApiException(SurveyErrorCode.NO_PERMISSION_TO_GET_SURVEY_INFO);
        }
    }
}
