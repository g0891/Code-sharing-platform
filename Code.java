package platform;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@JsonSerialize(using = CodeSerializer.class)
public class Code {


    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid = UUID.randomUUID().toString();
    private String code;
    private LocalDateTime date = LocalDateTime.now();
    private long time = 0;
    private int views = 0;
    private int viewsDone = 0;
    @Transient
    private long timeLeft = 0;


    public Code(){}

    public Code(String code){
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String id) {
        this.uuid = id;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft() {
        this.timeLeft = time - LocalDateTime.from(date).until(LocalDateTime.now(), ChronoUnit.SECONDS);
        if (this.timeLeft < 0) {
            this.timeLeft = 0;
        }
    }

    public int getViewsDone() {
        return viewsDone;
    }

    public void setViewsDone(int viewsDone) {
        this.viewsDone = viewsDone;
    }
}

class CodeSerializer extends JsonSerializer<Code> {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void serialize(Code code, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("code", code.getCode());
            jsonGenerator.writeStringField("date", code.getDate().toString());
            jsonGenerator.writeNumberField("time", code.getTimeLeft());
            jsonGenerator.writeNumberField("views", code.getViews() - code.getViewsDone());
        jsonGenerator.writeEndObject();
    }
}