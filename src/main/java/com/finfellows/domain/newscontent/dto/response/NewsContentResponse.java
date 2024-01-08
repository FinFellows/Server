package com.finfellows.domain.newscontent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsContentResponse {
    private Long id;
    private String title;
    private String content;
    private boolean bookmarked;
}
