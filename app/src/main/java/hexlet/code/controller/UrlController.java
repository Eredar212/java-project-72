package hexlet.code.controller;

import hexlet.code.NamedRoutes;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class UrlController {
    public static void create(Context ctx) throws SQLException {
        try {
            var name = ctx.formParamAsClass("url", String.class).check(value -> !value.isEmpty(),
                    "Название не должно быть пустым").get();
            var parsedUrl = new URI(name);
            String protocol = parsedUrl.getScheme();
            String authority = parsedUrl.getAuthority();
            var normalizedUrl = String.format("%s://%s", protocol, authority);
            if (UrlRepository.find(normalizedUrl).isPresent()) {
                var page = new BasePage("info", "Страница уже существует");
                ctx.render("allUrls.jte", Map.of("page", page, "urlsPage",
                        new UrlsPage(UrlRepository.getEntities())));
            } else {
                Timestamp createdAt = new Timestamp(new Date().getTime());
                var url = new Url(normalizedUrl, createdAt);
                UrlRepository.save(url);
                var page = new BasePage("success", "Страница успешно добавлена");
                ctx.render("allUrls.jte", Map.of("page", page, "urlsPage",
                        new UrlsPage(UrlRepository.getEntities())));
            }

        } catch (ValidationException | URISyntaxException e) {
            var page = new BasePage("danger", "Некорректный URL");
            ctx.render("index.jte", Collections.singletonMap("page", page));
        }
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var urlsPage = new UrlsPage(urls);
        ctx.render("allUrls.jte", Collections.singletonMap("urlsPage", urlsPage));
    }

    @SneakyThrows
    public static void show(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = null;
        try {
            url = UrlRepository.find(id).isPresent() ? UrlRepository.find(id).get() : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        var urlChecks = UrlCheckRepository.getEntities(id);
        var page = new UrlPage(url, urlChecks);
        ctx.render("show.jte", Collections.singletonMap("urlPage", page));
    }

    public static void check(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            var url = UrlRepository.find(id).orElseThrow(() -> new NotFoundResponse("Url not found"));
            System.out.println(url.getName());
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            var statusCode = response.getStatus();
            var body = Jsoup.parse(response.getBody());
            var title = body.title();
            var h1 = body.selectFirst("h1") != null ? body.selectFirst("h1").wholeText() : "none";
            String description = body.selectFirst("meta[name=description]") != null
                    ? body.selectFirst("meta[name=description]").attr("content") : "none";
            Timestamp createdAt = new Timestamp(new Date().getTime());
            UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, id, createdAt);
            UrlCheckRepository.save(urlCheck);
            ctx.redirect(NamedRoutes.urlPath(id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
        }
    }
}
