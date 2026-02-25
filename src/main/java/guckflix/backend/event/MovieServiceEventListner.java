package guckflix.backend.event;

import guckflix.backend.service.AiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieServiceEventListner {

    private final AiChatService aiChatService;

    public record MovieSavedOrUpdatedEvent(Long movieId) {}
    public record MovieDeletedEvent(Long movieId) {}

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMovieSavedOrUpdated(MovieSavedOrUpdatedEvent event) {
        try {
            aiChatService.upsertEmbeddedMovie(event.movieId());
        } catch (Exception e) {
            log.error("qdrant upsert failed. movieId={}", event.movieId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMovieDeleted(MovieDeletedEvent event) {
        try {
            aiChatService.deleteEmbeddedMovie(event.movieId());
        } catch (Exception e) {
            log.error("qdrant delete failed. movieId={}", event.movieId(), e);
        }
    }
}
