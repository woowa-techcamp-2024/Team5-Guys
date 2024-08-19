package info.logbat.domain.project.domain;

import info.logbat.domain.project.domain.enums.AppType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type", nullable = false)
    private AppType appType;

    @Column(name = "app_key", nullable = false, unique = true)
    private UUID appKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private App(Project project, AppType appType) {
        this.project = Objects.requireNonNull(project, "프로젝트는 필수입니다.");
        this.appType = Objects.requireNonNull(appType, "앱 타입은 필수입니다.");
        this.appKey = UUID.randomUUID();
    }

    public static App of(Project project, AppType appType) {
        return new App(project, appType);
    }

}
