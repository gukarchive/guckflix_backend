package guckflix.backend.controller;


import guckflix.backend.dto.MemberDto.passwordChangeForm;
import guckflix.backend.dto.MemberDto.Post;
import guckflix.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity join(@Valid @RequestBody Post form, BindingResult br) throws BindException {
        String savedUsername = memberService.save(form);
        return ResponseEntity.ok(savedUsername);
    }

    @DeleteMapping("/members/{userId}")
    public ResponseEntity delete(@Validated @ModelAttribute passwordChangeForm form){
        return ResponseEntity.ok("OK");
    }


    @PatchMapping("/members/{userId}")
    public ResponseEntity update(@Validated @ModelAttribute passwordChangeForm form){
        return ResponseEntity.ok("OK");
    }



    @PatchMapping("/members/{userId}/password")
    public ResponseEntity password(@Validated @ModelAttribute passwordChangeForm form){
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/members/usernameCheck")
    public ResponseEntity usernameCheck(@Validated @RequestParam(name = "username") String username){
        String findUsername = memberService.usernameAvailableCheck(username);

        if (findUsername == null) {
            return ResponseEntity.ok(null);
        } else {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }


}