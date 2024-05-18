package es.codeurjc.webapp17.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @OneToMany(mappedBy="post")
    private ArrayList<Comment> comments;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private byte[] image;

    @Column(columnDefinition = "TEXT", length = 300)
    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Formato de fecha
    private LocalDateTime date;


    private String tags;


}
