package platform;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

@SpringBootApplication
@RestController
public class CodeSharingPlatform {

    private static final Logger log = LoggerFactory.getLogger(CodeSharingPlatform.class);

    private static Configuration cfg;
    private static Template showCode;
    private static Template createCode;

    @Autowired
    CodeRepository codeRepository;

    public static void main(String[] args) {
        cfg = FreeMarkerConfig.create();
        if (cfg == null) {
            log.warn("Freemarker cfg was not created");
        }
        showCode = FreeMarkerConfig.load(cfg, "showCodeTemplate.ftlh");
        if (showCode == null) {
            log.warn("Freemarker template not loaded");
        }

        createCode = FreeMarkerConfig.load(cfg, "createCodeTemplate.html");
        if (createCode == null) {
            log.warn("Freemarker template not loaded");
        }

        SpringApplication.run(CodeSharingPlatform.class, args);
    }

    @GetMapping(path = "/code/{uuid}")
    public String getCode(HttpServletResponse response, @PathVariable String uuid) {
        response.addHeader("Content-Type", "text/html");
        Code code = codeRepository.findByUuid(uuid).orElse(null);
        if (code == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (code.getTime() > 0) {
            code.setTimeLeft();
            if (code.getTimeLeft() == 0) {
                codeRepository.delete(code);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }

        if (code.getViews() >= 1) {
            code.setViewsDone(code.getViewsDone() + 1);
            if (code.getViews() == code.getViewsDone()) codeRepository.delete(code);
            else codeRepository.save(code);
        }

        Map<String, Object> root= new HashMap<>();
        ArrayList<Code> codes = new ArrayList<Code>();
        codes.add(code);
        root.put("codes", codes);
        root.put("title", "Code");
        Writer writer = new StringWriter();
        try {
            showCode.process(root, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Something was wrong";

        }
    }

    @GetMapping(path = "/api/code/{uuid}")
    public Code getCodeViaApi(HttpServletResponse response, @PathVariable String uuid) {
        response.addHeader("Content-Type", "application/json");

        Code code = codeRepository.findByUuid(uuid).orElse(null);
        if (code == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (code.getTime() > 0) {
            code.setTimeLeft();
            if (code.getTimeLeft() == 0) {
                codeRepository.delete(code);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }

        if (code.getViews() >= 1) {
            code.setViewsDone(code.getViewsDone() + 1);
            if (code.getViews() == code.getViewsDone()) codeRepository.delete(code);
            else codeRepository.save(code);
        }

        return code;
    }

    @GetMapping(path = "/api/code/latest")
    public ArrayList<Code> getLatestViaApi(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        return codeRepository.findTop10ByTimeLessThanEqualAndViewsLessThanEqualOrderByDateDesc(0L, 0);
    }

    @GetMapping(path = "/code/latest")
    public String getLatest(HttpServletResponse response) {
        response.addHeader("Content-Type", "text/html");

        Map<String, Object> root= new HashMap<>();
        root.put("codes", codeRepository.findTop10ByTimeLessThanEqualAndViewsLessThanEqualOrderByDateDesc(0, 0));
        root.put("title", "Latest");
        Writer writer = new StringWriter();
        try {
            showCode.process(root, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Something was wrong";
        }
    }

    @PostMapping(path = "/api/code/new")
    public Resp postNewCode(@RequestBody Code codePosted) {
        codeRepository.save(codePosted);
        return new Resp(codePosted.getUuid());
    }

    @GetMapping(path = "/code/new")
    public String getForm(HttpServletResponse response) {
        response.addHeader("Content-Type", "text/html");
        Writer writer = new StringWriter();
        try {
            createCode.process(null, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Something was wrong";

        }
    }

    @JsonSerialize
    class Resp {
        String id;
        Resp(String id) {
            this.id = id;
        }
        public String getId(){
            return id;
        }

    }

}

