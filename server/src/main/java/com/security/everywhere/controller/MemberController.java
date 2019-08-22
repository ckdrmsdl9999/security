package com.security.everywhere.controller;


import com.security.everywhere.model.Member;
import com.security.everywhere.model.MemberRole;
import com.security.everywhere.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collections;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @PostMapping("/create")
    public String create(@RequestBody Member member) {
        Member already = memberRepository.findByEmailAndNickName(member.getEmail(), member.getNickName());
        if (already != null) {
            MemberRole role = new MemberRole();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            member.setPw(passwordEncoder.encode(member.getPw()));
            role.setRoleName("BASIC");
            member.setRoles(Collections.singletonList(role));
            memberRepository.save(member);
            return "redirect:/main";
        } else {
            return "이미 존재합니다.";
        }
    }
}
