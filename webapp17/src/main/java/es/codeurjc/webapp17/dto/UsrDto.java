package es.codeurjc.webapp17.dto;

import java.util.List;
import es.codeurjc.webapp17.entity.Usr.Role;

public record UsrDto(
    Long id,
    String username,
    String email,
    Role role,
    List<PostDto> posts,
    List<CommentDto> comments
) {}
