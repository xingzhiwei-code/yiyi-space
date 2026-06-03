package com.yiyixing.repository;

import com.yiyixing.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Page<Tag> findByOrderByUseCountDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE Tag t SET t.useCount = t.useCount + 1 WHERE t.id = :id")
    void incrementUseCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Tag t SET t.useCount = GREATEST(t.useCount - 1, 0) WHERE t.id = :id")
    void decrementUseCount(@Param("id") Long id);
}
