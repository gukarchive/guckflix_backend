package guckflix.backend.dto.paging;

import guckflix.backend.dto.DataTransferable;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.entity.Movie;

import java.util.ArrayList;
import java.util.List;

public abstract class PagingFactory {

    /**
     * 서비스 단에서 Paging<>와 List<>를 전달받아 새 instance를 반환하는 정적 팩토리 메서드
     *
     * @see DataTransferable
     */
    public static <T extends DataTransferable> Paging<T> newPaging(Paging<?> paging, List<T> list){
        return new Paging<T>(
                paging.getRequestPage(), list, paging.getTotalCount(), paging.getTotalPage(), paging.getSize()
        );
    }
}
