package site.mokaform.surveyserver.domain.answer;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mokaform.surveyserver.common.entitiy.BaseEntity;
import site.mokaform.surveyserver.domain.MultipleChoiceQuestion;

import javax.persistence.*;

@Entity
@Table(name = "multiple_choice_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MultipleChoiceAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "multiple_choice_answer_id")
    private Long multipleChoiceAnswerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", referencedColumnName = "answer_id", nullable = false)
    private Answer answer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "multi_question_id", referencedColumnName = "multi_question_id", nullable = false)
    private MultipleChoiceQuestion multipleChoiceQuestion;

    @Builder
    public MultipleChoiceAnswer(Answer answer, MultipleChoiceQuestion multipleChoiceQuestion) {
        this.answer = answer;
        this.multipleChoiceQuestion = multipleChoiceQuestion;
    }
}
