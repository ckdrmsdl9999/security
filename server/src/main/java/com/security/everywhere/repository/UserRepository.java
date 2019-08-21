package com.security.everywhere.repository;

import java.util.*;
import com.security.everywhere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByNickName(String nickName);
    User findByPw(String pw);
    void deleteByNickName(String nickName);
}
