package es.codeurjc.webapp17.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    Post findById(long id);

    void deleteById(long id);

    Post save(Post post);

    void delete(Post post);

    Page<Post> findByUsr(Usr usr, Pageable pageable);

    List<Post> findByDateOrderByDate(Date date);

    Page<Post> findByTag(String tag, PageRequest pageable);
}
