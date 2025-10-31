package guckflix.backend.entity;

import guckflix.backend.dto.MovieDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    private String title;

    @Column(length = 1000)
    private String overview;

    private LocalDate releaseDate;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true, mappedBy = "movie")
    private List<MovieGenre> movieGenres = new ArrayList<>();

    private float popularity;

    private int voteCount;

    private float voteAverage;

    private String backdropPath;

    private String posterPath;

    @OneToMany(mappedBy = "movie")
    private List<Credit> credits = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<Video> videos = new ArrayList<>();

    public void updateVoteAdd(float voteRating) {
        this.voteAverage = ((this.voteAverage * voteCount) + voteRating) / (voteCount + 1);
        this.voteCount += 1;
    }

    public void updateVoteDelete(float voteRating) {
        this.voteAverage = ((this.voteAverage * voteCount) - voteRating) / (voteCount - 1);
        this.voteCount -= 1;
    }

    public void updateDetail(MovieDto.Update form) {
        this.title = form.getTitle();
        this.backdropPath = form.getBackdropPath();
        this.overview = form.getOverview();
        this.releaseDate = form.getReleaseDate();
    }

    public void updateCredit( Credit credit){
        credit.changeMovie(this);
    }
}