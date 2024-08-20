package info.logbat_meta.domain.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Entity
@Getter
@SoftDelete
@Table(name = "projects", indexes = {
    @Index(name = "idx_project_name", columnList = "name")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Project(String name) {
        this.name = name;
    }

    public static Project from(String name) {
        validateName(name);
        return new Project(name);
    }

    public void updateName(String name) {
        validateName(name);
        this.name = name;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank() || name.getBytes(StandardCharsets.UTF_8).length > 100) {
            throw new IllegalArgumentException("잘못된 이름 요청입니다.");
        }
    }

}
