package guckflix.backend.entity;


import guckflix.backend.entity.enums.ISO3166;
import guckflix.backend.entity.enums.ISO639;
import guckflix.backend.entity.enums.VideoProvider;
import guckflix.backend.entity.enums.VideoType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class Video {

    // tmdb api는 String uuid 62c041a322e4800fa8f138eb와 같은 방식으로 저장하므로 따르기로 한다.
    @Id @Column(name = "video_id", length = 30)
    private String id;

    @JoinColumn(name = "movie_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Column(name = "video_name")
    private String name;

    // 언어 (en, us)
    @Column(name = "iso_639", length = 10)
    @Enumerated(EnumType.STRING)
    private ISO639 iso639;

    // 국가 (EN, KR)
    @Column(name = "iso_3166")
    @Enumerated(EnumType.STRING)
    private ISO3166 iso3166;

    //ex) youtube/2xksl159291와 같이 사이트에 요청할 비디오 식별자. site/key로 URL 요청
    @Column(name = "video_key")
    private String key;

    private Boolean official;

    @Enumerated(EnumType.STRING)
    private VideoProvider site;

    @Enumerated(EnumType.STRING)
    @Column(name = "video_type")
    private VideoType type;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

}
