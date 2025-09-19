package es.codeurjc.webapp17.dto;

public record CreateUsrDto(
    String username,
    String email,
    String password,
    Boolean admin
) {}
