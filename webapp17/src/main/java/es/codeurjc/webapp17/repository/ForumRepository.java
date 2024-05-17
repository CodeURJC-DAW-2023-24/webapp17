package es.codeurjc.webapp17.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Issues;

public interface ForumRepository extends JpaRepository<Issues, Long>{

}
