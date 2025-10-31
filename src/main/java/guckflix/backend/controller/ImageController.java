package guckflix.backend.controller;

import guckflix.backend.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import static guckflix.backend.file.FileConst.*;
import static org.springframework.http.MediaType.IMAGE_JPEG;

@RestController
@Slf4j
@Api(tags = {"이미지 API"})
@RequiredArgsConstructor
public class ImageController {

    /**
     * 영화, 배우 이미지 출력
     * ETag로 브라우저 캐시 사용 유도
     */
    @GetMapping(value = "/images/{imageCategory}/{file}")
    @ApiOperation(value = "이미지 조회", notes = "파일 명과 요청 종류에 따라 이미지를 보여준다.")
    public ResponseEntity<Resource> imageVideo(@PathVariable String file,
                                               @PathVariable String imageCategory, HttpServletRequest req) throws IOException {
        Resource resource = null;
        if(imageCategory.equals(DIRECTORY_W500)){
            resource = new UrlResource("file", IMAGE_DIRECTORY_ROOT +"/"+ DIRECTORY_W500 +"/"+file);
        } else if(imageCategory.equals(DIRECTORY_ORIGINAL)) {
            resource = new UrlResource("file", IMAGE_DIRECTORY_ROOT +"/"+ DIRECTORY_ORIGINAL +"/"+file);
        } else if(imageCategory.equals(DIRECTORY_PROFILE)){
            resource = new UrlResource("file", IMAGE_DIRECTORY_ROOT +"/"+ DIRECTORY_PROFILE +"/"+file);
        }

        // 클라이언트 if-None-Match와 서버 E-Tag를 생성하여 비교해서 바뀐 것이 없으면 본문을 포함하지 않은 304 응답
        String clientETag = req.getHeader("If-None-Match");
        String serverETag = generateETag(resource);
        if (clientETag != null && clientETag.equals(serverETag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).contentType(IMAGE_JPEG).
            cacheControl(CacheControl.noCache()).eTag(serverETag).build();
        }

        // 최초 요청이거나 바뀐 것이 있으면 etag와 함께 200 응답
        return ResponseEntity.ok().contentType(IMAGE_JPEG)
                .cacheControl(CacheControl.noCache()) // no-Cache:캐시는 저장하지만 사용할 때마다 서버에 재검증
                .eTag(generateETag(resource))
                .body(resource);
    }

    /**
     * 스프링이 제공하는 Etag filter ShallowEtagHeaderFilter는
     * 응답 헤더에서 다음과 같이 따옴표가 들어가는 형식 : Etag : "836901692966263412"
     * https://www.baeldung.com/etags-for-rest-with-spring
     */
    private String generateETag(Resource resource){
        try {
            long contentLength = resource.contentLength(); // 리소스 길이
            long lastModified = resource.lastModified(); // 리소스 마지막 수정일
            String fileName = resource.getFilename();
            return "\"" +contentLength+lastModified+fileName+"\"";
        } catch (FileNotFoundException e) {
            throw new NotFoundException("File Not Exist", e);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
