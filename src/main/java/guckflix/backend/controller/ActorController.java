package guckflix.backend.controller;

import guckflix.backend.dto.ActorDto;
import guckflix.backend.dto.paging.PagingRequest;
import guckflix.backend.dto.paging.Slice;
import guckflix.backend.file.FileConst;
import guckflix.backend.file.FileUploader;
import guckflix.backend.service.ActorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    private final FileUploader fileUploader;

    @GetMapping("/actors/{actorId}")
    public ResponseEntity<ActorDto.Response> getActor(@PathVariable("actorId") Long actorId) {
        return ResponseEntity.ok(actorService.findDetail(actorId));
    }

    @GetMapping("/actors/search")
    public ResponseEntity<Slice> search(@RequestParam("keyword") String keyword, PagingRequest paging) {
        Slice<ActorDto.Response> actors = actorService.findActorsByKeyword(keyword, paging);
        return ResponseEntity.ok().body(actors);
    }

    @PostMapping("/actors")
    public ResponseEntity newActor(@RequestPart ActorDto.Post form,
                                   @RequestPart MultipartFile profileFile) throws BindException {

        String profileUUID = UUID.randomUUID().toString()+".jpg";
        form.setProfilePath(profileUUID);
        actorService.save(form);

        fileUploader.upload(profileFile, FileConst.DIRECTORY_PROFILE, profileUUID);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/actors/{actorId}")
    public ResponseEntity delete(@PathVariable("actorId") Long actorId){

        ActorDto.Response actor = actorService.findDetail(actorId);
        actorService.delete(actorId);
        fileUploader.delete(FileConst.DIRECTORY_PROFILE, actor.getProfilePath());

        return new ResponseEntity(HttpStatus.OK);
    } 

    @PatchMapping("/actors/{actorId}")
    public ResponseEntity update(@PathVariable("actorId") Long actorId,
                                 ActorDto.UpdateInfo actorUpdafeForm) {
        actorService.updateInfo(actorId, actorUpdafeForm);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/actors/{actorId}/photo")
    public ResponseEntity update(@PathVariable("actorId") Long actorId,
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
