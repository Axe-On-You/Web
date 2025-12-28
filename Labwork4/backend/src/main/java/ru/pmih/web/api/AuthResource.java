package ru.pmih.web.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.pmih.web.dto.TokenDTO;
import ru.pmih.web.dto.UserDTO;
import ru.pmih.web.entity.UserEntity;
import ru.pmih.web.service.UserService;
import ru.pmih.web.utils.JwtUtils;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private UserService userService;

    @Inject
    private JwtUtils jwtUtils;

    @POST
    @Path("/register")
    public Response register(UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();
        }
        UserEntity user = userService.register(userDTO.getUsername(), userDTO.getPassword());
        if (user == null) {
            return Response.status(Response.Status.CONFLICT).entity("User already exists").build();
        }
        return Response.ok("User registered successfully").build();
    }

    @POST
    @Path("/login")
    public Response login(UserDTO userDTO) {
        UserEntity user = userService.authenticate(userDTO.getUsername(), userDTO.getPassword());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = jwtUtils.generateToken(user.getId());
        return Response.ok(new TokenDTO(token)).build();
    }
}