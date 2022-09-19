package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class CreateAds {
    @NotBlank
    private String description;
    private String image;
    @Positive
    private int price;
    @NotBlank
    @Size(min = 3)
    private String title;
}
