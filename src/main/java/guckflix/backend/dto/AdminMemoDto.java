package guckflix.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import guckflix.backend.entity.AdminMemo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


public class AdminMemoDto {

    @Getter
    @Setter
    public static class Post {

        private String text;

    }

    @Getter
    @Setter
    public static class Response {

        public Response(AdminMemo memoEntity) {
            this.id = memoEntity.getId();
            this.createdAt = memoEntity.getCreatedAt();
            this.text = memoEntity.getText();
        }

        private Long id;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        private String text;

    }


}
