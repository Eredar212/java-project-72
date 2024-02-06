package hexlet.code;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
    public static Javalin getApp(){
        Logger logger = LoggerFactory.getLogger(App.class);
        var app = Javalin.create();
        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });
        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }
}
