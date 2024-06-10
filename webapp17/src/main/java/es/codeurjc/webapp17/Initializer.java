package es.codeurjc.webapp17;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.CommentRepository;
import es.codeurjc.webapp17.repository.IssueRepository;
import es.codeurjc.webapp17.repository.PostRepository;
import es.codeurjc.webapp17.repository.UsrRepository;
import jakarta.annotation.PostConstruct;

@Component
public class Initializer {
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UsrRepository usrRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @PostConstruct
    public void init() {
        /*
        Usr usr = new Usr();
        usr.setRole(Usr.Role.ADMIN);
        usr.setUsername("usr1");
        usr.setPassword("admin");
        usr.setEmail("admin@admin");
        usrRepository.save(usr);
        
        Usr usr2 = new Usr();
        usr2.setRole(Usr.Role.USER);
        usr2.setUsername("user");
        usr2.setPassword("user");
        usr2.setEmail("user@user");
        usrRepository.save(usr2);

        
        Post post = new Post();
        post.setUsr(usr);
        post.setTitle("title");
        post.setContent("content");
        post.setDate(LocalDateTime.now());
        postRepository.save(post);

        Post post2 = new Post();
        post2.setUsr(usr2);
        post2.setTitle("title2");
        post2.setContent("content2");
        post2.setDate(LocalDateTime.now());
        postRepository.save(post2);

        Post post3 = new Post();
        post3.setUsr(usr2);
        post3.setTitle("title3");
        post3.setContent("content3");
        post3.setDate(LocalDateTime.now());
        postRepository.save(post3);
        */
    }
}
