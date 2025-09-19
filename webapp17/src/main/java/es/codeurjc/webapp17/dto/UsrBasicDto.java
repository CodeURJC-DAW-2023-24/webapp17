package es.codeurjc.webapp17.dto;

import es.codeurjc.webapp17.entity.Usr;

public record UsrBasicDto(
    Long id,
    String username,
    Usr.Role role
) {}
