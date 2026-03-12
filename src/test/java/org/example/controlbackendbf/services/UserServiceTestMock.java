package org.example.controlbackendbf.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.controlbackendbf.api.model.CreateUserRequest;
import org.example.controlbackendbf.entities.UserEntity;
import org.example.controlbackendbf.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
        import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTestMock {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setupSaveAssignsId() {
        // Damit create() eine ID zurückgeben kann (ohne DB)
        lenient().when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity e = inv.getArgument(0);
            setField(e, "id", 7L); // falls kein setId existiert
            return e;
        });
    }

    @Test
    void create_shouldThrowConflict_whenUsernameAlreadyExists() {
        CreateUserRequest req = new CreateUserRequest().name("testuser").email("email@aol.com");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        ResponseStatusException ex = catchThrowableOfType(
                () -> userService.create(req),
                ResponseStatusException.class
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository).existsByUsername("testuser");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void create_shouldThrowBadRequest_whenEmailInvalid() {
        CreateUserRequest req = new CreateUserRequest().name("testuser").email("invalidEmail");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        ResponseStatusException ex = catchThrowableOfType(
                () -> userService.create(req),
                ResponseStatusException.class
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userRepository).existsByUsername("testuser");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void create_shouldThrowConflict_whenEmailAlreadyExists() {
        CreateUserRequest req = new CreateUserRequest().name("testuser").email("email@aol.com");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("email@aol.com")).thenReturn(true);

        ResponseStatusException ex = catchThrowableOfType(
                () -> userService.create(req),
                ResponseStatusException.class
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("email@aol.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void create_shouldSave_andReturnUser() {
        CreateUserRequest req = new CreateUserRequest().name("testuser").email("email@aol.com");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("email@aol.com")).thenReturn(false);

        var result = userService.create(req);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getName()).isEqualTo("testuser");

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("email@aol.com");
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findAll_shouldMapEntitiesToApiModel() {
        UserEntity e1 = new UserEntity();
        setField(e1, "id", 10L);
        e1.setUsername("a");

        UserEntity e2 = new UserEntity();
        setField(e2, "id", 11L);
        e2.setUsername("b");

        when(userRepository.findAll()).thenReturn(List.of(e1, e2));

        var users = userService.findAll();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(10L);
        assertThat(users.get(0).getName()).isEqualTo("a");
        verify(userRepository).findAll();
    }

    @Test
    void findById_shouldReturnUser_whenFound() {
        UserEntity e = new UserEntity();
        setField(e, "id", 99L);
        e.setUsername("x");

        when(userRepository.findById(99L)).thenReturn(Optional.of(e));

        var user = userService.findById(99L);

        assertThat(user.getId()).isEqualTo(99L);
        assertThat(user.getName()).isEqualTo("x");
        verify(userRepository).findById(99L);
    }

    @Test
    void deleteById_shouldThrowEntityNotFound_whenNotExists() {
        when(userRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(5L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository).existsById(5L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteById_shouldDeleteAndFlush_whenExists() {
        when(userRepository.existsById(5L)).thenReturn(true);

        userService.deleteById(5L);

        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).existsById(5L);
        inOrder.verify(userRepository).deleteById(5L);
        inOrder.verify(userRepository).flush();
    }

    @Test
    void updateUserById_shouldUpdateAndReturnUser() {
        UserEntity e = new UserEntity();
        setField(e, "id", 7L);
        e.setUsername("old");

        when(userRepository.findById(7L)).thenReturn(Optional.of(e));

        CreateUserRequest req = new CreateUserRequest().name("newName").email("email@aol.com");

        var updated = userService.updateUserById(7L, req);

        assertThat(updated.getId()).isEqualTo(7L);
        assertThat(updated.getName()).isEqualTo("newName");
        assertThat(e.getUsername()).isEqualTo("newName");

        verify(userRepository).findById(7L);
        verify(userRepository).save(e);
    }

    // --- helper: set private field via reflection (für id) ---
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot set field " + fieldName, ex);
        }
    }
}
