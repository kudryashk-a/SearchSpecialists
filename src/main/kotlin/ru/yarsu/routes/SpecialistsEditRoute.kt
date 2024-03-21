package ru.yarsu.routes

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import ru.yarsu.*
import ru.yarsu.domain.operations.Advertisement
import ru.yarsu.domain.operations.AdvertisementWithoutDate
import ru.yarsu.domain.operations.Specialists
import ru.yarsu.models.EditForm
import ru.yarsu.models.ShowEditForm
import ru.yarsu.models.ShowForm
import ru.yarsu.templates.ContextRender
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun specialistsEditRouter(
    specialists: Specialists,
    htmlView: ContextRender,
) = routes(
    "/" bind Method.GET to showEditSpecialists(specialists, htmlView),
    "/" bind Method.POST to editSpecialists(specialists, htmlView),
)

fun showEditSpecialists(
    specialists: Specialists,
    htmlView: ContextRender,
): HttpHandler = handler@{ request ->
    val indexString = request.path("num").orEmpty()
    val index = indexString.toIntOrNull() ?: return@handler Response(Status.BAD_REQUEST)
    if (index < 0 && index >= specialists.newNumber()) {
        return@handler Response(Status.BAD_REQUEST)
    }
    Response(Status.OK).with(htmlView(request) of ShowEditForm())
}

fun editSpecialists(
    specialists: Specialists,
    htmlView: ContextRender,
): HttpHandler = { request ->
    val identifier = request.path("num").orEmpty().toInt()
    val webForm = specialistsFormLens(request)
    var errorName = ""
    var errorPhone = ""
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
    val formatted = LocalDateTime.parse(current.format(formatter), formatter)
    if (!nameFormLens(webForm).matches(Regex("""[А-Я][а-я]{2,25}(-[А-Я][а-я]{2,25})?\s[А-Я][а-я]{2,25}(-[А-Я][а-я]{2,25})?(\s[А-Я][а-я]{2,25})?"""))) {
        errorName = "Неправильный формат ФИО"
    }
    if (!phoneFormLens(webForm).matches(Regex("""[+]7-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}"""))) {
        errorPhone = "Неправильный формат номера телефона"
    }
    if (webForm.errors.isEmpty() && errorName.isEmpty() && errorPhone.isEmpty()) {
        specialists.edit(
            Advertisement(
                identifier,
                AdvertisementWithoutDate(
                    serviceFormLens(webForm),
                    categoryFormLens(webForm),
                    descriptionFormLens(webForm),
                    nameFormLens(webForm),
                    educationFormLens(webForm),
                    phoneFormLens(webForm),
                ),
                formatted,
            ),
        )
        Response(Status.FOUND).header("Location", "/listofspecialists")
    } else {
        Response(Status.OK).with(htmlView(request) of EditForm(webForm, errorName, errorPhone))
    }
}
