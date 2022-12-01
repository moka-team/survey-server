package site.mokaform.surveyserver.domain.answer;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mokaform.surveyserver.common.entitiy.BaseEntity;
import site.mokaform.surveyserver.domain.Question;

import javax.persistence.*;

@Entity
@Table(name = "answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @Column(name = "surveyee_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id", nullable = false)
    private Question question;

    @Builder
    public Answer(Long userId, Question question) {
        this.userId = userId;
        this.question = question;
    }
}
