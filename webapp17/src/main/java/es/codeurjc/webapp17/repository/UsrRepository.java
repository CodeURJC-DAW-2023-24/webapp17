package es.codeurjc.webapp17.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.webapp17.entity.Usr;

public interface UsrRepository extends JpaRepository<Usr, Long>{
    
    List<Usr> findAll();

    Usr findById(long id);

    Usr findByUsername(String username);

    List<Usr> findByRole(Usr.Role role);

    Usr save(Usr usr);

    void delete(Usr usr);

    void deleteById(long id);

    Usr findByEmail(String email);

   

}
