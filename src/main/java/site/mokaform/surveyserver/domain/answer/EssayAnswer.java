package site.mokaform.surveyserver.domain.answer;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mokaform.surveyserver.common.entitiy.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "essay_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EssayAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "essay_answer_id")
    private Long essayAnswerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", referencedColumnName = "answer_id", nullable = false)
    private Answer answer;

    @Column(name = "answer_content", nullable = false, length = 255)
    private String answerContent;

    @Builder
    public EssayAnswer(Answer answer, String answerContent) {
        this.answer = answer;
        this.answerContent = answerContent;
    }
}
