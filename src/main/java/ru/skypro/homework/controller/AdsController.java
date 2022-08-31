package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.security.Principal;

@RestController
@RequestMapping("/ads")
@CrossOrigin(value = "http://localhost:3000")
public class AdsController {
    private final AdsService adsService;

    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        ResponseWrapperAds allAds = adsService.getAllAds();
        if (allAds.getCount() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allAds);
    }

    @PostMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<AdsDto> addAds(@RequestPart("properties") @Valid CreateAds createAds,
                                         @RequestPart("image") @Valid @NotNull MultipartFile file) {
        if (createAds == null) {
            return ResponseEntity.notFound().build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(adsService.createAds(createAds, file, authentication));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(Principal principal) {
        ResponseWrapperAds Ads = adsService.getAdsMe(principal);
        if (Ads.getCount() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Ads);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AdsDto> removeAds(@PathVariable @Positive int id) {
        if (id < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdsDto adsDto = adsService.removeAds(id, authentication);
        if (adsDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<FullAds> getAds(@PathVariable int id) {
        if (id < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        FullAds fullAds = adsService.getAds(id);
        if (fullAds == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(fullAds);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable int id,
                                            @RequestPart("properties") @Valid AdsDto adsDto,
                                            @RequestPart("image") @Valid @NotNull MultipartFile file) {
        if (id < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdsDto ads = adsService.updateAds(id, adsDto, file, authentication);
        if (ads == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adsDto);
    }

    @GetMapping("/title")
    public ResponseEntity<ResponseWrapperAds> getAdsByTitle(@RequestParam String title) {
        if (title.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ResponseWrapperAds responseWrapperAds = adsService.getAdsByTitle(title);
        if (responseWrapperAds == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(responseWrapperAds);
    }

    @GetMapping("/{ad_pk}/comment")
    public ResponseEntity<ResponseWrapperAdsComment> getAdsComments(@PathVariable("ad_pk") @Positive String adPk) {
        int pk = Integer.parseInt(adPk);
        if (pk < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ResponseWrapperAdsComment adsComment = adsService.getAdsComments(pk);
        if (adsComment.getCount() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adsComment);
    }

    @PostMapping("/{ad_pk}/comment")
    public ResponseEntity<AdsCommentDto> addAdsComment(@PathVariable("ad_pk") @Positive String adPk,
                                                       @RequestBody AdsCommentDto adsCommentDto) {
        int pk = Integer.parseInt(adPk);
        if (pk < 0 || adsCommentDto == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        AdsCommentDto adsComment = adsService.addAdsComment(pk, adsCommentDto);
        if (adsComment == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(adsComment);
    }

    @DeleteMapping("/{ad_pk}/comment/{id}")
    public ResponseEntity deleteAdsComment(@PathVariable("ad_pk") String adPk,
                                           @PathVariable int id) {
        int pk = Integer.parseInt(adPk);
        if (pk < 0 || id < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        AdsCommentDto adsCommentDto = adsService.deleteAdsComment(pk, id);
        if (adsCommentDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{ad_pk}/comment/{id}")
    public ResponseEntity<AdsCommentDto> getAdsComment(@PathVariable("ad_pk") String adPk,
                                                       @PathVariable int id) {
        int pk = Integer.parseInt(adPk);
        if (pk < 0 || id < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        AdsCommentDto adsCommentDto = adsService.getAdsComment(pk, id);
        if (adsCommentDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adsCommentDto);
    }


    @PatchMapping("/{ad_pk}/comment/{id}")
    public ResponseEntity<AdsCommentDto> updateAdsComment(@PathVariable("ad_pk") String adPk,
                                                          @PathVariable int id,
                                                          @RequestBody AdsCommentDto adsCommentDto) {
        int pk = Integer.parseInt(adPk);
        if (pk < 0 || id < 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        AdsCommentDto adsComment = adsService.updateAdsComment(pk, id, adsCommentDto);
        if (adsCommentDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adsComment);
    }
}