package es.codeurjc.webapp17.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.UsrRepository;

@Service
public class UsrService {
    @Autowired
    private UsrRepository userRepository;

    @Autowired
    private PostService postService;




    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean authenticate(String email, String password) {
        Usr user = userRepository.findByEmail(email);
        if (user != null) {
            System.out.println("Usuario encontrado: " + user.getEmail());
            System.out.println("Contraseña codificada: " + user.getPassword());
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("Las contraseñas coinciden: " + matches);
            return matches;
        }
        System.out.println("Usuario no encontrado");
        return false;
    }

    public Usr getUsr(String email) {
        Usr user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        System.out.println("Usuario no encontrado");
        return null;
    }

    @Transactional
    public void deleteUsr(Long userId) {
        try {
            userRepository.deleteCommentsByUsrId(userId);
            List<Post> posts = postService.getPostsByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
            for (Post post : posts) {
                postService.deletePost(post.getId());
            }
            userRepository.deleteById(userId);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex; 
        }
    }

    public ArrayList<Usr> getAllUsrs() {
        return new ArrayList<Usr>(userRepository.findAll());

    }

    public void createUsr(String name, String email, String password, Boolean admin) {
        String encodedPassword = passwordEncoder.encode(password);

        Usr usuario = new Usr(name, email, encodedPassword, admin);
        userRepository.save(usuario);
    }

    public void createUsr(Usr newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
    }

    public Usr findUsrById(Long id) {
        return userRepository.findById(id).get();
    }

}
