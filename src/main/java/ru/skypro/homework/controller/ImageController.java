package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.ImageDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.ImageServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@Validated
@RequestMapping("/image")

public class ImageController {

    private final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageServiceImpl imageServiceImpl;
    private final AdsServiceImpl adsServiceImpl;


    public ImageController(ImageServiceImpl imageServiceImpl, AdsServiceImpl adsServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;
        this.adsServiceImpl = adsServiceImpl;
    }


    @PostMapping
    public ResponseEntity<AdsDto> createAds(@RequestPart("properties") @Valid CreateAds ads,
                                         @RequestPart("image") @Valid @NotNull MultipartFile file) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(adsServiceImpl.createAds(ads, file, authentication));
    }

    @GetMapping(value = "/api/image/{id}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getAdsAvatar(@PathVariable("id") Long id) {

        return (ResponseEntity<byte[]>) imageServiceImpl.downloadImage(id);
    }


    @DeleteMapping
    public ResponseEntity<Image> delete(@RequestParam(required = false) @Min(1) Long id) {
        if (id != null) {
            imageServiceImpl.deleteImage(id);
            return ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
