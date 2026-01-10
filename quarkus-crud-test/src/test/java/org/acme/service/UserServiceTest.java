package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.NotFoundException;
import org.acme.mapper.UserEntityMapper;
import org.acme.model.User;
import org.acme.repository.entity.UserEntity;
import org.acme.repository.repository.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @InjectMock
    UserRepository repository;

    @InjectMock
    UserEntityMapper userEntityMapper;

    private User user;
    private UserEntity userEntity;
    private ObjectId objectId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectId = new ObjectId();
        user = new User();
        user.setId(objectId.toString());
        user.setName("Test User");

        userEntity = new UserEntity();
        userEntity.id = objectId;
        userEntity.name = "Test User";
    }

    @Test
    public void testCreateUser() {
        when(userEntityMapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(user);

        User createdUser = userService.createUser(new User());

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        verify(repository, times(1)).persist(userEntity);
    }

    @Test
    public void testListAllUsers() {
        when(repository.listAll()).thenReturn(Collections.singletonList(userEntity));
        when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(user);

        List<User> users = userService.listAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        verify(repository, times(1)).listAll();
    }

    @Test
    public void testGetUserById_Success() {
        when(repository.findById(objectId)).thenReturn(userEntity);
        when(userEntityMapper.toModel(userEntity)).thenReturn(user);

        User foundUser = userService.getUserById(objectId.toString());

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        verify(repository, times(1)).findById(objectId);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(repository.findById(any(ObjectId.class))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(new ObjectId().toString());
        });
        verify(repository, times(1)).findById(any(ObjectId.class));
    }

    @Test
    public void testUpdateUser_Success() {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");

        when(repository.findById(objectId)).thenReturn(userEntity);
        when(userEntityMapper.toModel(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            User model = new User();
            model.setId(entity.id.toString());
            model.setName(entity.name);
            return model;
        });

        User result = userService.updateUser(objectId.toString(), updatedUser);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(repository, times(1)).findById(objectId);
        verify(repository, times(1)).update(any(UserEntity.class));
    }

    @Test
    public void testUpdateUser_NotFound() {
        when(repository.findById(any(ObjectId.class))).thenReturn(null);
        User userToUpdate = new User();

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(new ObjectId().toString(), userToUpdate);
        });

        verify(repository, times(1)).findById(any(ObjectId.class));
        verify(repository, never()).update(any(UserEntity.class));
    }

    @Test
    public void testDeleteUser_Success() {
        when(repository.findById(objectId)).thenReturn(userEntity);
        when(repository.deleteById(objectId)).thenReturn(true);

        userService.deleteUser(objectId.toString());

        verify(repository, times(1)).findById(objectId);
        verify(repository, times(1)).deleteById(objectId);
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(repository.findById(any(ObjectId.class))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(new ObjectId().toString());
        });

        verify(repository, times(1)).findById(any(ObjectId.class));
        verify(repository, never()).deleteById(any(ObjectId.class));
    }
}
