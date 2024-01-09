package com.finfellows.domain.bookmark.dto;

import com.finfellows.domain.bookmark.domain.CmaBookmark;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CmaBookmarkRes {
    private String companyName;
    private String productName;
    private String cmaType;
    private String maturityInterestRate;
    private String specialCondition;


    @Builder
    public CmaBookmarkRes(String companyName, String productName, String cmaType, String maturityInterestRate, String specialCondition) {
        this.companyName = companyName;
        this.productName = productName;
        this.cmaType = cmaType;
        this.maturityInterestRate = maturityInterestRate;
        this.specialCondition = specialCondition;
    }



    public static List<CmaBookmarkRes> toDto(List<CmaBookmark> cmaBookmarks) {
        return cmaBookmarks.stream()
                .map(bookmark -> CmaBookmarkRes.builder()
                        .cmaType(bookmark.getCma().getCmaType())
                        .productName(bookmark.getCma().getProductName())
                        .companyName(bookmark.getCma().getCompanyName())
                        .maturityInterestRate(bookmark.getCma().getMaturityInterestRate())
                        .specialCondition(bookmark.getCma().getSpecialCondition())
                        .build())
                .collect(Collectors.toList());
    }
}