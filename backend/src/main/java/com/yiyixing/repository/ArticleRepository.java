package com.yiyixing.repository;

import com.yiyixing.entity.Article;
import com.yiyixing.entity.Article.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByStatusOrderByCreatedAtDesc(Status status, Pageable pageable);

    Optional<Article> findBySlugAndStatus(String slug, Status status);

    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Article a SET a.likeCount = a.likeCount + 1 WHERE a.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Article a SET a.likeCount = GREATEST(a.likeCount - 1, 0) WHERE a.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Article a SET a.favoriteCount = a.favoriteCount + 1 WHERE a.id = :id")
    void incrementFavoriteCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Article a SET a.favoriteCount = GREATEST(a.favoriteCount - 1, 0) WHERE a.id = :id")
    void decrementFavoriteCount(@Param("id") Long id);
}
