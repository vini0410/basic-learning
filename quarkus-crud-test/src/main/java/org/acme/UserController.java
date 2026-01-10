package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.User;
import org.acme.repository.repository.UserRepository;
import org.acme.service.UserService;
import org.acme.web.dto.UserDetailResponseDTO;
import org.acme.web.dto.UserRequestDTO;
import org.acme.web.dto.UserResponseDTO;
import org.acme.web.mapper.UserDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;

    @Inject
    UserDtoMapper userDtoMapper;

    @POST
    public UserResponseDTO createUser(UserRequestDTO userRequest) {
        User userModel = userDtoMapper.toModel(null, userRequest);
        User createdUser = userService.createUser(userModel);
        return userDtoMapper.toResponseDTO(createdUser);
    }

    @GET
    public List<UserResponseDTO> getUsers() {
        return userService.listAllUsers().stream()
                .map(userDtoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public UserDetailResponseDTO getUserById(@PathParam("id") String id) {
        User user = userService.getUserById(id);
        return userDtoMapper.toDetailResponseDTO(user);
    }

    @GET
    @Path("/{id}/addresses")
    public UserDetailResponseDTO getUserAddresses(@PathParam("id") String id) {
        User user = userService.getUserById(id);
        return userDtoMapper.toDetailResponseDTO(user);
    }

    @PUT
    @Path("/{id}")
    public UserDetailResponseDTO updateUser(@PathParam("id") String id, UserRequestDTO userRequest) {
        User userModel = userDtoMapper.toModel(id, userRequest);
        User updatedUser = userService.updateUser(id, userModel);
        return userDtoMapper.toDetailResponseDTO(updatedUser);
    }

    @DELETE
    @Path("/{id}")
    public String deleteUser(@PathParam("id") String id) {
        userService.deleteUser(id);
        return "usuario " + id + " deletado";
    }
}
