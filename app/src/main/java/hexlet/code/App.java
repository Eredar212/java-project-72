package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() {
        Logger logger = LoggerFactory.getLogger(App.class);

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });
        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        //root
        app.get("/", ctx -> ctx.render("index.jte"));
        app.post("/urls", UrlController::create);
        app.get("/urls", UrlController::index);
        app.get("/urls/{id}", UrlController::show);
        app.post("/urls/{id}/checks", UrlController::check);


        //init DB
        var hikariConfig = new HikariConfig();
        if (System.getenv("JDBC_DATABASE_URL") != null) {
            hikariConfig.setJdbcUrl(System.getenv("JDBC_DATABASE_URL"));
        } else {
            hikariConfig.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        }
        //System.out.println("DATABASE_URL" + System.getenv("DATABASE_URL"));

        var dataSource = new HikariDataSource(hikariConfig);
        /*var url = App.class.getClassLoader().getResource("schema.sql");
        var file = new File(url.getFile());
        String sql = null;
        try {
            sql = Files.lines(file.toPath())
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        String sql = null;
        try {
            sql = getFile("schema.sql");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        BaseRepository.dataSource = dataSource;

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

    public static String getFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }

    }
}
