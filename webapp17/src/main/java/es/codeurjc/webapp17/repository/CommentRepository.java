package es.codeurjc.webapp17.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{

}
