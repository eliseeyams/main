package org.example.controlbackendbf.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", schema = "core") // oder default_schema setzen, s.u.
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String username;
}
