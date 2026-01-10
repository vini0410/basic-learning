package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Address;
import org.acme.model.User;
import org.acme.repository.entity.AddressEntity;
import org.acme.repository.entity.UserEntity;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserEntityMapper {

    public User toModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User model = new User();
        model.setId(entity.id.toString());
        model.setName(entity.name);
        model.setSurname(entity.surname);
        model.setEmail(entity.email);
        model.setNickname(entity.nickname);
        model.setPassword(entity.password);
        if (entity.addresses != null) {
            model.setAddresses(entity.addresses.stream()
                    .map(this::addressEntityToModel)
                    .collect(Collectors.toList()));
        }
        return model;
    }

    public UserEntity toEntity(User model) {
        if (model == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        if(model.getId() != null) {
            entity.id = new ObjectId(model.getId());
        }
        entity.name = model.getName();
        entity.surname = model.getSurname();
        entity.email = model.getEmail();
        entity.nickname = model.getNickname();
        entity.password = model.getPassword();
        if (model.getAddresses() != null) {
            entity.addresses = model.getAddresses().stream()
                    .map(this::addressModelToEntity)
                    .collect(Collectors.toList());
        } else {
            entity.addresses = Collections.emptyList();
        }
        return entity;
    }

    private Address addressEntityToModel(AddressEntity entity) {
        if (entity == null) {
            return null;
        }
        Address model = new Address();
        model.setCode(entity.getCode());
        model.setStreet(entity.getStreet());
        model.setNumber(entity.getNumber());
        model.setComplement(entity.getComplement());
        return model;
    }

    public AddressEntity addressModelToEntity(Address model) {
        if (model == null) {
            return null;
        }
        AddressEntity entity = new AddressEntity();
        entity.setCode(model.getCode());
        entity.setStreet(model.getStreet());
        entity.setNumber(model.getNumber());
        entity.setComplement(model.getComplement());
        return entity;
    }
}
