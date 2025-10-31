package guckflix.backend.entity;


import guckflix.backend.entity.base.TimeDateBaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class Review extends TimeDateBaseEntity {

    @Column(name = "review_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "user_id")
    private Long userId;

    private String content;

    @Column(name = "vote_rating")
    private Float voteRating;

}
