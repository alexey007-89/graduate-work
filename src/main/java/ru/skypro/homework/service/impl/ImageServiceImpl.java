package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.ImageMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.ImageRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ImageServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageRepository imageRepository;
    private final AdsRepository adsRepository;
    private final ImageMapper imageMapper;

    public ImageServiceImpl(ImageRepository imageRepository, AdsRepository adsRepository, ImageMapper imageMapper) {
        this.imageRepository = imageRepository;
        this.adsRepository = adsRepository;
        this.imageMapper = imageMapper;
    }

    public ImageDto uploadAdsImage(Integer idAds, MultipartFile imageFile) throws IOException, NotFoundException {
        Ads ads = adsRepository.findById(idAds).orElseThrow(NotFoundException::new);

        Image image = new Image();
        image.setAds(ads);
        image.setFileSize((int) imageFile.getSize());
        image.setMediaType(imageFile.getContentType());
        image.setData(imageFile.getBytes());
        imageRepository.save(image);
        return imageMapper.imageToImageDto(image, ads);
    }

    public List<ResponseEntity<byte[]>> downloadImage(Long idAds) {
        List<Image> images = imageRepository.findAllByAdsPk(idAds);
        if (images.size() == 0) {
            Throwable throwable = new NotFoundException("Для объявления с идентификатором " + idAds + " картинок нет.");
        }
        ArrayList<ResponseEntity<byte[]>> listImages = new ArrayList<ResponseEntity<byte[]>>();
        for (Image image : images) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
            headers.setContentLength(image.getData().length);
            listImages.add(ResponseEntity.status(HttpStatus.OK).headers(headers).body(image.getData()));
        }
        return listImages;
    }

    public Image findImageById(Long idImage)  {
        return imageRepository.findById(idImage).orElseThrow(ru.skypro.homework.exception.NotFoundException::new);
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }

    public void deleteAll() {
        imageRepository.deleteAll();
    }
}
