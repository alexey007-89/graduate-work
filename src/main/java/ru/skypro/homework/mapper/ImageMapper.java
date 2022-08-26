package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.ImageDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageMapper zINSTANCE = Mappers.getMapper(ImageMapper.class);

    @Mapping(source = "ads.pk", target = "idAds")
    @Mapping(source = "image.id", target = "pk")
    @Mapping(source = "image.fileSize", target = "fileSize")
    @Mapping(source = "image.mediaType", target = "mediaType")
    ImageDto imageToImageDto(Image image, Ads ads);
}
