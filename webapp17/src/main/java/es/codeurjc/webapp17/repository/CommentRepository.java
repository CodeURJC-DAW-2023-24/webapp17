package es.codeurjc.webapp17.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAll();

    Comment findById(long id);

    @Override
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comment WHERE id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);

    Comment save(Comment comment);

    void delete(Comment comment);

    List<Comment> findByPost(Post post);

    List<Comment> findByUsr(Usr usr);

    List<Comment> findByDateOrderByDate(Date date);

}
