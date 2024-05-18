package es.codeurjc.webapp17.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Issue;


public interface IssueRepository extends JpaRepository<Issue, Long>{

    List<Issue> findAll();

    Issue findById(long id);

    void deleteById(long id);

  
    Issue save(Issue Issue);

    void delete(Issue Issue);


   



}
