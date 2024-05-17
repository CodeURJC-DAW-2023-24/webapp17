package es.codeurjc.webapp17.entity;

import java.util.ArrayList;

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

    @JsonBackReference
    @ManyToOne
    private Issues forum;

    @JsonManagedReference
    @OneToMany(mappedBy="post")
    private ArrayList<Comment> comments;

    private String title;

    private String link;

    private String image;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String date;


    private String tags;



    // hashCode, equals y toStri


    @Override
    public String toString() {
        return "Entry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", tags='" + tags + '\'' +
                ", comments=" + comments +
                '}';
    }
}
