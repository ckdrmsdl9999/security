package com.security.everywhere.repository;

import com.security.everywhere.model.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {

}
