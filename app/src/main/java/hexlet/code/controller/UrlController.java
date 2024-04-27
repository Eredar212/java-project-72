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
import java.util.Collections;
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
                        new UrlsPage(UrlRepository.getEntities(), UrlCheckRepository.findLastCheck())));
            } else {
                var url = new Url(normalizedUrl);
                UrlRepository.save(url);
                var page = new BasePage("success", "Страница успешно добавлена");
                ctx.render("allUrls.jte", Map.of("page", page, "urlsPage",
                        new UrlsPage(UrlRepository.getEntities(), UrlCheckRepository.findLastCheck())));
            }

        } catch (ValidationException | URISyntaxException e) {
            var page = new BasePage("danger", "Некорректный URL");
            ctx.render("index.jte", Collections.singletonMap("page", page));
        }
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        Map<String, UrlCheck> urlsCheck = UrlCheckRepository.findLastCheck();
        var urlsPage = new UrlsPage(urls, urlsCheck);
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
        var urlChecks = UrlCheckRepository.findByUrlId(id);
        var page = new UrlPage(url, urlChecks);
        ctx.render("show.jte", Collections.singletonMap("urlPage", page));
    }

    public static void check(Context ctx) {
        try {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            var url = UrlRepository.find(id).orElseThrow(() -> new NotFoundResponse("Url not found"));
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            var statusCode = response.getStatus();
            var body = Jsoup.parse(response.getBody());
            var title = body.title();
            var h1 = body.selectFirst("h1") != null ? body.selectFirst("h1").wholeText() : "none";
            String description = body.selectFirst("meta[name=description]") != null
                    ? body.selectFirst("meta[name=description]").attr("content") : "none";
            UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, id);
            UrlCheckRepository.save(urlCheck);
            ctx.redirect(NamedRoutes.urlPath(id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
        }
    }
}
