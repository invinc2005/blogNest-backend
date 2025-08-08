package com.example.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthlyPostCountDto {

    private String month;
    private long postCount;

    public MonthlyPostCountDto(String month, Long postCount) {
        this.month = month;
        this.postCount = (postCount != null) ? postCount : 0L;
    }
}