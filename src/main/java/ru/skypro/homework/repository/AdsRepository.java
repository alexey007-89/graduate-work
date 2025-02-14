package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.User;

import java.util.List;

@Repository
public interface AdsRepository extends JpaRepository<Ads, Integer> {
    @Query(value = "SELECT * FROM ads WHERE title ilike '%' || ?1 || '%' ", nativeQuery = true)
    List<Ads> findLikeTitle(String title);

    List<Ads> findAdsByAuthorOrderByPk(User author);
}
