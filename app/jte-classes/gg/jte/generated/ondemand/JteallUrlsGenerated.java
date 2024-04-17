package gg.jte.generated.ondemand;
import hexlet.code.dto.UrlsPage;
import hexlet.code.dto.BasePage;
import hexlet.code.repository.UrlCheckRepository;
import java.time.format.DateTimeFormatter;
public final class JteallUrlsGenerated {
	public static final String JTE_NAME = "allUrls.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,4,4,4,6,9,9,27,27,30,30,30,33,33,33,33,33,33,33,36,36,37,39,39,39,39,40,40,43,43,44,45,45,45,46,46,49,49,57,57,57,57,57,4,5,5,5,5};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, UrlsPage urlsPage, BasePage page) {
		gg.jte.generated.ondemand.layout.JtepageGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n    <section>\n\n        <div class=\"container mt-5\">\n            <div class=\"row\">\n                <div>\n                    <h1>Сайты</h1>\n                    <table class=\"table table-bordered table-hover mt-3\">\n                        <thead>\n                        <tr>\n                            <th class=\"col-1\">ID</th>\n                            <th>Имя</th>\n                            <th class=\"col-2\">Последняя проверка</th>\n                            <th class=\"col-1\">Код ответа</th>\n                        </tr>\n                        </thead>\n                        <tbody>\n\n                        ");
				for (var url : urlsPage.getUrls()) {
					jteOutput.writeContent("\n                            <tr>\n                                <td>\n                                    ");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(url.getId());
					jteOutput.writeContent("\n                                </td>\n                                <td>\n                                    <a href=\"urls/");
					jteOutput.setContext("a", "href");
					jteOutput.writeUserContent(url.getId());
					jteOutput.setContext("a", null);
					jteOutput.writeContent("\">");
					jteOutput.setContext("a", null);
					jteOutput.writeUserContent(url.getName());
					jteOutput.writeContent("</a>\n                                </td>\n                                <td>\n                                    ");
					if (!UrlCheckRepository.getEntities(url.getId()).isEmpty()) {
						jteOutput.writeContent("\n                                        ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(UrlCheckRepository.getEntities(url.getId()).get(0).getCreatedAt()
                                            .toLocalDateTime()
                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
						jteOutput.writeContent("\n                                    ");
					}
					jteOutput.writeContent("\n                                </td>\n                                <td>\n                                    ");
					if (!UrlCheckRepository.getEntities(url.getId()).isEmpty()) {
						jteOutput.writeContent("\n                                        ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(UrlCheckRepository.getEntities(url.getId()).get(0)
                                            .getStatusCode());
						jteOutput.writeContent("\n                                    ");
					}
					jteOutput.writeContent("\n                                </td>\n                            </tr>\n                        ");
				}
				jteOutput.writeContent("\n                        </tbody>\n                    </table>\n                </div>\n            </div>\n        </div>\n\n    </section>\n");
			}
		}, page);
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		UrlsPage urlsPage = (UrlsPage)params.get("urlsPage");
		BasePage page = (BasePage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, urlsPage, page);
	}
}