package es.codeurjc.webapp17.service;

import java.util.ArrayList;
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



    private List<Comment> comments = new ArrayList<>();

    public List<Comment> getAllComment() {
        return commentRepository.findAll();
    }

    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.delete(commentRepository.findById(id).get());
    }
}