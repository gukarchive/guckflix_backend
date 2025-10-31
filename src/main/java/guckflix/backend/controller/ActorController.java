package guckflix.backend.controller;

import guckflix.backend.dto.ActorDto;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.dto.paging.PagingRequest;
import guckflix.backend.dto.paging.Slice;
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
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@RestController
@Api(tags = {"배우 API"})
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    private final FileUploader fileUploader;

    @GetMapping("/actors/{actorId}")
    @ApiOperation(value = "배우 조회", notes = "배우를 조회한다")
    public ResponseEntity<ActorDto.Response> getActor(@PathVariable Long actorId) {
        return ResponseEntity.ok(actorService.findDetail(actorId));
    }

    @GetMapping("/actors/search")
    @ApiOperation(value = "배우 검색", notes = "배우를 검색한다")
    public ResponseEntity<Slice> search(@RequestParam String keyword, PagingRequest paging) {
        Slice<ActorDto.Response> actors = actorService.findActorsByKeyword(keyword, paging);
        return ResponseEntity.ok().body(actors);
    }

    @PostMapping("/actors")
    @ApiOperation(value = "배우 등록", notes = "배우를 등록한다")
    public ResponseEntity newActor(@RequestPart ActorDto.Post form,
                                   @RequestPart MultipartFile profileFile) throws BindException {

        String profileUUID = UUID.randomUUID().toString()+".jpg";
        form.setProfilePath(profileUUID);
        actorService.save(form);

        fileUploader.upload(profileFile, FileConst.DIRECTORY_PROFILE, profileUUID);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/actors/{actorId}")
    @ApiOperation(value = "배우 삭제", notes = "배우를 삭제한다. 크레딧도 함께 삭제")
    public ResponseEntity delete(@PathVariable Long actorId){

        ActorDto.Response actor = actorService.findDetail(actorId);
        actorService.delete(actorId);
        fileUploader.delete(FileConst.DIRECTORY_PROFILE, actor.getProfilePath());

        return new ResponseEntity(HttpStatus.OK);
    } 

    @PatchMapping("/actors/{actorId}")
    @ApiOperation(value = "배우 기본정보 수정", notes = "배우 기본 정보를 수정한다")
    public ResponseEntity update(@PathVariable Long actorId,
                                 ActorDto.UpdateInfo actorUpdafeForm) {
        actorService.updateInfo(actorId, actorUpdafeForm);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/actors/{actorId}/photo")
    @ApiOperation(value = "배우 사진 수정", notes = "배우 이미지를 수정한다")
    public ResponseEntity update(@PathVariable Long actorId,
                                 @RequestPart("imageFile") MultipartFile imageFile, HttpServletRequest request) throws URISyntaxException {

        ActorDto.Response findActor = actorService.findDetail(actorId);

        String profileUUID = UUID.randomUUID().toString()+ ".jpg";
        fileUploader.delete(FileConst.DIRECTORY_PROFILE, findActor.getProfilePath());
        fileUploader.upload(imageFile, FileConst.DIRECTORY_PROFILE, profileUUID);
        actorService.updatePhoto(actorId, profileUUID);

        String protocol = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();

        URI location = new URI(protocol+"://"+host+":"+port+"/"+"images/"+FileConst.DIRECTORY_PROFILE+"/"+profileUUID);
        return ResponseEntity.created(location).build();
    }

}
