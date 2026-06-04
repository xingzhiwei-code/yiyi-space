package com.yiyixing.repository;

import com.yiyixing.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadOrderByCreatedAtDesc(Long userId, Boolean read, Pageable pageable);

    long countByUserIdAndReadFalse(Long userId);

    void deleteAllByUserId(Long userId);
}
