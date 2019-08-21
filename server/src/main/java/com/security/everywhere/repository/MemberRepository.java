package com.security.everywhere.repository;

import com.security.everywhere.model.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Member findByEmailAndNickName(String email, String nickName);
}
