package guckflix.backend.controller;

import guckflix.backend.dto.AdminMemoDto;
import guckflix.backend.file.FileConst;
import guckflix.backend.service.AdminService;
import guckflix.backend.service.MemberService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Api(tags = {"관리자 API"})
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

//    @PostMapping("/login")
//    @ApiOperation(value = "관리자 로그인", notes = "아이디와 비밀번호를 검사하여 관리자 로그인")
//    public ResponseEntity login(@Validated @ModelAttribute MemberDto.LoginForm form, BindingResult br) throws BindException {
//        if (br.hasErrors()) {
//            throw new BindException(br);
//        }
//        //String savedUsername = memberService.save(form);
//        return ResponseEntity.ok("ok");
//    }

    private final AdminService adminService;

    @GetMapping("/memos")
    public ResponseEntity<List<AdminMemoDto.Response>> getMemo() {

        List<AdminMemoDto.Response> memos = adminService.getMemos();

        return new ResponseEntity<>(memos, HttpStatus.OK);
    }

    @PostMapping("/memos")
    public ResponseEntity<Long> postMemo(@RequestBody AdminMemoDto.Post post) throws URISyntaxException {

        Long createdMemoId = adminService.postMemo(post);

        URI location = new URI("/admin/memos/"+createdMemoId);
        return ResponseEntity.created(location).body(createdMemoId);
    }

    @DeleteMapping("/memos/{id}")
    public ResponseEntity deleteMemo(@PathVariable Long id){
        adminService.deleteMemo(id);
        return ResponseEntity.ok(null);
    }

}
