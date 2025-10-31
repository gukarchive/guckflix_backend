package guckflix.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
public class MemberDto {


    @Getter
    @Setter
    @ToString
    @ApiModel(value = "MemberDto-Post")
    public static class Post{

        @Pattern (regexp = "^(?=.*[A-Za-z])[A-Za-z0-9]{4,20}$", message = "아이디 : 영문을 포함한 4 ~ 20자")
        private String username;

        @Pattern (regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[a-z]).{8,20}$", message = "비밀번호 : 영문 소문자, 대문자, 숫자가 적어도 1개씩 포함된 8 ~ 20자")
        private String password;

        @Email
        @Length(max = 30)
        private String email;

    }

    @Getter
    @Setter
    @ToString
    @ApiModel(value = "MemberDto-LoginForm")
    public static class LoginForm {

        @NotBlank
        @Length(min = 4, max = 40)
        private String username;

        @NotBlank
        @Length(min = 8, max = 30)
        private String password;

    }

    @Getter
    @Setter
    @ToString
    @ApiModel(value = "MemberDto-PasswordChangeForm")
    public static class passwordChangeForm {

        @NotBlank
        @Length(min = 8, max = 20)
        private String password;

    }

    @Getter
    @Setter
    @ApiModel(value = "MemberDto-User")
    public static class User{
        private Long id;
        private String role;

        public User(Long id, String role) {
            this.id = id;
            this.role = role;
        }
    }

}
