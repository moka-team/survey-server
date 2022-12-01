package site.mokaform.surveyserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.mokaform.surveyserver.common.response.ApiResponse;
import site.mokaform.surveyserver.dto.answer.request.AnswerCreateRequest;
import site.mokaform.surveyserver.service.AnswerService;

import javax.validation.Valid;

@Tag(name = "답변", description = "설문 답변 관련 API입니다.")
@RestController
@RequestMapping("/survey-service/answer")
public class AnswerController {
    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @Operation(summary = "설문 답변 등록", description = "설문의 답변을 등록하는 API입니다.")
    @PostMapping
    public ResponseEntity<ApiResponse> createAnswer(@RequestBody @Valid AnswerCreateRequest request,
                                                    @RequestParam(required = true) Long userId) {
        answerService.createAnswer(request, userId);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("새로운 답변 생성이 성공하였습니다.")
                        .build());

    }

}
