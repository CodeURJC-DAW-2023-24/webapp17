package es.codeurjc.webapp17.dto;

import java.time.LocalDateTime;

public record CommentDto(
    Long id,
    Long userId,
    String username,
    Long postId,
    LocalDateTime date,
    String text
) {}
