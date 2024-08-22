package info.logbat_meta.domain.project.domain;

import info.logbat_meta.domain.project.domain.enums.AppType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Table(name = "apps", indexes = {
    @Index(name = "idx_app_token", columnList = "appKey")
})
@SoftDelete
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type", nullable = false)
    private AppType appType;

    @Column(name = "app_key", nullable = false, unique = true)
    private UUID appKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private App(Project project, String name, AppType appType) {
        this.project = Objects.requireNonNull(project, "프로젝트는 필수입니다.");
        this.name = Objects.requireNonNull(name, "앱 이름은 필수입니다.");
        this.appType = Objects.requireNonNull(appType, "앱 타입은 필수입니다.");
        this.appKey = UUID.randomUUID();
    }

    public static App of(Project project, String name, AppType appType) {
        return new App(project, name, appType);
    }

}
