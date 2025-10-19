package co.com.challenge.model.utils.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@ToString
public class ResponseUtil {

    private int code;
    private String message;
    private Object data;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT-5")
    private String timestamp;

    public ResponseUtil(int code400, String successfulProcess, Object[] objects, String ok) {
        super();
        initTimestamp();
    }

    public ResponseUtil(int code, String message, Object data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
        initTimestamp();
    }

    public ResponseUtil(int code, String message) {
        super();
        this.code = code;
        this.message = message;
        initTimestamp();
    }

    private void initTimestamp() {
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}