package com.security.everywhere.repository;

import com.security.everywhere.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByContentId(String contentid);

    Review findByContentIdOrderByLikecountDesc(String contentid);

}
