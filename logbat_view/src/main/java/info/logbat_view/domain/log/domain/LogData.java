package info.logbat_view.domain.log.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("logs")
@AllArgsConstructor
public class LogData {

    @Id
    private Long logId;
    private String appKey;
    private String level;
    private String data;
    private LocalDateTime timestamp;
}
