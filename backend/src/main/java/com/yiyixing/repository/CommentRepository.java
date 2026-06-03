package com.yiyixing.repository;

import com.yiyixing.entity.Comment;
import com.yiyixing.entity.Comment.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByTargetTypeAndTargetIdOrderByCreatedAtAsc(TargetType targetType, Long targetId, Pageable pageable);
}
