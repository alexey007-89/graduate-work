package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findRoleByRoleName(String roleName);
}
