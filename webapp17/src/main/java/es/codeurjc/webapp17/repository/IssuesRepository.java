package es.codeurjc.webapp17.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Issues;
import es.codeurjc.webapp17.entity.Usr;

public interface IssuesRepository extends JpaRepository<Issues, Long>{

    List<Issues> findAll();

    Issues findById(long id);

    Issues findByUsr(Usr usr);

    void deleteById(long id);

    void save(Issues issues);

    void delete(Issues issues);

    void deleteByUsr(Usr usr);



}
