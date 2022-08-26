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
import ru.skypro.homework.dto.ImageDto;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.service.impl.ImageServiceImpl;

import javax.validation.constraints.Min;
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

    public ImageController(ImageServiceImpl imageServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;
    }



    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDto> uploadAdsImage(@PathVariable @Min(1) Integer id,
                                                     @RequestParam MultipartFile adsImage) {

        ImageDto imageDto;
        try{
            imageDto = imageServiceImpl.uploadAdsImage(id, adsImage);
        } catch (IOException | NotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(imageDto);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable @Min(1) Long id) {
        Image image = imageServiceImpl.findImageById(id);
        if (image.getMediaType() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        headers.setContentLength(image.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(image.getData());
    }


    @GetMapping(value = "/all/{idAds}")
    public List<ResponseEntity<byte[]>> downloadImageByAds(@PathVariable @Min(1) Long id) {

        return imageServiceImpl.downloadImage(id);
    }



    @DeleteMapping
    public ResponseEntity<Image> delete(@RequestParam(required = false) @Min(1) Long id,
                                          @RequestParam(required = false) boolean all) {
        logger.info("Method delete is running");
        if (all) {
            imageServiceImpl.deleteAll();
            return ok().build();
        }
        if (id != null) {
            imageServiceImpl.deleteImage(id);
            return ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
