package ru.skypro.homework.service.impl;

import org.mapstruct.factory.Mappers;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.AdsComment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdsCommentNotFoundException;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.NoAccessException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsCommentRepository;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsService;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AdsServiceImpl implements AdsService {
    private final AdsRepository adsRepository;
    private final AdsCommentRepository adsCommentRepository;
    private final UserRepository userRepository;
    private final AdsMapper mapper = Mappers.getMapper(AdsMapper.class);
    private final ImageServiceImpl imageServiceImpl;

    public AdsServiceImpl(AdsRepository adsRepository, AdsCommentRepository adsCommentRepository, UserRepository userRepository, ImageServiceImpl imageServiceImpl) {
        this.adsRepository = adsRepository;
        this.adsCommentRepository = adsCommentRepository;
        this.userRepository = userRepository;
        this.imageServiceImpl = imageServiceImpl;
    }

    /**
     * Get a list of all adverts
     *
     * @return ResponseWrapperAds(DTO)
     */
    @Override
    public ResponseWrapperAds getAllAds() {
        List<Ads> adsList = adsRepository.findAll();
        return getResponseWrapperAds(adsList);
    }

    /**
     * Create advert
     *
     * @param createAds      - advert information from client
     * @param file           - advert image from client
     * @param authentication - user authentication
     * @return created advert as AdsDto (DTO)
     */
    @Override
    public AdsDto createAds(CreateAds createAds, MultipartFile file, Authentication authentication) {
        Ads ads = mapper.createAdsToAds(createAds);
        ads.setAuthor(userRepository.findUserByEmail(authentication.getName()).orElseThrow(UserNotFoundException::new));
        ads.setImage("/api/" + imageServiceImpl.saveImage(file) + "/image");
        adsRepository.save(ads);
        return mapper.adsToAdsDto(ads);
    }

    /**
     * Get a list of all user's adverts
     *
     * @param principal - user's principal
     * @return ResponseWrapperAds(DTO)
     */
    @Override
    public ResponseWrapperAds getAdsMe(Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Ads> adsList = adsRepository.findAdsByAuthorOrderByPk(user);
        return getResponseWrapperAds(adsList);
    }

    /**
     * Remove ads by Id
     *
     * @param id             - ads Id
     * @param authentication - user's authentication
     * @return deleted ads AdsDto(DTO)
     */
    @Override
    public AdsDto removeAds(int id, Authentication authentication) {
        Ads ads = adsRepository.findById(id).orElseThrow(AdsNotFoundException::new);
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().contains("ADMIN"))
                || authentication.getName().equals(ads.getAuthor().getEmail())) {
            adsRepository.deleteById(id);
            return mapper.adsToAdsDto(ads);
        } else {
            throw new NoAccessException();
        }
    }

    /**
     * Get advert by ID
     *
     * @param id - advert Id
     * @return found advert as FullAdsDto (DTO)
     */
    @Override
    public FullAds getAds(int id) {
        Ads ads = adsRepository.findById(id).orElseThrow(AdsNotFoundException::new);
        User user = userRepository.findById(ads.getAuthor().getId()).orElseThrow(UserNotFoundException::new);
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

    /**
     * Update advert by Id
     *
     * @param id             - ads Id
     * @param adsDto         - advert information as AdsDto (DTO) from client
     * @param file           - advert image from client
     * @param authentication - user's authentication
     * @return updated ads as AdsDto (DTO) or throw exception
     */
    @Override
    public AdsDto updateAds(int id, AdsDto adsDto, MultipartFile file, Authentication authentication) {
        Ads ads = adsRepository.findById(id).orElseThrow(AdsNotFoundException::new);
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().contains("ADMIN"))
                || authentication.getName().equals(ads.getAuthor().getEmail())) {
            ads.setImage("/api/" + imageServiceImpl.saveImage(file) + "/image");
            ads.setTitle(adsDto.getTitle());
            ads.setPrice(adsDto.getPrice());
            ads.setAuthor(userRepository.findById(adsDto.getAuthor()).orElseThrow(UserNotFoundException::new));
            return adsDto;
        } else {
            throw new NoAccessException();
        }
    }

    /**
     * Find adverts by keyword(s) in ads title
     *
     * @param title - keyword(s) from client
     * @return list of adverts finding by keyword(s) as ResponseWrapperAds (DTO)
     */
    @Override
    public ResponseWrapperAds getAdsByTitle(String title) {
        List<Ads> adsList = adsRepository.findLikeTitle(title);
        return getResponseWrapperAds(adsList);
    }

    /**
     * Get all AdsComments by Ads Id
     *
     * @param pk - ads id from client
     * @return list of all comments of a specific advert as ResponseWrapperAdsComment (DTO)
     */
    @Override
    public ResponseWrapperAdsComment getAdsComments(int pk) {
        Ads ads = adsRepository.findById(pk).orElseThrow(AdsNotFoundException::new);
        List<AdsComment> adsCommentList = adsCommentRepository.findByPk(ads);
        List<AdsCommentDto> adsCommentDtoList = mapper.adsCommentToAdsCommentDto(adsCommentList);
        ResponseWrapperAdsComment responseWrapperAdsComment = new ResponseWrapperAdsComment();
        if (!adsCommentDtoList.isEmpty()) {
            responseWrapperAdsComment.setCount(adsCommentDtoList.size());
            responseWrapperAdsComment.setResults(adsCommentDtoList);
        }
        return responseWrapperAdsComment;
    }

    /**
     * Create comment by ads Id
     *
     * @param pk            - ads Id
     * @param adsCommentDto - comment information from client
     * @param username      - Username from authentication
     * @return created comment as AdsCommentDto (DTO)
     */
    @Override
    public AdsCommentDto addAdsComment(int pk, AdsCommentDto adsCommentDto, String username) {
        AdsComment adsComment = new AdsComment();
        adsComment.setAuthor(userRepository.findUserByEmail(username).orElseThrow(UserNotFoundException::new));
        adsComment.setPk(adsRepository.findById(pk).orElseThrow(AdsNotFoundException::new));
        adsComment.setCreatedAt(OffsetDateTime.now());
        adsComment.setText(adsCommentDto.getText());
        adsCommentRepository.save(adsComment);
        return adsCommentDto;
    }

    /**
     * Create comment by adsComment Id and ads Id
     *
     * @param pk             - ads Id
     * @param id             - adsComment Id
     * @param authentication - user's authentication
     * @return deleted comment as AdsCommentDto (DTO)
     */
    @Override
    public AdsCommentDto deleteAdsComment(int pk, int id, Authentication authentication) {
        AdsComment adsComment = adsCommentRepository.findById(id).orElseThrow(AdsCommentNotFoundException::new);
        Ads ads = adsRepository.findById(pk).orElseThrow(AdsNotFoundException::new);
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().contains("ADMIN"))
                || authentication.getName().equals(ads.getAuthor().getEmail())) {
            adsCommentRepository.deleteById(id);
            return mapper.adsCommentToAdsCommentDto(adsComment);
        } else {
            throw new NoAccessException();
        }
    }

    /**
     * Get comment by adsComment Id and ads Id
     *
     * @param pk - ads Id
     * @param id - adsComment Id
     * @return comment as AdsCommentDto (DTO)
     */
    @Override
    public AdsCommentDto getAdsComment(int pk, int id) {
        adsRepository.findById(pk).orElseThrow(AdsNotFoundException::new);
        AdsComment adsComment = adsCommentRepository.findById(id).orElseThrow(AdsCommentNotFoundException::new);
        return mapper.adsCommentToAdsCommentDto(adsComment);
    }

    /**
     * Update comment by adsComment Id and ads Id
     *
     * @param pk             - ads Id
     * @param id             - adsComment Id
     * @param adsCommentDto  - comment information from client
     * @param authentication - user's authentication
     * @return updated comment as AdsCommentDto (DTO)
     */
    @Override
    public AdsCommentDto updateAdsComment(int pk, int id, AdsCommentDto adsCommentDto, Authentication authentication) {
        AdsComment adsComment = adsCommentRepository.findById(id).orElseThrow(AdsCommentNotFoundException::new);
        Ads ads = adsRepository.findById(pk).orElseThrow(AdsNotFoundException::new);
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().contains("ADMIN"))
                || authentication.getName().equals(ads.getAuthor().getEmail())) {
            adsComment.setAuthor(userRepository.findById(adsCommentDto.getAuthor()).orElseThrow(UserNotFoundException::new));
            adsComment.setPk(ads);
            adsComment.setText(adsCommentDto.getText());
            adsComment.setCreatedAt(OffsetDateTime.now());
            adsCommentRepository.save(adsComment);
            return adsCommentDto;
        } else {
            throw new NoAccessException();
        }
    }

    /**
     * Map List of ads to ResponseWrapperAds (DTO)
     *
     * @param adsList - list of ads
     * @return List of ads as ResponseWrapperAds (DTO)
     */
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
