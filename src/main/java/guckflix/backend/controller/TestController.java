package guckflix.backend.controller;


import guckflix.backend.dto.ActorDto;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.dto.paging.PagingRequest;
import guckflix.backend.dto.paging.Slice;
import guckflix.backend.exception.DuplicateException;
import guckflix.backend.exception.NotAllowedIdException;
import guckflix.backend.file.FileConst;
import guckflix.backend.file.FileUploader;
import guckflix.backend.service.ActorService;
import guckflix.backend.service.CreditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test")
    public String test() {
        try {
            causeException();
        } catch (RuntimeException e) {
            throw new NotAllowedIdException("Not allowed ID", e);
        }
        return "oK";
    }

    @GetMapping("/test2")
    public String test2() {
        try {
            causeException();
        } catch (RuntimeException e) {
            throw new DuplicateException("로깅되지 않는 예외");
        }
        return "oK";
    }


    private void causeException() {
        if( Math.random() <= 1) {
            throw new RuntimeException("런타임 익셉션");
        }
    }

    @GetMapping("/test3")
    public String test3(MovieDto.Post post) {

        return "oK";
    }

}
