package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.AdsComment;
import ru.skypro.homework.entity.User;

import java.util.List;
import java.util.Optional;

public interface AdsCommentRepository extends JpaRepository<AdsComment, Integer> {
    List<AdsComment> findByPk(Ads pk);

    Optional<AdsComment> findAdsCommentByPkAndAuthor(Ads pk, User author);
}
