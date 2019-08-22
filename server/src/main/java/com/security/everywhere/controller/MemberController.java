package com.security.everywhere.controller;


import com.security.everywhere.model.AuthenticationRequest;
import com.security.everywhere.model.AuthenticationToken;
import com.security.everywhere.model.Member;
import com.security.everywhere.model.MemberRole;
import com.security.everywhere.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final AuthenticationManager authenticationManager;

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository, AuthenticationManager authenticationManager) {
        this.memberRepository = memberRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public AuthenticationToken login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
        String nickName = authenticationRequest.getNickName();
        String password = authenticationRequest.getPassword();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(nickName, password);
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        Member member = memberRepository.findByNickName(nickName);

        return new AuthenticationToken(member.getNickName(), member.getRoles(), session.getId());
    }

    @PostMapping("/check/nickName")
    public String checkId(@RequestBody Member member) {
        int already = memberRepository.countByNickName(member.getNickName());

        if (already != 0) {
            return "1";
        } else {
            return "0";
        }
    }

    @PostMapping("/typeCheck/pw")
    public String passwordTypeChech(@RequestBody Member member) {
        boolean isPass = isPassed(member.getPassword());

        if (!member.getNickName().contains(member.getNickName())) {
            isPass = false;
        }

        if (isPass) {
            return "1";
        } else {
            return "0";
        }
    }

    @PostMapping("/create")
    public String create(@RequestBody Member member) {
        Member already = memberRepository.findByNickName(member.getNickName());
        boolean isPass;

        if (already == null) {
            isPass = isPassed(member.getPassword());

            if (!member.getNickName().contains(member.getNickName())) {
                isPass = false;
            }
            if (isPass) {
                MemberRole role = new MemberRole();
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                member.setPassword(passwordEncoder.encode(member.getPassword()));
                role.setRoleName("USER");
                member.setRoles(Collections.singletonList(role));
                memberRepository.save(member);
                return "1";
//                return "redirect:/main";
            } else {
                return "0";
            }
        } else {
            return "0";
        }
    }

    private boolean isPassed(String password) {
        boolean passed = true;     // 패턴 통과 여부
        String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{9,20}$";
        Matcher matcher = Pattern.compile(pwPattern).matcher(password);

        pwPattern = "(.)\\1\\1\\1";
        Matcher matcher2 = Pattern.compile(pwPattern).matcher(password);

        if(!matcher.matches()){
            passed = false;
        }

        if(matcher2.find()){
            passed = false;
        }

        if(password.contains(" ")){
            passed = false;
        }

        return passed;
    }
}
