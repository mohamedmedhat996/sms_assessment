package bb.ta.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto implements Serializable {
    private Long id;
    private String courseName;
    private String description;
    private int credits;
    private String instructor;
    private boolean registered;
    private Timestamp registeredAt;
    private Timestamp canceledAt;
}
