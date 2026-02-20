package guckflix.backend.controller;

import guckflix.backend.dto.AiDto;
import guckflix.backend.service.AiChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;

    @PostMapping("/ai/chat")
    public ResponseEntity<AiDto.ChatResponse> chat(@Valid @RequestBody AiDto.ChatRequest request) {
        AiDto.SearchCondition condition = aiChatService.analyze(request);
        if (condition == null || !Boolean.TRUE.equals(condition.getIsMovieRecommendation())) {
            return ResponseEntity.ok(
                    new AiDto.ChatResponse("This request cannot be processed. I only provide movie recommendation functionality.")
            );
        }

        String answer = aiChatService.ask(request.getMessage(), condition);
        return ResponseEntity.ok(new AiDto.ChatResponse(answer));
    }

    @PostMapping("/ai/embed")
    public ResponseEntity<Integer> embedMovies() {
        int indexedCount = aiChatService.embedAllMovies();
        return ResponseEntity.ok(indexedCount);
    }
}
