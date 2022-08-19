package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String roleName;
    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
