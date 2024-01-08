package com.finfellows.domain.educontent.application;

import com.finfellows.domain.bookmark.domain.repository.EduContentBookmarkRepository;
import com.finfellows.domain.educontent.domain.EduContent;
import com.finfellows.domain.educontent.domain.repository.EduContentRepository;
import com.finfellows.domain.educontent.dto.request.EduContentRequest;
import com.finfellows.domain.educontent.dto.response.EduContentResponse;
import com.finfellows.domain.post.domain.Post;
import com.finfellows.domain.post.domain.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EduContentService {
    private final EduContentRepository eduContentRepository;
    private final PostRepository postRepository;
    private final EduContentBookmarkRepository eduContentBookmarkRepository;

    @Transactional
    public EduContent createEduContent(EduContentResponse request) {
        Post post = new Post();
        postRepository.save(post);

        // 빌더 패턴을 사용하여 EduContent 생성하면서 Post 엔터티를 설정
        EduContent eduContent = EduContent.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .post(post)  // Post 엔터티를 설정
                .build();

        // EduContent 저장
        EduContent savedContent = eduContentRepository.save(eduContent);
        return savedContent;
    }

    public List<EduContentResponse> getAllEduContents(Long userId) {
        List<EduContent> eduContents = eduContentRepository.findAll();
        return eduContents.stream()
                .map(eduContent -> EduContentResponse.builder()
                        .id(eduContent.getId())
                        .title(eduContent.getTitle())
                        .content(eduContent.getContent())
                        .bookmarked(checkBookmarked(userId, eduContent.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public EduContentResponse getEduContent(Long id) {
        EduContent eduContent = eduContentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EduContent not found with id: " + id));

        return EduContentResponse.builder()
                .id(eduContent.getId())
                .title(eduContent.getTitle())
                .content(eduContent.getContent())
                .build();
    }

    @Transactional
    public void deleteEduContent(Long id) {
        EduContent eduContent = eduContentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EduContent not found with id: " + id));

        eduContentRepository.delete(eduContent);
    }

    @Transactional
    public EduContentResponse updateEduContent(Long id, EduContentRequest request) {
        EduContent eduContent = eduContentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EduContent not found with id: " + id));

        eduContent.updateContent(request.getTitle(), request.getContent());

        EduContent updatedContent = eduContentRepository.save(eduContent);

        return EduContentResponse.builder()
                .id(updatedContent.getId())
                .title(updatedContent.getTitle())
                .content(updatedContent.getContent())
                .build();
    }


    private boolean checkBookmarked(Long userId, Long eduContentId) {
        return eduContentBookmarkRepository.existsByUser_IdAndEduContent_Id(userId, eduContentId);
    }
}
