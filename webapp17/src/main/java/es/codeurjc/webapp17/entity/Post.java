package es.codeurjc.webapp17.entity;

import java.time.LocalDateTime;


import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    private Usr usr;

    @JsonManagedReference
    @OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(length = 200) 
    private String image;

    @Column(columnDefinition = "TEXT", length = 300)
    private String content;

    @DateTimeFormat(pattern = "dd 'de' MMMM 'de' yyyy HH:mm") 
    private LocalDateTime date;


    private String tag;

    public List<Comment> getComments() {
        return comments;
    }

}
