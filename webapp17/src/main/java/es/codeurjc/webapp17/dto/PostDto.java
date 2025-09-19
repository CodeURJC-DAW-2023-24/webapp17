package es.codeurjc.webapp17.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDto(
    Long id,
    Long userId,
    String username,
    String title,
    String image,
    String content,
    LocalDateTime date,
    String tag,
    List<CommentDto> comments
) {}
