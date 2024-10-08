package es.codeurjc.webapp17.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    private PostRepository postRepository;

    private List<Post> posts = new ArrayList<>();

    // Método para obtener todos los posts
    public Page<Post> getPosts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }

    // Método para agregar un nuevo post
    public void addPost(Post post) {
        postRepository.save(post);
    }

    // Método para borrar un post
    public void deletePost(Long id) {
        postRepository.delete(postRepository.findById(id).orElse(null));
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }
}
