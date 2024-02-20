@import hexlet.code.dto.UrlPage
@import java.time.format.DateTimeFormatter
@param UrlPage urlPage

@template.layout.page(page = null, content = @`
    <section>

        <div class="container-lg mt-5">
            <h1>Сайт: ${urlPage.getUrl().getName()}</h1>

            <table class="table table-bordered table-hover mt-3">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td>${urlPage.getUrl().getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${urlPage.getUrl().getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${urlPage.getUrl().getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</td>
                </tr>
                </tbody>
            </table>

            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th class="col-1">Код ответа</th>
                    <th>Заголовок</th>
                    <th>Заголовок первого уровня</th>
                    <th>Описание</th>
                    <th class="col-2">Дата проверки</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>

    </section>
` )