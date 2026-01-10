package org.acme.mapper;

import org.acme.model.Address;
import org.acme.model.User;
import org.acme.repository.entity.AddressEntity;
import org.acme.repository.entity.UserEntity;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityMapperTest {

    private UserEntityMapper userEntityMapper;

    @BeforeEach
    public void setUp() {
        userEntityMapper = new UserEntityMapper();
    }

    @Test
    public void testToModel() {
        ObjectId objectId = new ObjectId();
        UserEntity entity = new UserEntity();
        entity.id = objectId;
        entity.name = "John";
        entity.surname = "Doe";
        entity.email = "john.doe@example.com";
        entity.nickname = "johndoe";

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCode("12345-678");
        addressEntity.setStreet("Main Street");
        addressEntity.setNumber("123");
        addressEntity.setComplement("Apt 4B");
        entity.addresses = Collections.singletonList(addressEntity);

        User model = userEntityMapper.toModel(entity);

        assertEquals(objectId.toString(), model.getId());
        assertEquals("John", model.getName());
        assertEquals("Doe", model.getSurname());
        assertEquals("john.doe@example.com", model.getEmail());
        assertEquals("johndoe", model.getNickname());
        assertNotNull(model.getAddresses());
        assertEquals(1, model.getAddresses().size());
        Address addressModel = model.getAddresses().get(0);
        assertEquals("Main Street", addressModel.getStreet());
        assertEquals("12345-678", addressModel.getCode());
    }

    @Test
    public void testToModel_withEntityNull() {
        User model = userEntityMapper.toModel(null);
        assertNull(model);
    }
    
    @Test
    public void testToEntity() {
        User model = new User();
        model.setName("Jane");
        model.setSurname("Doe");
        model.setEmail("jane.doe@example.com");
        model.setNickname("janedoe");
        model.setPassword("password");

        Address addressModel = new Address();
        addressModel.setCode("87654-321");
        addressModel.setStreet("Another Street");
        addressModel.setNumber("456");
        model.setAddresses(Collections.singletonList(addressModel));

        UserEntity entity = userEntityMapper.toEntity(model);

        assertNull(entity.id); // ID is not mapped from model to entity
        assertEquals("Jane", entity.name);
        assertEquals("Doe", entity.surname);
        assertEquals("jane.doe@example.com", entity.email);
        assertEquals("janedoe", entity.nickname);
        assertEquals("password", entity.password);
        assertNotNull(entity.addresses);
        assertEquals(1, entity.addresses.size());
        AddressEntity addressEntity = entity.addresses.get(0);
        assertEquals("Another Street", addressEntity.getStreet());
        assertEquals("87654-321", addressEntity.getCode());
    }

    @Test
    public void testToEntity_withId() {
        ObjectId objectId = new ObjectId();
        User model = new User();
        model.setId(objectId.toString());
        model.setName("Jane");

        UserEntity entity = userEntityMapper.toEntity(model);

        assertEquals(objectId, entity.id);
        assertEquals("Jane", entity.name);
    }

    @Test
    public void testToEntity_withModelNull() {
        UserEntity entity = userEntityMapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    public void testToEntity_withAddressesNull() {
        User model = new User();
        model.setName("Test");
        model.setAddresses(null);

        UserEntity entity = userEntityMapper.toEntity(model);

        assertNotNull(entity);
        assertEquals("Test", entity.name);
        assertNotNull(entity.addresses);
        assertTrue(entity.addresses.isEmpty());
    }
}
