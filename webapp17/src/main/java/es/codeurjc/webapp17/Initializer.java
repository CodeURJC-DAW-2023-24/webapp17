package es.codeurjc.webapp17;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.CommentRepository;
import es.codeurjc.webapp17.repository.ForumRepository;
import es.codeurjc.webapp17.repository.PostRepository;
import es.codeurjc.webapp17.repository.UsrRepository;
import jakarta.annotation.PostConstruct;

@Component
public class Initializer {
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private UsrRepository usrRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @PostConstruct
    public void init() {
        
        Usr usr = new Usr();
        usr.setRole(Usr.Role.ADMIN);
        usr.setUsername("usr1");
        usr.setPassword("admin");
        usr.setEmail("admin");
        usrRepository.save(usr);
        
        Usr usr2 = new Usr();
        usr2.setRole(Usr.Role.USER);
        usr2.setUsername("user");
        usr2.setPassword("user");
        usr2.setEmail("user");
        usrRepository.save(usr2);

    }
}
