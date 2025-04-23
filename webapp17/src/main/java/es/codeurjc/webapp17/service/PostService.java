package es.codeurjc.webapp17.service;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    private PostRepository postRepository;


    public Page<Post> getPosts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return postRepository.findAllByOrderByIdDesc(pageable);
    }

    public Page<Post> getPostsbyUsr(int page, int size, Usr user) {
        PageRequest pageable = PageRequest.of(page, size);
        return postRepository.findByUsr(user, pageable);
    }
    public ArrayList<Post> getAllPosts() {
        return new ArrayList<>(postRepository.findAll());
    }
    public void addPost(Post post) {
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.delete(postRepository.findById(id).orElse(null));
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public void updatePost(Post post) {
        postRepository.save(post);
    }
    public Page<Post> getPostsByTag(int page, int size, String tag) {
        PageRequest pageable = PageRequest.of(page, size);
        return postRepository.findByTag(tag, pageable);
    }
}
