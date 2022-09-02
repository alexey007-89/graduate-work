package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.service.impl.ImageServiceImpl;


@CrossOrigin(value = "http://localhost:3000")
@RestController

public class ImageController {

    private final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageServiceImpl imageServiceImpl;


    public ImageController(ImageServiceImpl imageServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;
    }

    @Operation(
            tags = "Фото объявлений (ImageController)",
            summary = "Получение изображения объявления по id (getAdsAvatar)"
    )
    @GetMapping(value = "/api/{id}/image", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getAdsAvatar(@PathVariable("id") String id) {

        return ResponseEntity.ok().body(imageServiceImpl.downloadImage(id));
    }


}
