package guckflix.backend.service;

import guckflix.backend.dto.VideoDto;
import guckflix.backend.dto.VideoDto.Response;
import guckflix.backend.entity.Video;
import guckflix.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public List<Response> findById(Long movieId, String locale){
        return videoRepository.findAllByMovieId(movieId, locale).stream()
                .map((entity)-> new Response(entity))
                .collect(Collectors.toList());
    }

}
