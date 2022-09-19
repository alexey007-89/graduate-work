package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class AdsController {
    private final AdsService adsService;

    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @Operation(
            tags = "Объявления (AdsController)",
            summary = "Получение списка всех объявлений (getAllAds)"
    )
    @GetMapping
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        ResponseWrapperAds allAds = adsService.getAllAds();
        if (allAds.getCount() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allAds);
    }

    @Operation(
            tags = "Объявления (AdsController)",
            summary = "Добавление объявления (addAds)"
    )
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

    @Operation(
            tags = "Объявления (AdsController)",
            summary = "Получение списка объявлений пользователя (getAdsMe)"
    )
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(Principal principal) {
        ResponseWrapperAds Ads = adsService.getAdsMe(principal);
        return ResponseEntity.ok(Ads);
    }

    @Operation(
            tags = "Объявления (AdsController)",
            summary = "Удаление объявления по id (removeAds)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<AdsDto> removeAds(@PathVariable @Positive int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdsDto adsDto = adsService.removeAds(id, authentication);
        if (adsDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            tags = "Объявления (AdsController)",
            summary = "Получение объявления по id (getAds)"
    )
    @GetMapping("/{id}")
    public ResponseEntity<FullAds> getAds(@PathVariable @Positive int id) {
        FullAds fullAds = adsService.getAds(id);
        if (fullAds == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(fullAds);
    }

    @Operation(
            tags = "Объявления (AdsController)",
            summary = "Редактирование объявления по id (updateAds)"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable @Positive int id,
                                            @RequestPart("properties") @Valid AdsDto adsDto,
                                            @RequestPart("image") @Valid @NotNull MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdsDto ads = adsService.updateAds(id, adsDto, file, authentication);
        if (ads == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adsDto);
    }

    @Operation(
            tags = "Объявления (AdsController)",
            summary = "Получение списка объявлений в результате поиска по заголовку(getAdsByTitle)"
    )
    @GetMapping("/title")
    public ResponseEntity<ResponseWrapperAds> getAdsByTitle(@RequestParam(required = false) String title) {
        ResponseWrapperAds responseWrapperAds = adsService.getAdsByTitle(title);
        if (responseWrapperAds == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(responseWrapperAds);
    }

    @Operation(
            tags = "Отзывы (AdsController)",
            summary = "Получение списка отзывов объявления (getAdsComments)"
    )
    @GetMapping("/{ad_pk}/comments")
    public ResponseEntity<ResponseWrapperAdsComment> getAdsComments(@PathVariable("ad_pk") @Positive String adPk) {
        int pk = Integer.parseInt(adPk);
        ResponseWrapperAdsComment adsComment = adsService.getAdsComments(pk);
        if (adsComment.getCount() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adsComment);
    }

    @Operation(
            tags = "Отзывы (AdsController)",
            summary = "Добавление отзыва к объявлению (addAdsComment)"
    )
    @PostMapping("/{ad_pk}/comments")
    public ResponseEntity<AdsCommentDto> addAdsComment(@PathVariable("ad_pk") @Positive String adPk,
                                                       @RequestBody AdsCommentDto adsCommentDto) {
        int pk = Integer.parseInt(adPk);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdsCommentDto adsComment = adsService.addAdsComment(pk, adsCommentDto, authentication.getName());
        if (adsComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adsComment);
    }

    @Operation(
            tags = "Отзывы (AdsController)",
            summary = "Удаление отзыва по id (deleteAdsComment)"
    )
    @DeleteMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<Void> deleteAdsComment(@PathVariable("ad_pk") @Positive String adPk,
                                                 @PathVariable @Positive int id) {
        int pk = Integer.parseInt(adPk);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdsCommentDto adsCommentDto = adsService.deleteAdsComment(pk, id, authentication);
        if (adsCommentDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            tags = "Отзывы (AdsController)",
            summary = "Получение отзыва к объявлению по id (getAdsComment)"
    )
    @GetMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<AdsCommentDto> getAdsComment(@PathVariable("ad_pk") @Positive String adPk,
                                                       @PathVariable @Positive int id) {
        int pk = Integer.parseInt(adPk);
        AdsCommentDto adsCommentDto = adsService.getAdsComment(pk, id);
        if (adsCommentDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adsCommentDto);
    }

    @Operation(
            tags = "Отзывы (AdsController)",
            summary = "Редактирование отзыва по id (updateAdsComment)"
    )
    @PatchMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<AdsCommentDto> updateAdsComment(@PathVariable("ad_pk") @Positive String adPk,
                                                          @PathVariable @Positive int id,
                                                          @RequestBody @Valid AdsCommentDto adsCommentDto) {
        int pk = Integer.parseInt(adPk);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdsCommentDto adsComment = adsService.updateAdsComment(pk, id, adsCommentDto, authentication);
        if (adsCommentDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adsComment);
    }
}