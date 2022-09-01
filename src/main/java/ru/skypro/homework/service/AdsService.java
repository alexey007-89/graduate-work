package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.security.Principal;

public interface AdsService {
    ResponseWrapperAds getAllAds();

    AdsDto createAds(CreateAds createAds, MultipartFile file, Authentication authentication);

    ResponseWrapperAds getAdsMe(Principal principal);

    ResponseWrapperAdsComment getAdsComments(int pk);

    AdsCommentDto addAdsComment(int pk, AdsCommentDto adsCommentDto);

    AdsCommentDto deleteAdsComment(int pk, int id);

    AdsCommentDto getAdsComment(int pk, int id);

    AdsCommentDto updateAdsComment(int pk, int id, AdsCommentDto adsCommentDto);

    AdsDto removeAds(int id, Authentication authentication);

    FullAds getAds(int id);

    AdsDto updateAds(int id, AdsDto adsDto, MultipartFile file, Authentication authentication);

    ResponseWrapperAds getAdsByTitle(String title);

}
