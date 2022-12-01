package site.mokaform.surveyserver.dto.survey.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import site.mokaform.surveyserver.domain.enums.Category;
import site.mokaform.surveyserver.domain.enums.MultiQuestionType;
import site.mokaform.surveyserver.domain.enums.QuestionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
public class SurveyCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String summary;

    @NotNull
    private Boolean isAnonymous;

    @NotNull
    private Boolean isPublic;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private List<Category> categories;

    private List<Question> questions;

    private List<MultiQuestion> multiQuestions;

    @Builder
    public SurveyCreateRequest(String title, String summary,
                               LocalDate startDate, LocalDate endDate,
                               Boolean isAnonymous, Boolean isPublic,
                               List<Category> categories, List<Question> questions,
                               List<MultiQuestion> multiQuestions) {
        this.title = title;
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAnonymous = isAnonymous;
        this.isPublic = isPublic;
        this.categories = categories;
        this.questions = questions;
        this.multiQuestions = multiQuestions;
    }

    @NoArgsConstructor
    @Getter
    public static class Question {

        @NotNull
        private Long index;

        @NotBlank
        private String title;

        @NotBlank
        private QuestionType type;

        @NotNull
        private Boolean isMultipleAnswer;

        @Builder
        public Question(Long index, String title,
                        QuestionType type, Boolean isMultipleAnswer) {
            this.index = index;
            this.title = title;
            this.type = type;
            this.isMultipleAnswer = isMultipleAnswer;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class MultiQuestion {

        @NotNull
        private Long questionIndex;

        @NotNull
        private Long index;

        @NotBlank
        private String content;

        @NotNull
        private MultiQuestionType type;

        @Builder
        public MultiQuestion(Long questionIndex, Long index,
                             String content, MultiQuestionType type) {
            this.questionIndex = questionIndex;
            this.index = index;
            this.content = content;
            this.type = type;
        }
    }
}
