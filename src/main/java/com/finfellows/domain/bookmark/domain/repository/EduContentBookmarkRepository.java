package com.finfellows.domain.bookmark.domain.repository;

import com.finfellows.domain.bookmark.domain.EduContentBookmark;
import com.finfellows.domain.bookmark.domain.FinancialProductBookmark;
import com.finfellows.domain.educontent.domain.EduContent;
import com.finfellows.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EduContentBookmarkRepository extends JpaRepository<EduContentBookmark, Long> {
    Optional<EduContentBookmark> findByUserAndEduContent(User user, EduContent eduContent);

    List<EduContentBookmark> findAllByUser(User user);

    boolean existsByUser_IdAndEduContent_Id(Long userId, Long id);
}
