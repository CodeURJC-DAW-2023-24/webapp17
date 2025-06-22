package es.codeurjc.webapp17.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.webapp17.entity.Usr;

public interface UsrRepository extends JpaRepository<Usr, Long> {

    List<Usr> findAll();

    Usr findById(long id);

    Usr findByUsername(String username);

    List<Usr> findByRole(Usr.Role role);

    Usr save(Usr usr);

    void delete(Usr usr);

    void deleteById(long id);

    Usr findByEmail(String email);

    @Override
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usr WHERE id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comment WHERE usr_id = :id", nativeQuery = true)
    void deleteCommentsByUsrId(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post WHERE usr_id = :id", nativeQuery = true)
    void deletePostsByUsrId(@Param("id") Long id);
}

