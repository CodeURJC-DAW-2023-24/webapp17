package es.codeurjc.webapp17.dto;

import java.time.LocalDateTime;

public record IssueDto(
    Long id,
    String name,
    String email,
    String content,
    LocalDateTime date
) {}
