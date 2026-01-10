package org.acme.web.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.web.dto.AddressDTO;
import org.acme.web.dto.UserRequestDTO;
import org.acme.web.dto.UserResponseDTO;
import org.acme.web.dto.UserDetailResponseDTO;
import org.acme.model.Address;
import org.acme.model.User;

import java.util.Collections;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserDtoMapper {

    public User toModel(String id, UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        User model = new User();
        model.setId(id);
        model.setName(dto.getName());
        model.setSurname(dto.getSurname());
        model.setEmail(dto.getEmail());
        model.setNickname(dto.getNickname());
        model.setPassword(dto.getPassword());
        if (dto.getAddresses() != null) {
            model.setAddresses(dto.getAddresses().stream()
                .map(this::addressDtoToModel)
                .collect(Collectors.toList()));
        }
        return model;
    }

    public UserResponseDTO toResponseDTO(User model) {
        if (model == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(model.getId().toString());
        dto.setName(model.getName());
        dto.setSurname(model.getSurname());
        dto.setEmail(model.getEmail());
        dto.setNickname(model.getNickname());
        return dto;
    }

    public UserDetailResponseDTO toDetailResponseDTO(User model) {
        if (model == null) {
            return null;
        }
        UserDetailResponseDTO dto = new UserDetailResponseDTO();
        dto.setId(model.getId().toString());
        dto.setName(model.getName());
        dto.setSurname(model.getSurname());
        dto.setEmail(model.getEmail());
        dto.setNickname(model.getNickname());
        if (model.getAddresses() != null) {
            dto.setAddresses(model.getAddresses().stream()
                .map(this::addressModelToDto)
                .collect(Collectors.toList()));
        } else {
            dto.setAddresses(Collections.emptyList());
        }
        return dto;
    }

    private Address addressDtoToModel(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        Address model = new Address();
        model.setCode(dto.getCode());
        model.setStreet(dto.getStreet());
        model.setNumber(dto.getNumber());
        model.setComplement(dto.getComplement());
        return model;
    }

    private AddressDTO addressModelToDto(Address model) {
        if (model == null) {
            return null;
        }
        AddressDTO dto = new AddressDTO();
        dto.setCode(model.getCode());
        dto.setStreet(model.getStreet());
        dto.setNumber(model.getNumber());
        dto.setComplement(model.getComplement());
        return dto;
    }
}
