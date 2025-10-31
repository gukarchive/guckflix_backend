package guckflix.backend.dto.paging;

public enum OrderDirection {
    ASC("asc"), DESC("desc");

    final private String direction;

    OrderDirection(String direction) {
        this.direction = direction;
    }
}
