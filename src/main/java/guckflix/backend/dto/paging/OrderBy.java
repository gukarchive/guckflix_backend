package guckflix.backend.dto.paging;

public enum OrderBy {
    RELEASE_DATE("release_date"), VOTE_AVERAGE("vote_average"), POPULARITY("popularity");

    final private String condition;

    OrderBy(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
