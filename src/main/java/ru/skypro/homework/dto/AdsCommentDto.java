package ru.skypro.homework.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AdsCommentDto {
    private int author;
    private OffsetDateTime createdAt;
    private int pk;
    private String text;
}
