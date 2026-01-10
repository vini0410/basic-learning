package org.acme.web.mapper;

import org.acme.model.Address;
import org.acme.model.User;
import org.acme.web.dto.AddressDTO;
import org.acme.web.dto.UserDetailResponseDTO;
import org.acme.web.dto.UserRequestDTO;
import org.acme.web.dto.UserResponseDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoMapperTest {

    private UserDtoMapper userDtoMapper;

    @BeforeEach
    public void setUp() {
        userDtoMapper = new UserDtoMapper();
    }

    @Test
    public void testToModel() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setNickname("johndoe");
        dto.setPassword("password");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCode("12345-678");
        addressDTO.setStreet("Main Street");
        addressDTO.setNumber("123");
        addressDTO.setComplement("Apt 4B");
        dto.setAddresses(Collections.singletonList(addressDTO));

        String userId = new ObjectId().toString();
        User user = userDtoMapper.toModel(userId, dto);

        assertEquals(userId, user.getId());
        assertEquals("John", user.getName());
        assertEquals("Doe", user.getSurname());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("johndoe", user.getNickname());
        assertEquals("password", user.getPassword());
        assertNotNull(user.getAddresses());
        assertEquals(1, user.getAddresses().size());
        assertEquals("Main Street", user.getAddresses().get(0).getStreet());
    }
    
    @Test
    public void testToModel_withDtoNull() {
        User model = userDtoMapper.toModel(new ObjectId().toString(), null);

        assertNull(model);
    }
    
    @Test
    public void testToResponseDTO() {
        User user = new User();
        String userId = new ObjectId().toString();
        user.setId(userId);
        user.setName("Jane");
        user.setSurname("Doe");
        user.setEmail("jane.doe@example.com");
        user.setNickname("janedoe");

        UserResponseDTO dto = userDtoMapper.toResponseDTO(user);

        assertEquals(userId, dto.getId());
        assertEquals("Jane", dto.getName());
        assertEquals("Doe", dto.getSurname());
        assertEquals("jane.doe@example.com", dto.getEmail());
        assertEquals("janedoe", dto.getNickname());
    }

    @Test
    public void testToResponseDTO_withUserNull() {
        UserResponseDTO dto = userDtoMapper.toResponseDTO(null);

        assertNull(dto);
    }

    @Test
    public void testToDetailResponseDTO() {
        User user = new User();
        String userId = new ObjectId().toString();
        user.setId(userId);
        user.setName("Peter");
        user.setSurname("Jones");
        user.setEmail("peter.jones@example.com");
        user.setNickname("peterj");

        Address address = new Address();
        address.setCode("87654-321");
        address.setStreet("Another Street");
        address.setNumber("456");
        user.setAddresses(Collections.singletonList(address));

        UserDetailResponseDTO dto = userDtoMapper.toDetailResponseDTO(user);

        assertEquals(userId, dto.getId());
        assertEquals("Peter", dto.getName());
        assertEquals("Jones", dto.getSurname());
        assertEquals("peter.jones@example.com", dto.getEmail());
        assertEquals("peterj", dto.getNickname());
        assertNotNull(dto.getAddresses());
        assertEquals(1, dto.getAddresses().size());
        assertEquals("Another Street", dto.getAddresses().get(0).getStreet());
        assertEquals("87654-321", dto.getAddresses().get(0).getCode());
    }

    @Test
    public void testToDetailResponseDTO_withUserNull() {
        UserDetailResponseDTO dto = userDtoMapper.toDetailResponseDTO(null);

        assertNull(dto);
    }

    @Test
    public void testToDetailResponseDTO_withAddressNull() {
        User user = new User();
        String userId = new ObjectId().toString();
        user.setId(userId);
        user.setName("Mary");
        user.setSurname("Smith");
        user.setAddresses(null);

        UserDetailResponseDTO dto = userDtoMapper.toDetailResponseDTO(user);

        assertEquals(userId, dto.getId());
        assertEquals("Mary", dto.getName());
        assertNotNull(dto.getAddresses());
        assertTrue(dto.getAddresses().isEmpty());
    }
}