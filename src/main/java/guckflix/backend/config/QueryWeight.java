package guckflix.backend.config;

/**
 * top_rated 작품을 가져올 때
 * 단순 평점 순 정렬이 아닌,
 * 평점과 평점 갯수, 인기도의 가중치
 */
public abstract class QueryWeight {

    public final static float VOTE_AVERAGE_WEIGHT = 2000.0f;
    public final static int VOTE_COUNT_WEIGHT = 1;
    public final static float POPULARITY_WEIGHT = 0.1f;

}
