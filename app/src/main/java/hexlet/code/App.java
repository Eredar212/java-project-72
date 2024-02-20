package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.javalin.rendering.template.JavalinJte;

public class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() {
        Logger logger = LoggerFactory.getLogger(App.class);

        var app = Javalin.create();
        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        //root
        app.get("/", ctx -> ctx.render("index.jte","Hello World"));

        //init DB
        var hikariConfig = new HikariConfig();
        if (System.getenv("DATABASE_URL") != null) {
            hikariConfig.setUsername(System.getenv("DATABASE_USERNAME"));
            hikariConfig.setPassword(System.getenv("DATABASE_PASSWORD"));
            hikariConfig.setJdbcUrl(System.getenv("DATABASE_URL"));
        } else {
            hikariConfig.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        }

        JavalinJte jte = new JavalinJte(createTemplateEngine());
        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }
}
