package root.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class Tags {

    /****** ПОЛЯ ******/
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @NotNull
    private String title;

    @OneToMany(mappedBy = "tag")
    Set<Tag2Post> tag2Post;

    /****** ГЕТТЕРЫ ******/
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<Tag2Post> getTag2Post() {
        return tag2Post;
    }

    /****** СЕТТЕРЫ ******/
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTag2Post(Set<Tag2Post> tag2Post) {
        this.tag2Post = tag2Post;
    }
}
