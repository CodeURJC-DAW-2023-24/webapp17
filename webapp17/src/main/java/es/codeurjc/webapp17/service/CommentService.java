package es.codeurjc.webapp17.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.dto.CommentDto;
import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.CommentRepository;
import es.codeurjc.webapp17.repository.PostRepository;
import es.codeurjc.webapp17.repository.UsrRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UsrRepository usrRepository;
    
    @Autowired
    private PostRepository postRepository;

    /**
     * Retrieves a list of all comments from the database.
     *
     * @return a list of CommentDto objects
     */
    public List<CommentDto> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new comment to the database.
     *
     * @param commentDto the comment DTO to be saved
     * @return the saved CommentDto
     */
    public CommentDto addComment(CommentDto commentDto) {
        Comment comment = convertToEntity(commentDto);
        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id the ID of the comment to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteComment(Long id) {
        try {
            if (commentRepository.existsById(id)) {
                commentRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a comment by its ID.
     *
     * @param id the ID of the comment to retrieve
     * @return CommentDto if found, null otherwise
     */
    public CommentDto getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    // Private mapping methods
    
    /**
     * Converts a Comment entity to CommentDto.
     *
     * @param comment the Comment entity to convert
     * @return the converted CommentDto
     */
    private CommentDto convertToDto(Comment comment) {
        if (comment == null) return null;
        
        return new CommentDto(
            comment.getId(),
            comment.getUsr() != null ? comment.getUsr().getId() : null,
            comment.getUsr() != null ? comment.getUsr().getUsername() : null,
            comment.getPost() != null ? comment.getPost().getId() : null,
            comment.getDate(),
            comment.getText()
        );
    }

    /**
     * Converts a CommentDto to Comment entity.
     *
     * @param commentDto the CommentDto to convert
     * @return the converted Comment entity
     */
    private Comment convertToEntity(CommentDto commentDto) {
        if (commentDto == null) return null;
        
        Comment comment = new Comment();
        comment.setId(commentDto.id());
        comment.setDate(commentDto.date());
        comment.setText(commentDto.text());
        
        // Set user relationship
        if (commentDto.userId() != null) {
            Usr usr = usrRepository.findById(commentDto.userId()).orElse(null);
            comment.setUsr(usr);
        }
        
        // Set post relationship
        if (commentDto.postId() != null) {
            Post post = postRepository.findById(commentDto.postId()).orElse(null);
            comment.setPost(post);
        }
        
        return comment;
    }
}
