package com.security.everywhere.controller;

import java.util.*;
import com.security.everywhere.model.User;
import com.security.everywhere.repository.UserRepository;
import com.security.everywhere.request.UserParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/login")
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public boolean login(@RequestBody UserParam userParam, Model model) {
        String nickName = userParam.getNickName();
        String pw = userParam.getPw();
        if(nickName.equals(userRepository.findByNickName(nickName).getNickName()) && pw.equals(userRepository.findByNickName(nickName).getPw())
                && nickName.equals(userRepository.findByPw(pw).getNickName()) && pw.equals(userRepository.findByPw(pw).getPw())) {
            model.addAttribute("msg", "세션 이름 : " + nickName);
            //session.setAttribute("id", nickName);
            return true;
        }
        else
            return false;
    }

    @RequestMapping("/logout")
    public boolean logout(@RequestBody Model model, HttpSession session)
    {
        User user = (User)session.getAttribute("id");
        return true;
    }

    @Transactional
    @PostMapping("/addUser")
    public boolean addUser(@RequestBody UserParam userParam) {
        String nickName = userParam.getNickName();
        String pw = userParam.getPw();
        User user = new User(nickName, pw);
        userRepository.save(user);
        return true;
//        User user = userRepository.findByNickName(nickName);
//        if(ObjectUtils.isEmpty(user)) {
//            user.setNickName(nickName);
//            user.setPw(pw);
//            userRepository.save(user);
//            return true;
//        }
//        else
//            return false;
    }

    @Transactional
    @PostMapping("/deleteUser")
    public boolean deleteUser(@RequestBody UserParam userParam) {
        userRepository.deleteByNickName(userParam.getNickName());
        return true;
    }

    @PostMapping("/check")
    public String check(@RequestBody UserParam userParam) {
        String nickName = userParam.getNickName();
        User user = userRepository.findByNickName(nickName);
        if(user.getNickName() == nickName)
            return "Choose another nickname";
        else
            return "Complete";
    }

    @PostMapping("/userInfo")
    public List<User> userInfo() {
        List<User> list = userRepository.findAll();
        return list;
    }
}
