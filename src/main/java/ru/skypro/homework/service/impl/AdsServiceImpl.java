package ru.skypro.homework.service.impl;

import org.mapstruct.factory.Mappers;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.AdsComment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.NoAccessException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsCommentRepository;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsService;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdsServiceImpl implements AdsService {
    private final AdsRepository adsRepository;
    private final AdsCommentRepository adsCommentRepository;
    private final UserRepository userRepository;
    private final AdsMapper mapper = Mappers.getMapper(AdsMapper.class);
    private final ImageServiceImpl imageServiceImpl;

    public AdsServiceImpl(AdsRepository adsRepository, AdsCommentRepository adsCommentRepository, UserRepository userRepository,  ImageServiceImpl imageServiceImpl) {
        this.adsRepository = adsRepository;
        this.adsCommentRepository = adsCommentRepository;
        this.userRepository = userRepository;
        this.imageServiceImpl = imageServiceImpl;
    }

    @Override
    public ResponseWrapperAds getAllAds() {
        List<Ads> adsList = adsRepository.findAll();
        return getResponseWrapperAds(adsList);
    }

    @Override
    public AdsDto createAds(CreateAds createAds, MultipartFile file, Authentication authentication) {
        Ads ads = mapper.createAdsToAds(createAds);
        ads.setAuthor(userRepository.findUserByEmail(authentication.getName()).orElseThrow(NoSuchElementException::new));
        ads.setImage("/api/" + imageServiceImpl.saveImage(file) + "/image");
        adsRepository.save(ads);
        return mapper.adsToAdsDto(ads);
    }

    @Override
    public ResponseWrapperAds getAdsMe(Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Ads> adsList = adsRepository.findAdsByAuthorOrderByPk(user);
        return getResponseWrapperAds(adsList);
    }

    @Override
    public ResponseWrapperAdsComment getAdsComments(int pk) {
        Ads ads = adsRepository.findById(pk).orElseThrow(NoSuchElementException::new);
        List<AdsComment> adsCommentList = adsCommentRepository.findByPk(ads);
        List<AdsCommentDto> adsCommentDtoList = mapper.adsCommentToAdsCommentDto(adsCommentList);
        ResponseWrapperAdsComment responseWrapperAdsComment = new ResponseWrapperAdsComment();
        if (!adsCommentDtoList.isEmpty()) {
            responseWrapperAdsComment.setCount(adsCommentDtoList.size());
            responseWrapperAdsComment.setResults(adsCommentDtoList);
        }
        return responseWrapperAdsComment;
    }

    @Override
    public AdsCommentDto addAdsComment(int pk, AdsCommentDto adsCommentDto) {
        AdsComment adsComment = new AdsComment();
        adsComment.setAuthor(userRepository.findById(adsCommentDto.getAuthor()).orElseThrow(NoSuchElementException::new));
        adsComment.setPk(adsRepository.findById(adsCommentDto.getPk()).orElseThrow(NoSuchElementException::new));
        adsComment.setCreatedAt(OffsetDateTime.now());
        adsComment.setText(adsCommentDto.getText());
        adsCommentRepository.save(adsComment);
        return adsCommentDto;
    }

    @Override
    public AdsCommentDto deleteAdsComment(int pk, int id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        Ads ads = adsRepository.findById(pk).orElseThrow(NoSuchElementException::new);
        AdsComment adsComment = adsCommentRepository.findAdsCommentByPkAndAuthor(ads, user).orElseThrow(NoSuchElementException::new);
        adsCommentRepository.delete(adsComment);
        return mapper.adsCommentToAdsCommentDto(adsComment);
    }

    @Override
    public AdsCommentDto getAdsComment(int pk, int id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        Ads ads = adsRepository.findById(pk).orElseThrow(NoSuchElementException::new);
        AdsComment adsComment = adsCommentRepository.findAdsCommentByPkAndAuthor(ads, user).orElseThrow(NoSuchElementException::new);
        return mapper.adsCommentToAdsCommentDto(adsComment);
    }

    @Override
    public AdsCommentDto updateAdsComment(int pk, int id, AdsCommentDto adsCommentDto) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        Ads ads = adsRepository.findById(pk).orElseThrow(NoSuchElementException::new);
        AdsComment adsComment = adsCommentRepository.findAdsCommentByPkAndAuthor(ads, user).orElseThrow(NoSuchElementException::new);
        adsComment.setAuthor(userRepository.findById(id).orElseThrow(NoSuchElementException::new));
        adsComment.setPk(adsCommentRepository.findById(pk).orElseThrow(NoSuchElementException::new).getPk());
        adsComment.setText(adsCommentDto.getText());
        adsComment.setCreatedAt(adsCommentDto.getCreatedAt());
        adsCommentRepository.save(adsComment);
        return adsCommentDto;
    }

    @Override
    public AdsDto removeAds(int id, Authentication authentication) {
        Ads ads = adsRepository.findById(id).orElseThrow(NoSuchElementException::new);
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().contains("ADMIN"))
                || authentication.getName().equals(ads.getAuthor().getEmail())) {
            adsRepository.delete(ads);
            return mapper.adsToAdsDto(ads);
        } else {
            throw new NoAccessException();
        }
    }

    @Override
    public FullAds getAds(int id) {
        Ads ads = adsRepository.findById(id).orElseThrow(NoSuchElementException::new);
        User user = userRepository.findById(ads.getAuthor().getId()).orElseThrow(NoSuchElementException::new);
        FullAds fullAds = new FullAds();
        fullAds.setAuthorFirstName(user.getFirstName());
        fullAds.setAuthorLastName(user.getLastName());
        fullAds.setDescription(ads.getDescription());
        fullAds.setImage(ads.getImage());
        fullAds.setEmail(user.getEmail());
        fullAds.setPhone(user.getPhone());
        fullAds.setPk(ads.getPk());
        fullAds.setPrice(ads.getPrice());
        fullAds.setTitle(ads.getTitle());
        return fullAds;
    }

    @Override
    public AdsDto updateAds(int id, AdsDto adsDto, MultipartFile file, Authentication authentication) {
        Ads ads = adsRepository.findById(id).orElseThrow(NoSuchElementException::new);
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().contains("ADMIN"))
                || authentication.getName().equals(ads.getAuthor().getEmail())) {
            ads.setImage("/api/" + imageServiceImpl.saveImage(file) + "/image");
            ads.setTitle(adsDto.getTitle());
            ads.setPrice(adsDto.getPrice());
            ads.setAuthor(userRepository.findById(adsDto.getAuthor()).orElseThrow(NoSuchElementException::new));
            return adsDto;
        } else {
            throw new NoAccessException();
        }
    }

    @Override
    public ResponseWrapperAds getAdsByTitle(String title) {
        List<Ads> adsList = adsRepository.findLikeTitle(title);
        return getResponseWrapperAds(adsList);

    }

    private ResponseWrapperAds getResponseWrapperAds(List<Ads> adsList) {
        List<AdsDto> adsDtoList = mapper.adsToAdsDto(adsList);
        ResponseWrapperAds responseWrapperAds = new ResponseWrapperAds();
        if (!adsDtoList.isEmpty()) {
            responseWrapperAds.setCount(adsDtoList.size());
            responseWrapperAds.setResults(adsDtoList);
        }
        return responseWrapperAds;
    }

}
