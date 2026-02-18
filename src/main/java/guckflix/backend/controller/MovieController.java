package guckflix.backend.controller;

import guckflix.backend.config.GenreCached;
import guckflix.backend.dto.*;
import guckflix.backend.dto.ReviewDto.Post;
import guckflix.backend.dto.ReviewDto.Response;
import guckflix.backend.dto.paging.PagingRequest;
import guckflix.backend.dto.paging.Slice;
import guckflix.backend.dto.paging.Paging;
import guckflix.backend.dto.wrapper.ResponseWrapper;
import guckflix.backend.entity.Genre;
import guckflix.backend.exception.RuntimeIOException;
import guckflix.backend.file.FileConst;
import guckflix.backend.file.FileUploader;
import guckflix.backend.repository.GenreRepository;
import guckflix.backend.security.authen.PrincipalDetails;
import guckflix.backend.service.CreditService;
import guckflix.backend.service.MovieService;
import guckflix.backend.service.ReviewService;
import guckflix.backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final CreditService creditService;
    private final VideoService videoService;
    private final ReviewService reviewService;

    private final FileUploader fileUploader;

    @GetMapping("/movies")
    public ResponseEntity<Paging> getMovies(PagingRequest pagingRequest) {
        Paging<MovieDto.Response> paging = movieService.searchAndSort(pagingRequest);
        return ResponseEntity.ok(paging);
    }

    /**
     * popularity 기준 페이징
     */
        @GetMapping("/movies/popular")
    public ResponseEntity<Paging> popular(PagingRequest pagingRequest) {
        Paging<MovieDto.Response> popular = movieService.findPopular(pagingRequest);
        return ResponseEntity.ok(popular);
    }


    /**
     * 투표 가중치 기준 페이징
     * 투표 가중치 : guckflix.backend.config.QueryWeight
     */
    @GetMapping("/movies/top-rated")
    public ResponseEntity<Paging> topRated(PagingRequest pagingRequest) {
        Paging<MovieDto.Response> topRated = movieService.findTopRated(pagingRequest);
        return ResponseEntity.ok(topRated);
    }

    /**
     * 영화 상세 보기
     */
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<MovieDto.Response> detail(@PathVariable("movieId") Long movieId) {
        MovieDto.Response findMovie = movieService.findById(movieId);
        return ResponseEntity.ok(findMovie);
    }

    /**
     * 유사한 영화 보기
     */
    @GetMapping("/movies/{movieId}/similar")
    public ResponseEntity<Paging> similar(@PathVariable("movieId") Long movieId,
                                          PagingRequest pagingRequest) {
        Paging<MovieDto.Response> similar = movieService.findSimilar(movieId, pagingRequest);
        return ResponseEntity.ok(similar);
    }

    /**
     * 영화 크레딧(배역, 배우) 보기
     */
    @GetMapping("/movies/{movieId}/credits")
    public ResponseEntity<ResponseWrapper> credits(@PathVariable("movieId") Long movieId) {
        List<CreditDto.Response> credit = creditService.findActors(movieId);
        return ResponseEntity.ok(ResponseWrapper.withMovieId(movieId, credit));
    }

    /**
     * 영화 비디오(트레일러 등) 리스트
     * accept-language : ko or en
     */
    @GetMapping("/movies/{movieId}/videos")
    public ResponseEntity<ResponseWrapper> videos(@PathVariable("movieId") Long movieId,
                                                        Locale locale) {
        List<VideoDto.Response> result = videoService.findById(movieId, locale.getLanguage());
        return ResponseEntity.ok(ResponseWrapper.withMovieId(movieId, result));
    }

    /**
     * 리뷰 조회
     */
    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<Paging> reviews(@PathVariable("movieId") Long movieId, PagingRequest pagingRequest) {
        Paging<Response> reviews = reviewService.findAllById(movieId, pagingRequest);
        return ResponseEntity.ok().body(reviews);
    }

    /**
     * 리뷰 작성
     */
    @PostMapping("/movies/{movieId}/reviews")
    public ResponseEntity<Response> reviewsPost(@PathVariable("movieId") Long movieId,
                                                @AuthenticationPrincipal PrincipalDetails user,
                                                @ModelAttribute Post dto) {
        dto.setMovieId(movieId);
        dto.setUserId(user.getMember().getId());
        Long findId = reviewService.save(dto);
        return ResponseEntity.ok().body(reviewService.findById(findId));
    }

    @DeleteMapping("/movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<String> reviewsDelete(@PathVariable("movieId") Long movieId,
                                                @AuthenticationPrincipal PrincipalDetails user,
                                                @PathVariable("reviewId") Long reviewId) {
        reviewService.delete(reviewId, movieId, user.getMember().getId());
        return ResponseEntity.ok().body("DELETED");
    }

    /**
     * 영화 검색
     */
    @GetMapping("/movies/search")
    public ResponseEntity<Slice> search(@RequestParam("keyword") String keyword, PagingRequest pagingRequest) {
        Slice<MovieDto.Response> movies = movieService.findByKeyword(keyword, pagingRequest);
        return ResponseEntity.ok().body(movies);
    }


    /**
     * 영화 등록
     */
    @PostMapping(value = "/movies")
    public ResponseEntity post(@Valid @RequestPart MovieDto.Post form,
                               @RequestPart MultipartFile originFile,
                               @RequestPart MultipartFile w500File) throws URISyntaxException {


        // form에 세팅할 input 이미지 UUID
        String originUUID = UUID.randomUUID().toString() + ".jpg";
        String w500UUID = UUID.randomUUID().toString() + ".jpg";
        form.setBackdropPath(originUUID);
        form.setPosterPath(w500UUID);

        // DB 업데이트
        Long savedId = movieService.save(form);

        // 파일 업로드
        try {
            fileUploader.upload(originFile, FileConst.DIRECTORY_ORIGINAL, originUUID);
            fileUploader.upload(w500File, FileConst.DIRECTORY_W500, w500UUID);
        } catch (RuntimeIOException e) {
            log.warn("The entity was saved but encountered an error processing the file. movie id = " + savedId, e);
        }

        URI location = new URI("/movies/"+savedId);
        return ResponseEntity.created(location).build();
    };

    /**
     * 영화 수정
     */
    @PatchMapping("/movies/{movieId}")
    public ResponseEntity update(
                                @PathVariable("movieId") Long movieId,
                                @RequestPart(required = false) MultipartFile w500File,
                                @RequestPart(required = false) MultipartFile originFile,
                                @Valid @RequestPart MovieDto.Update movieUpdateForm){

        // 이미지가 있는 경우 movieUpdateForm에 수정할 UUID 지정
        String w500UUID = null;
        String originUUID = null;

        if(!w500File.isEmpty()) {
            w500UUID = UUID.randomUUID().toString()+ ".jpg";
            movieUpdateForm.setPosterPath(w500UUID);
        }
        if(!originFile.isEmpty()) {
            originUUID = UUID.randomUUID().toString()+".jpg";
            movieUpdateForm.setBackdropPath(originUUID);
        }

        // 기존 이미지 삭제를 위한 기존 데이터 불러오기
        MovieDto.Response dto = movieService.findById(movieId);
        
        // DB 업데이트
        movieService.update(movieUpdateForm, movieId);

        // 파일 업로드 및 삭제
        try {
            if(!w500File.isEmpty()) {
                fileUploader.upload(w500File, FileConst.DIRECTORY_W500, w500UUID);
                fileUploader.delete(FileConst.DIRECTORY_W500, dto.getPosterPath());
            }
            if(!originFile.isEmpty()) {
                fileUploader.upload(originFile, FileConst.DIRECTORY_ORIGINAL, originUUID);
                fileUploader.delete(FileConst.DIRECTORY_ORIGINAL, dto.getBackdropPath());
            }
        } catch (RuntimeIOException e) {
            log.warn("The entity was updated but encountered an error processing the file. movie id = " + movieId, e);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 영화 삭제
     */
    @DeleteMapping("/movies/{movieId}") 
    public ResponseEntity delete(@PathVariable("movieId") Long movieId){
        MovieDto.Response dto = movieService.findById(movieId);
        movieService.delete(movieId);
        fileUploader.delete(FileConst.DIRECTORY_ORIGINAL, dto.getBackdropPath());
        fileUploader.delete(FileConst.DIRECTORY_W500, dto.getPosterPath());

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 영화 크레딧 추가
     */
    @PostMapping("/movies/{movieId}/credits")
    public ResponseEntity addCredit(@PathVariable("movieId") Long movieId,
                                    @RequestBody CreditDto.Post form) throws URISyntaxException {

        ActorDto.Response.CreditWithMovieInfo response = creditService.addCredit(movieId, form);
        return ResponseEntity.ok(response);
    }

    /**
     * 영화 크레딧 수정
     */
    @PatchMapping("/movies/{movieId}/credits/{creditId}")
    public ResponseEntity patchCredit(@PathVariable("movieId") Long movieId, @PathVariable("creditId") Long creditId,
                                      @RequestBody CreditDto.Patch creditPatchForm) {

        creditService.updateCredit(movieId, creditId, creditPatchForm);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * 영화 크레딧 삭제
     */
    @DeleteMapping("/movies/{movieId}/credits/{creditId}")
    public ResponseEntity deleteCredit(@PathVariable("movieId") Long movieId,
                                       @PathVariable("creditId") Long creditId){
        creditService.deleteCredit(movieId, creditId); ;
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * 영화 장르 조회
     */
    @GetMapping("/genres")
    public ResponseEntity getGenres(){
        return ResponseEntity.ok().body(GenreCached.getGenresByDtos());
    }

}
