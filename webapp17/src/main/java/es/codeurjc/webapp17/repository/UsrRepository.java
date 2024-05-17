package es.codeurjc.webapp17.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Usr;

public interface UsrRepository extends JpaRepository<Usr, Long>{
    
}
