package org.example.controlbackendbf.repositories;

import org.example.controlbackendbf.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <UserEntity, Long>{
}
