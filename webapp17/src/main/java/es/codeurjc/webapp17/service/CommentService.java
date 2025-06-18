package es.codeurjc.webapp17.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.repository.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Retrieves a list of all comments from the database.
     *
     * @return a list of Comment objects
     */
    public List<Comment> getAllComment() {
        return commentRepository.findAll();
    }

    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        try {
            commentRepository.deleteById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

    public Comment getCommentById(Long id) {
        
        return commentRepository.findById(id).orElse(null);
    }
}