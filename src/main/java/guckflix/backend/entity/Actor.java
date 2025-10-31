package guckflix.backend.entity;

import guckflix.backend.dto.ActorDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class Actor {

    @Column(name = "actor_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    private String biography;

    private LocalDate birthDay;

    private LocalDate deathDay;

    private String placeOfBirth;

    private String name;

    private String profilePath;

    private float popularity;

    @OneToMany(mappedBy = "actor")
    private List<Credit> credits = new ArrayList<>();

    public void updateInfo(ActorDto.UpdateInfo actorUpdafeForm) {
        name = actorUpdafeForm.getName();
        profilePath = actorUpdafeForm.getProfilePath();
        birthDay = actorUpdafeForm.getBirthDay();
        deathDay = actorUpdafeForm.getDeathDay();
        biography = actorUpdafeForm.getBiography();
    }

    public void updatePhoto(String uuid) {
        this.profilePath = uuid;
    }



}

