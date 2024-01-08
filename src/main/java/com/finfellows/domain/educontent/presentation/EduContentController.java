package com.finfellows.domain.educontent.presentation;

import com.finfellows.domain.educontent.application.EduContentService;
import com.finfellows.domain.educontent.domain.EduContent;
import com.finfellows.domain.educontent.dto.request.EduContentRequest;
import com.finfellows.domain.educontent.dto.response.EduContentResponse;
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

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learn/edu")
@Tag(name = "EduContent", description = "EduContent API")
public class EduContentController {
    private final EduContentService eduContentService;

    @Operation(summary = "교육콘텐츠 저장", description = "교육콘텐츠를 저장합니다.")
    @ApiResponse(responseCode = "201", description = "교육콘텐츠 저장 성공", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EduContentResponse.class)))
    })
    @PostMapping("")
    public ResponseEntity<EduContent> saveEduContent(@RequestBody EduContentResponse request) {
        EduContent response = eduContentService.createEduContent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "교육콘텐츠 전체 목록 조회", description = "교육콘텐츠 전체 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "E교육콘텐츠 목록 조회 성공", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EduContentResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<EduContentResponse>> getAllEduContents(@CurrentUser UserPrincipal userPrincipal) {
        List<EduContentResponse> responseList = eduContentService.getAllEduContents(userPrincipal.getId());
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "교육콘텐츠 상세 내용 조회", description = "교육콘텐츠 상세 내용을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "교육콘텐츠 조회 성공", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EduContentResponse.class))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EduContentResponse> getEduContent(@PathVariable Long id) {
        EduContentResponse response = eduContentService.getEduContent(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "교육콘텐츠 삭제", description = "교육콘텐츠를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "교육콘텐츠 삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEduContent(@PathVariable Long id) {
        eduContentService.deleteEduContent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "교육콘텐츠 수정", description = "교육콘텐츠를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "교육콘텐츠 수정 성공", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = EduContentResponse.class))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EduContentResponse> updateEduContent(@PathVariable Long id, @RequestBody EduContentRequest request) {
        EduContentResponse updatedContent = eduContentService.updateEduContent(id, request);
        return new ResponseEntity<>(updatedContent, HttpStatus.OK);
    }
}