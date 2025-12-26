package org.example.controlbackendbf.services;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.controlbackendbf.api.model.CreateUserRequest;
import org.example.controlbackendbf.api.model.User;
import org.example.controlbackendbf.entities.UserEntity;
import org.example.controlbackendbf.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class UserService {

    private final UserRepository userRepository;

    public User create(CreateUserRequest body){
        var e = new UserEntity();
            e.setUsername(body.getName());
            userRepository.save(e);

        return new User().id(e.getId()).name(body.getName());
    }

    public List<User> findAll(){
      return  userRepository.findAll().stream().map(e ->
                new User().id(e.getId()).name(e.getUsername())).toList();
    }

    public User findById(Long id){
        var e = userRepository.findById(id).orElseThrow();
        return new User().id(e.getId()).name(e.getUsername());
    }

    @Transactional
    public void deleteById(Long id){
        if(!userRepository.existsById(id)){
            throw new EntityNotFoundException("User with " + id + " not found");
        }

        userRepository.deleteById(id);
        userRepository.flush();
    }

    @Transactional
    public User updateUserById(Long id, CreateUserRequest body){
        var e  = userRepository.findById(id).orElseThrow();
        e.setUsername(body.getName());
        userRepository.save(e);
        return new User().id(e.getId()).name(body.getName());
    }


}
