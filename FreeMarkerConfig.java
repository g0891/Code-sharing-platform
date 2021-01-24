package platform;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;

public class FreeMarkerConfig {
    public static Configuration create() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);

        try {
            cfg.setDirectoryForTemplateLoading(new File("./src/resources/templates"));
        } catch (IOException e) {
            return null;
        }

        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        return cfg;

    }

    public static Template load(Configuration cfg, String name) {
        Template tmpl = null;
        try {
            tmpl = cfg.getTemplate(name);
        } catch (Exception e) {}
        return tmpl;
    }
}
