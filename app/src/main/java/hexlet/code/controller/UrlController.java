package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.validation.ValidationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class UrlController {
    public static void create(Context ctx) throws SQLException {
        try {
            var name = ctx.formParamAsClass("url", String.class)
                    .check(value -> !value.isEmpty(), "Название не должно быть пустым")
                    .get();
            var parsedUrl = new URI(name);
            String protocol = parsedUrl.getScheme();
            String authority = parsedUrl.getAuthority();
            var normalizedUrl = String.format("%s://%s", protocol, authority);
            if (UrlRepository.find(normalizedUrl).isPresent()) {
                var page = new BasePage("info", "Страница уже существует");
                ctx.render("allUrls.jte", Map.of("page", page, "urlsPage", new UrlsPage(UrlRepository.getEntities())));
            } else {
                var url = new Url(normalizedUrl);
                UrlRepository.save(url);
                var page = new BasePage("success", "Страница успешно добавлена");
                ctx.render("allUrls.jte", Map.of("page", page, "urlsPage", new UrlsPage(UrlRepository.getEntities())));
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

    public static void show(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = null;
        try {
            url = UrlRepository.find(id).isPresent() ? UrlRepository.find(id).get() : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        var page = new UrlPage(url);
        ctx.render("show.jte", Collections.singletonMap("urlPage", page));
    }
}
