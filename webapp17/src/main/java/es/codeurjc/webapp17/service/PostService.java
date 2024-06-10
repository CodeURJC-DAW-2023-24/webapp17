package es.codeurjc.webapp17.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Comment;
import es.codeurjc.webapp17.entity.Post;

import es.codeurjc.webapp17.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    private PostRepository PostRepository;



    private List<Post> posts = new ArrayList<>();

    // Método para obtener todos los posts
    public List<Post> getAllPost() {
        return PostRepository.findAll();
    }

    // Método para agregar un nuevo post
    public void addPost(Post post) {
        PostRepository.save(post);
    }

    // Método para borrar un post
    public void deletePost(Long id) {
        PostRepository.delete(PostRepository.findById(id).get());
    }

    public Post getPostById(Long id) {
        return PostRepository.findById(id).get();
    }

}