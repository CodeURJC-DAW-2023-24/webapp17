package es.codeurjc.webapp17.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Post;

public interface PostRepository extends JpaRepository<Post,Long>{

}
