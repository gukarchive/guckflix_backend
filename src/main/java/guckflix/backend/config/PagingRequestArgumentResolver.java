package guckflix.backend.config;

import guckflix.backend.dto.paging.OrderBy;
import guckflix.backend.dto.paging.OrderDirection;
import guckflix.backend.dto.paging.PagingRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Spring Data Jpa의 Pageable 사용하는 것 대신 커스텀 페이징 객체 Resolver
 */
public class PagingRequestArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == PagingRequest.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String paramPage = webRequest.getParameter("page");
        String paramLimit = webRequest.getParameter("limit");
        String orderByString = webRequest.getParameter("orderBy");
        String orderDirectionString = webRequest.getParameter("direction");
        String keyword = webRequest.getParameter("keyword");

        /**
         * requestPage : API 사용자가 요청한 페이지 넘버를 확인하기 위한 변수. 0페이지를 요청하면 1페이지, 1페이지를 요청해도 1페이지로 응답 dto에 함께 나감.
         * page : requestPage에 따라 요청된 실제 db의 페이지 넘버. 프론트에서는 알 필요 없는 변수
         * limit : 페이지 사이즈
         * offset : (페이지 사이즈 X page) 번째 데이터부터 출력
         */
        int page = (paramPage == null || paramPage.equals("")) ? 0 : Integer.parseInt(paramPage); // defaultVaule 0
        int limit = (paramLimit == null || paramLimit.equals("")) ? 20 : Integer.parseInt(paramLimit); // defaultVaule 20
        int requestPage = (page == 0) ? 1 : page;
        int offset = page - 1 >= 0 ? (page - 1 ) * limit : 0;

        // @RequestParam(required = false)와 같은 역할
        if(orderByString != null || orderByString != null) {

            // enum 생성
            OrderBy orderBy = OrderBy.valueOf(orderByString.toUpperCase());
            OrderDirection orderDirection = OrderDirection.valueOf(orderDirectionString.toUpperCase());

            return PagingRequest.createWithOrder(requestPage, offset, limit, orderBy, orderDirection, keyword);
        }

        return PagingRequest.create(requestPage, offset, limit, keyword);
    }
}
