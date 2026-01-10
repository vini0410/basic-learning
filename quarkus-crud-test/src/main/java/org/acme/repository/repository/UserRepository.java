package org.acme.repository.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.repository.entity.UserEntity;
import org.acme.web.dto.UserResponseDTO;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<UserEntity> {

//    public UserEntity findByName(String name) {
//        return find("name", name).firstResult();
//    }
}
