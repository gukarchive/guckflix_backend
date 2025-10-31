package guckflix.backend.service;

import guckflix.backend.entity.Member;
import guckflix.backend.entity.enums.MemberRole;
import guckflix.backend.exception.DuplicateException;
import guckflix.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;

import static guckflix.backend.dto.MemberDto.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public String save(Post form){

        List<Member> findMember = memberRepository.findByUsername(form.getUsername());
        if (findMember.size() != 0) throw new DuplicateException("already exist id");

        form.setPassword(passwordEncoder.encode(form.getPassword()));
        Member member = Member.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .email(form.getEmail())
                .role(MemberRole.USER).build();
        memberRepository.save(member);
        return member.getUsername();
    }

    public String usernameAvailableCheck(String username) {

        List<Member> users = memberRepository.findByUsername(username);
        if(users.size() != 0) {
             return users.get(0).getUsername();
        } else {
            return null;
        }
    }
}
