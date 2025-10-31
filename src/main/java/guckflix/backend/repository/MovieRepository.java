package guckflix.backend.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import guckflix.backend.config.SnakeToCamelCaseUtil;
import guckflix.backend.dto.paging.*;
import guckflix.backend.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static guckflix.backend.config.QueryWeight.*;
import static guckflix.backend.entity.QMovie.movie;
import static guckflix.backend.entity.QMovieGenre.movieGenre;


@Repository
public class MovieRepository implements CommonRepository<Movie, Long> {

    @Autowired EntityManager em;

    JPAQueryFactory queryFactory;

    public MovieRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long save(Movie entity){
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public Movie findById(Long id) {
        return em.find(Movie.class, id);
    }

    public Movie findByIdFetch(Long id){
        return em.createQuery("select m from Movie m join fetch m.movieGenres mg join fetch mg.genre g where m.id = :id", Movie.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public void remove(Movie entity){
        em.remove(entity);
    }

    public Paging<Movie> findPopular(PagingRequest pagingRequest) {
        List<Movie> list = em.createQuery("select m from Movie m order by m.popularity desc", Movie.class)
                .setFirstResult(pagingRequest.getOffset()) // offset
                .setMaxResults(pagingRequest.getLimit())
                .getResultList();
        int totalCount = selectCountAll().intValue();
        int totalPage = getTotalPage(totalCount, pagingRequest.getLimit());
        return new Paging(pagingRequest.getRequestPage(), list, totalCount, totalPage, pagingRequest.getLimit());
    }

    /**
     * select title, vote_average, vote_count, popularity from movie
     * order by ( vote_average * 가중치 ) + (vote_count * 가중치 ) + (popularity * 가중치 ) desc;
     */
    public Paging<Movie> findTopRated(PagingRequest pagingRequest) {
        List<Movie> list = em.createQuery("select m from Movie m" +
                        " order by ((m.voteAverage * :voteAverage) + (m.voteCount * :voteCount) + (popularity * :popularity )) desc", Movie.class)
                .setParameter("voteAverage", VOTE_AVERAGE_WEIGHT)
                .setParameter("voteCount", VOTE_COUNT_WEIGHT)
                .setParameter("popularity", POPULARITY_WEIGHT)
                .setFirstResult(pagingRequest.getOffset()) // offset
                .setMaxResults(pagingRequest.getLimit())
                .getResultList();
        int totalCount = selectCountAll().intValue();
        int totalPage = getTotalPage(totalCount, pagingRequest.getLimit());
        return new Paging(pagingRequest.getRequestPage(), list, totalCount, totalPage, pagingRequest.getLimit());
    }

    private Long selectCountAll(){
        return em.createQuery("select count(m) from Movie m", Long.class).getSingleResult();
    }

    public Slice<Movie> findByKeyword(String keyword, PagingRequest pagingRequest) {
        List<Movie> list = em.createQuery("select m from Movie m where m.title like :keyword", Movie.class)
                .setParameter("keyword", "%"+keyword+"%")
                .setFirstResult(pagingRequest.getOffset())
                .setMaxResults(pagingRequest.getLimit()+1)
                .getResultList();

        /**
         * Slice는 limit보다 한 개 더 가져와서 다음 페이지가 있는지 확인함
         */
        boolean hasNext = list.size() > pagingRequest.getLimit() ? true : false;
        if (hasNext == true) list.remove(list.size()-1);
        return new Slice<>(hasNext, pagingRequest.getRequestPage(), list, pagingRequest.getLimit());
    }

    public Paging<Movie> findSimilarByGenres(Movie entity, PagingRequest pagingRequest) {

        List<MovieGenre> movieGenres = entity.getMovieGenres();
        List<Long> genreIds = movieGenres.stream().map(e -> e.getGenre().getId()).collect(Collectors.toList());

        BooleanBuilder genreCond = new BooleanBuilder();

        // 검색한 영화 장르를 포함하면서
        for (Long genreId : genreIds) {
            genreCond.or(movieGenre.genre.id.in(genreId));
        }
        // 검색한 영화와 같은 MovieGenre 엔티티만 제외
        for (MovieGenre entitiesMovieGenre : movieGenres) {
            genreCond.andNot(movieGenre.eq(entitiesMovieGenre));
        }

        //
        List<Movie> list = queryFactory.selectDistinct(movie)
                .from(movie)
                .join(movie.movieGenres, movieGenre)
                .where(genreCond)
                .offset(pagingRequest.getOffset())
                .limit(pagingRequest.getLimit())
                .orderBy(movie.popularity.desc())
                .fetch();

        // 프록시 객체 초기화
        for (Movie movie : list) {
            for (MovieGenre movieGenre : movie.getMovieGenres()) {
                movieGenre.getId();
            }
        }

        int totalCount = selectCountAll().intValue();
        int totalPage = getTotalPage(totalCount, pagingRequest.getLimit());
        return new Paging(pagingRequest.getRequestPage(), list, totalCount, totalPage, pagingRequest.getLimit());

    }

    public int getTotalPage(int totalCount, int limit){
        int totalPage = totalCount / limit;
        if(totalCount % limit > 0) {
            totalPage = totalPage + 1;
        }
        return totalPage;
    }

    public List<Movie> findByIds(List<Long> movieIds) {
        return em.createQuery("select m from Movie m where m.id in :ids")
                .setParameter("ids", movieIds)
                .getResultList();
    }

    public Paging<Movie> searchAndSort(PagingRequest pagingRequest) {

        // 검색 키워드 설정
        BooleanBuilder cond = new BooleanBuilder();
        cond.and(movie.title.like("%"+pagingRequest.getKeyword()+"%"));

        // 정렬 기준, 정렬 방향 설정
        OrderDirection orderDirection = pagingRequest.getOrderDirection();
        PathBuilder<Movie> entityPath = new PathBuilder<>(Movie.class, "movie");

        if(orderDirection == null) {
            orderDirection = OrderDirection.ASC;
        }

        OrderSpecifier<?> orderSpecifier = orderDirection.equals(OrderDirection.ASC) ?
                entityPath.getString(SnakeToCamelCaseUtil.convertSnakeToCamel(pagingRequest.getOrderBy().getCondition())).asc()
                : entityPath.getString(SnakeToCamelCaseUtil.convertSnakeToCamel(pagingRequest.getOrderBy().getCondition())).desc();

        // 쿼리
        // 1:n을 페치조인 하면 limit 절이 동작하지 않아 OOM 발생할 가능성이 있음
        List<Movie> movies = queryFactory.select(movie)
                .from(movie)
                .where(cond)
                .offset(pagingRequest.getOffset())
                .limit(pagingRequest.getLimit())
                .orderBy(orderSpecifier)
                .fetch();

        // 프록시 초기화. default_batch_fetch_size 또는 @BatchSize으로 in절 처리
        for (Movie m : movies) {
            for (MovieGenre mg : m.getMovieGenres()) {
                mg.getId();
                mg.getGenre().getId();
            }
            for (Credit c : m.getCredits()) {
                c.getId();
            }
        }

        Long count = queryFactory.select(movie.count())
                .from(movie)
                .where(cond)
                .offset(pagingRequest.getOffset())
                .limit(pagingRequest.getLimit())
                .orderBy(orderSpecifier)
                .fetchCount();

        int totalPage = getTotalPage(count.intValue(), pagingRequest.getLimit());
        return new Paging(pagingRequest.getRequestPage(), movies, count.intValue(), totalPage, pagingRequest.getLimit());
    }
}
