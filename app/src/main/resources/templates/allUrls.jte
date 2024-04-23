@import hexlet.code.dto.UrlsPage
@import hexlet.code.dto.BasePage
@import java.time.format.DateTimeFormatter
@param UrlsPage urlsPage
@param BasePage page
@template.layout.page(
content = @`
    <section>

        <div class="container mt-5">
            <div class="row">
                <div>
                    <h1>Сайты</h1>
                    <table class="table table-bordered table-hover mt-3">
                        <thead>
                        <tr>
                            <th class="col-1">ID</th>
                            <th>Имя</th>
                            <th class="col-2">Последняя проверка</th>
                            <th class="col-1">Код ответа</th>
                        </tr>
                        </thead>
                        <tbody>

                        @for(var url : urlsPage.getUrls())
                            <tr>
                                <td>
                                    ${url.getId()}
                                </td>
                                <td>
                                    <a href="urls/${url.getId()}">${url.getName()}</a>
                                </td>
                                <td>
                                    @if(!urlsPage.getUrlsCheck().isEmpty())
                                        ${urlsPage.getUrlsCheck().get(Long.valueOf(url.getId())).getCreatedAt()
                                            .toLocalDateTime()
                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
                                    @endif
                                </td>
                                <td>
                                    @if(!urlsPage.getUrlsCheck().isEmpty())
                                        ${urlsPage.getUrlsCheck().get(url.getId()).getStatusCode()}
                                    @endif
                                </td>
                            </tr>
                        @endfor
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </section>
`,
page =page
)