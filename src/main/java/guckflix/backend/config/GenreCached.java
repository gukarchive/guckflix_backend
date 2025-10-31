package guckflix.backend.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.entity.Genre;
import guckflix.backend.repository.GenreRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * 스프링 시작 시 장르 테이블 캐시
 * unmodifiableMap으로 생성해서 어플리케이션 시작 시 외에 수정 불가
 *
 * 장르는 정적인 데이터. 따라서 테이블은 따로 두되 어플리케이션 시작 시 캐시된 이 곳에서 조인함
 */

@Component
@RequiredArgsConstructor
public class GenreCached {

    private final GenreRepository repository;

    @Getter
    private static Map<Long, Genre> genres;

    @PostConstruct
    public void init(){
        Map<Long, Genre> genresMap = new HashMap<>();
        List<Genre> genreList = repository.findAll();
        for (Genre genre : genreList) {
            genresMap.put(genre.getId(), genre);
        }
        genres = Collections.unmodifiableMap(genresMap);
    }

    @JsonIgnore
    public static List<GenreDto> getGenresByDtos(){
        List<Genre> entities = new ArrayList<>(genres.values());
        List<GenreDto> dtos = new ArrayList<>();
        for (Genre entity : entities) {
            dtos.add(new GenreDto(entity.getId(), entity.getGenreName()));
        }
        return dtos;
    }

//    @Deprecated(since = "다대다 연관관계를 MovieGenre 테이블 추가로 풀게 되어 사용하지 않게 되었음")
//    @JsonIgnore
//    public static List<GenreDto> genreStringToList(String entityGenres){
//
//        if(entityGenres == null || entityGenres.equals("")){
//            return null;
//        }
//
//        List<GenreDto> genreDtos = new ArrayList<>();
//        List<String> genreList = Arrays.asList(entityGenres.split(","));
//        for (String genre : genreList) {
//            long genreId = Long.parseLong(genre);
//            String genreName = GenreCached.getGenres().get(genreId);
//            genreDtos.add(new GenreDto(genreId, genreName));
//        }
//        return genreDtos;
//    }
//
//    @Deprecated(since = "다대다 연관관계를 MovieGenre 테이블 추가로 풀게 되어 사용하지 않게 되었음")
//    @JsonIgnore
//    public static String genreListToString(List<GenreDto> genreDtos){
//
//        if(genreDtos == null || genreDtos.size() == 0) {
//            return null;
//        }
//
//        return genreDtos.stream().map(genreDto -> Long.toString(genreDto.getId()))
//                .collect(Collectors.joining(","));
//
//    }

}
