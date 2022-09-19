package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skypro.homework.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    User findByUsername(String username);

    Optional<User> findUserByEmail(String username);
}
