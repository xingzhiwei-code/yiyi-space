package com.yiyixing.repository;

import com.yiyixing.entity.UserInteraction;
import com.yiyixing.entity.UserInteraction.TargetType;
import com.yiyixing.entity.UserInteraction.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    Optional<UserInteraction> findByUserIdAndTargetTypeAndTargetIdAndType(
            Long userId, TargetType targetType, Long targetId, Type type);

    boolean existsByUserIdAndTargetTypeAndTargetIdAndType(
            Long userId, TargetType targetType, Long targetId, Type type);

    long countByTargetTypeAndTargetIdAndType(TargetType targetType, Long targetId, Type type);

    Page<UserInteraction> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, Type type, Pageable pageable);

    List<UserInteraction> findByTargetTypeAndTargetIdAndType(TargetType targetType, Long targetId, Type type);
}
