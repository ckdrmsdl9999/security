package com.security.everywhere.repository;

import com.security.everywhere.model.Festival;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FestivalRepository extends PagingAndSortingRepository<Festival, Long> {

    // eventStartDate 이상의 값
    List<Festival> findAllByEventStartDateGreaterThanEqual(String startDate, Pageable pageable);
}