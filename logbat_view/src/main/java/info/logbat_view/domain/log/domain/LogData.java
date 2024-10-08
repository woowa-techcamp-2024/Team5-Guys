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
    private final Long logId;
    private final byte[] appKey;
    private final Integer level;
    private final String data;
    private final LocalDateTime timestamp;
}
