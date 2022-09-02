package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class AdsDto {
    @Positive
    private int author;
    private String image;
    @Positive
    private int pk;
    @Positive
    private int price;
    @NotBlank
    @Size(min = 8)
    private String title;
}
