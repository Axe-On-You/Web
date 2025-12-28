package ru.pmih.web.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.pmih.web.dto.PointDTO;
import ru.pmih.web.service.PointService;
import ru.pmih.web.utils.JwtUtils;

import java.util.List;

@Path("/points")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointResource {

    @Inject
    private PointService pointService;

    @Inject
    private JwtUtils jwtUtils;

    @GET
    public Response getPoints(@HeaderParam("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        if (userId == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        List<PointDTO> points = pointService.getPoints(userId);
        return Response.ok(points).build();
    }

    @POST
    public Response addPoint(@HeaderParam("Authorization") String authHeader, PointDTO pointDTO) {
        Long userId = getUserIdFromHeader(authHeader);
        if (userId == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        // Небольшая валидация входных данных
        if (pointDTO.getR() == null || pointDTO.getR() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Radius must be positive").build();
        }

        PointDTO created = pointService.addPoint(pointDTO, userId);
        return Response.ok(created).build();
    }

    private Long getUserIdFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7); // Убираем "Bearer "
        return jwtUtils.verifyToken(token);
    }
}