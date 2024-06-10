package es.codeurjc.webapp17.service;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.IssueRepository;
import es.codeurjc.webapp17.repository.PostRepository;
import es.codeurjc.webapp17.repository.UsrRepository;

@Service
public class UsrService {
    @Autowired
    private UsrRepository userRepository;

    public boolean authenticate(String email, String password) {
        Usr user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }


    public void deleteUsr(Long id) {
        userRepository.delete(userRepository.findById(id).get());
    }

    public ArrayList<Usr> getAllUsrs() {
        return new ArrayList<Usr>(userRepository.findAll());
        
    }


    public void createUsr( String name, String email, String password, Boolean admin) {
        Usr usuario =  new Usr(name, email, password,admin);
        userRepository.save(usuario);
    }


    public void createUsr(Usr newUser) {
        userRepository.save(newUser);
    }


    
}
