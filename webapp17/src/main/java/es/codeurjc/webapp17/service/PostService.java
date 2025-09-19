package es.codeurjc.webapp17.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.webapp17.dto.PostDto;
import es.codeurjc.webapp17.dto.CommentDto;
import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.entity.Post;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.PostRepository;
import es.codeurjc.webapp17.repository.UsrRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UsrRepository usrRepository;

    /**
     * Retrieves posts with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return a Page of PostDto objects
     */
    public Page<PostDto> getPosts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByOrderByIdDesc(pageable);
        return convertToPageDto(posts);
    }

    /**
     * Retrieves posts by user with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @param userId the ID of the user
     * @return a Page of PostDto objects
     */
    public Page<PostDto> getPostsByUsr(int page, int size, Long userId) {
        Usr user = usrRepository.findById(userId).orElse(null);
        if (user == null) {
            return Page.empty();
        }
        
        PageRequest pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByUsr(user, pageable);
        return convertToPageDto(posts);
    }

    /**
     * Retrieves all posts by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of PostDto objects
     */
    public List<PostDto> getPostsByUser(Long userId) {
        Usr user = usrRepository.findById(userId).orElse(null);
        if (user == null) {
            return List.of();
        }
        
        List<Post> posts = postRepository.findByUsr(user, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all posts.
     *
     * @return a list of PostDto objects
     */
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new post.
     *
     * @param postDto the post DTO to be saved
     * @return the saved PostDto
     */
    public PostDto addPost(PostDto postDto) {
        Post post = convertToEntity(postDto);
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    /**
     * Deletes a post and its associated comments.
     *
     * @param id the ID of the post to delete
     * @return true if deletion was successful, false otherwise
     */
    @Transactional
    public boolean deletePost(Long id) {
        try {
            if (postRepository.existsById(id)) {
                postRepository.deleteCommentsByPostId(id);
                postRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param id the ID of the post to retrieve
     * @return PostDto if found, null otherwise
     */
    public PostDto getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    /**
     * Updates an existing post.
     *
     * @param postDto the post DTO with updated information
     * @return the updated PostDto
     */
    public PostDto updatePost(PostDto postDto) {
        Post post = convertToEntity(postDto);
        Post updatedPost = postRepository.save(post);
        return convertToDto(updatedPost);
    }

    /**
     * Retrieves posts by tag with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @param tag the tag to filter by
     * @return a Page of PostDto objects
     */
    public Page<PostDto> getPostsByTag(int page, int size, String tag) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByTag(tag, pageable);
        return convertToPageDto(posts);
    }

    /**
     * Retrieves basic user information by post ID.
     *
     * @param id the ID of the post
     * @return UsrBasicDto if post found, null otherwise
     */
    public UsrBasicDto getUsrByPostId(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null && post.getUsr() != null) {
            Usr usr = post.getUsr();
            return new UsrBasicDto(usr.getId(), usr.getUsername(), usr.getRole());
        }
        return null;
    }

    // Private mapping methods
    
    /**
     * Converts a Post entity to PostDto.
     *
     * @param post the Post entity to convert
     * @return the converted PostDto
     */
    private PostDto convertToDto(Post post) {
        if (post == null) return null;
        
        // Convert comments to CommentDto list
        List<CommentDto> commentDtos = post.getComments() != null ? 
            post.getComments().stream()
                .map(comment -> new CommentDto(
                    comment.getId(),
                    comment.getUsr() != null ? comment.getUsr().getId() : null,
                    comment.getUsr() != null ? comment.getUsr().getUsername() : null,
                    comment.getPost() != null ? comment.getPost().getId() : null,
                    comment.getDate(),
                    comment.getText()
                ))
                .collect(Collectors.toList()) : List.of();
        
        return new PostDto(
            post.getId(),
            post.getUsr() != null ? post.getUsr().getId() : null,
            post.getUsr() != null ? post.getUsr().getUsername() : null,
            post.getTitle(),
            post.getImage(),
            post.getContent(),
            post.getDate(),
            post.getTag(),
            commentDtos
        );
    }

    /**
     * Converts a PostDto to Post entity.
     *
     * @param postDto the PostDto to convert
     * @return the converted Post entity
     */
    private Post convertToEntity(PostDto postDto) {
        if (postDto == null) return null;
        
        Post post = new Post();
        post.setId(postDto.id());
        post.setTitle(postDto.title());
        post.setImage(postDto.image());
        post.setContent(postDto.content());
        post.setDate(postDto.date());
        post.setTag(postDto.tag());
        
        // Set user relationship
        if (postDto.userId() != null) {
            Usr usr = usrRepository.findById(postDto.userId()).orElse(null);
            post.setUsr(usr);
        }
        
        return post;
    }

    /**
     * Converts a Page of Post entities to a Page of PostDto.
     *
     * @param postPage the Page of Post entities
     * @return the converted Page of PostDto
     */
    private Page<PostDto> convertToPageDto(Page<Post> postPage) {
        List<PostDto> postDtos = postPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(postDtos, postPage.getPageable(), postPage.getTotalElements());
    }
}
