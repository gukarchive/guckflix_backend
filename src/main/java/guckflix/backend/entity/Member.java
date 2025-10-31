package guckflix.backend.entity;


import guckflix.backend.entity.enums.MemberRole;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    private String username;

    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String provider;

    @Column(name= "provider_id")
    private String providerId;

}
