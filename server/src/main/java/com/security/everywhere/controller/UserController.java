package com.security.everywhere.controller;

import java.util.*;
import com.security.everywhere.model.User;
import com.security.everywhere.repository.UserRepository;
import com.security.everywhere.request.UserParam;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/loginPage", method = RequestMethod.POST)
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @ResponseBody
    public  String login(@RequestBody UserParam userParam) {
        String nickName = userParam.getNickName();
        String pw = userParam.getPw();
        if(nickName.equals(userRepository.findByNickName(nickName).getNickName()) && pw.equals(userRepository.findByNickName(nickName).getPw())
                && nickName.equals(userRepository.findByPw(pw).getNickName()) && pw.equals(userRepository.findByPw(pw).getPw())) {
           // System.out.println(model.addAttribute("msg", "세션 이름 : " + nickName));
//            session.setAttribute("id", nickName);
//            session.setMaxInactiveInterval(60*10);
            System.out.println("컨트롤러에 왓어" +
                    "");
            return "redirect:/main";
        }
        else
            return "redirect:/main";
    }

    @RequestMapping("/logout")
    public boolean logout(@RequestBody Model model, HttpSession session)
    {
        User user = (User)session.getAttribute("id");
        return true;
    }

    @Transactional
    @PostMapping("/addUser")
    public String addUser(String username, String confirm_password) {
        User user = new User(username, confirm_password);
        userRepository.save(user);
        return "redirect:/main";
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
