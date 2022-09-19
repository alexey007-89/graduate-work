package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.repository.ImageRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ImageServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public byte[] downloadImage(String id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        byte[] images = null;
        if (imageOptional.isPresent()) {
            images = imageOptional.get().getData();
        }
        return images;
    }


    public String saveImage(MultipartFile file) {
        Image image = new Image();
        try {
            byte[] bytes = file.getBytes();
            image.setData(bytes);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        image.setId(UUID.randomUUID().toString());
        image.setFileSize((int) file.getSize());
        image.setMediaType(file.getContentType());
        Image savedImage = imageRepository.save(image);
        return savedImage.getId();
    }
}
