package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.mapper.UserEntityMapper;
import org.acme.model.User;
import org.acme.repository.entity.UserEntity;
import org.acme.repository.repository.UserRepository; // Agora injetará a implementação do PanacheMongoRepository
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository repository;

    @Inject
    UserEntityMapper userEntityMapper;

    public User createUser(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);
        repository.persist(userEntity);
        return userEntityMapper.toModel(userEntity);
    }

    public List<User> listAllUsers() {
        return repository.listAll().stream()
                .map(userEntityMapper::toModel)
                .collect(Collectors.toList());
    }

    public User getUserById(String id) {
        var objId = new ObjectId(id);
        var entity = repository.findById(objId);
        if (Objects.isNull(entity)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        var user = userEntityMapper.toModel(entity);
        return user;
    }

    public User updateUser(String id, User user) {
        var objId = new ObjectId(id);
        var entity = repository.findById(objId);
        if (Objects.isNull(entity)) {
            throw new NotFoundException("User not found with id: " + id);
        }

        entity.name = user.getName();
        entity.surname = user.getSurname();
        entity.email = user.getEmail();
        entity.nickname = user.getNickname();
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            entity.password = user.getPassword();
        }

        if (user.getAddresses() != null) {
            entity.addresses = user.getAddresses().stream()
                    .map(userEntityMapper::addressModelToEntity)
                    .collect(Collectors.toList());
        }

        repository.update(entity);
        return userEntityMapper.toModel(entity);
    }

    public void deleteUser(String id) {
        var objId = new ObjectId(id);
        var entity = repository.findById(objId);
        if (Objects.isNull(entity)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        repository.deleteById(objId);
    }
}
