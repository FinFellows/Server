package com.finfellows.domain.newscontent.presentation;

import com.finfellows.domain.newscontent.application.NewsContentService;
import com.finfellows.domain.newscontent.domain.NewsContent;
import com.finfellows.domain.newscontent.dto.request.NewsContentRequest;
import com.finfellows.domain.newscontent.dto.response.NewsContentResponse;
import com.finfellows.global.config.security.token.CurrentUser;
import com.finfellows.global.config.security.token.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learn/news")
@Tag(name = "NewsContent", description = "NewsContent API")
public class NewsContentController {
    private final NewsContentService newsContentService;

    @Operation(summary = "뉴스콘텐츠 저장", description = "뉴스콘텐츠를 저장합니다.")
    @ApiResponse(responseCode = "201", description = "뉴스콘텐츠 저장 성공", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NewsContentResponse.class)))
    })
    @PostMapping("")
    public ResponseEntity<NewsContent> saveNewsContent(@RequestBody NewsContentResponse request) {
        NewsContent response = newsContentService.createNewsContent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "뉴스콘텐츠 전체 목록 조회", description = "뉴스콘텐츠 전체 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "뉴스콘텐츠 목록 조회 성공", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NewsContentResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<NewsContentResponse>> getAllNewsContents(@CurrentUser UserPrincipal userPrincipal) {
        List<NewsContentResponse> responseList = newsContentService.getAllNewsContents(userPrincipal.getId());
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "뉴스콘텐츠 상세 내용 조회", description = "뉴스콘텐츠 상세 내용을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "뉴스콘텐츠 조회 성공", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = NewsContentResponse.class))
    })
    @GetMapping("/{id}")
    public ResponseEntity<NewsContentResponse> getNewsContent(@PathVariable Long id) {
        NewsContentResponse response = newsContentService.getNewsContent(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "뉴스콘텐츠 삭제", description = "뉴스콘텐츠를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "뉴스콘텐츠 삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNewsContent(@PathVariable Long id) {
        newsContentService.deleteNewsContent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "뉴스콘텐츠 수정", description = "뉴스콘텐츠를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "뉴스콘텐츠 수정 성공", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = NewsContentResponse.class))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<NewsContentResponse> updateNewsContent(@PathVariable Long id, @RequestBody NewsContentRequest request) {
        NewsContentResponse updatedContent = newsContentService.updateNewsContent(id, request);
        return new ResponseEntity<>(updatedContent, HttpStatus.OK);
    }
}