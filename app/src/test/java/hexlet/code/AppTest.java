package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    private static Javalin app;

    private static MockWebServer mockServer;

    public static String getSite(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }

    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        var mockResponse = new MockResponse()
                .setBody(getSite("site.html"));
        mockServer.enqueue(mockResponse);
        mockServer.start();
    }

    @BeforeEach
    public void setUp() {
        app = App.getApp();
    }

    @Test
    void testMainPage() throws Exception {

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void testRoot() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });

    }

    @Test
    void testWrongUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlsPath(), "url=bu bu bu.ru");
            assertThat(response.body().string()).contains("Некорректный URL");
        });
    }

    @Test
    void testRegisterNewSites() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.google.ru/";
            client.post(NamedRoutes.urlsPath(), requestBody);
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();

            assertEquals("https://www.google.ru", UrlRepository.find("https://www.google.ru").get().getName());
            assertThat(bodyString).contains("Сайты");
            assertThat(bodyString).contains("https://www.google.ru");
        });
    }

    @Test
    void testDoubleSite() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://ru.hexlet.io";
            client.post(NamedRoutes.urlsPath(), requestBody);
            client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(UrlRepository.getEntities()).hasSize(1);
        });
    }

    @Test
    void testShowUrl() {
        JavalinTest.test(app, (server, client) -> {
            var name = "https://ru.hexlet.io";
            var url = new Url(name);
            UrlRepository.save(url);

            assertTrue(UrlRepository.find(url.getId()).isPresent());
            var id = url.getId().toString();
            var response = client.get(NamedRoutes.urlPath(id));
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();
            assertThat(bodyString).contains("Сайт:");
            assertThat(bodyString).contains(name);
        });
    }

    @Test
    void testCheckUrl() throws SQLException {
        var url = mockServer.url("/").toString();
        Url urlForCheck = new Url(url);
        UrlRepository.save(urlForCheck);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + urlForCheck.getId() + "/checks");
            assertThat(response.code()).isEqualTo(200);
        });
        List<UrlCheck> urlChecks = UrlCheckRepository.getEntities(urlForCheck.getId());
        assertThat(urlChecks.isEmpty()).isFalse();

        UrlCheck actualCheck = urlChecks.get(0);

        assertThat(actualCheck.getStatusCode()).isEqualTo(200);
        assertThat(actualCheck.getTitle()).isEqualTo("Title");
        assertThat(actualCheck.getH1()).isEqualTo("Test H1 Tag");
        assertThat(actualCheck.getDescription()).isEqualTo("Хекслет — лучшая школа");
    }
}
