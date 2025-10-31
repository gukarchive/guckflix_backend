package guckflix.backend.entity;

import guckflix.backend.entity.base.TimeDateBaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Table(name = "admin_memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdminMemo extends TimeDateBaseEntity {

    @Id @GeneratedValue
    private Long id;

    private String text;

}
