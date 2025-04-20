package otp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Code {
    private int id;
    private int userId;
    private String code;
    private CodeStatus status;
    private LocalDateTime createdAt;
    private String operationId;
}

