package ru.yarsu.routes

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.yarsu.*
import ru.yarsu.domain.operations.Advertisement
import ru.yarsu.domain.operations.AdvertisementWithoutDate
import ru.yarsu.domain.operations.Specialists
import ru.yarsu.models.Form
import ru.yarsu.models.ShowForm
import ru.yarsu.templates.ContextRender
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun specialistsAddRouter(
    specialists: Specialists,
    htmlView: ContextRender,
) = routes(
    "/" bind Method.GET to showAdvertisementForm(specialists, htmlView),
    "/" bind Method.POST to addSpecialistsLens(specialists, htmlView),
)

fun showAdvertisementForm(
    specialists: Specialists,
    htmlView: ContextRender,
): HttpHandler = handler@{ request ->
    val indexString = request.path("num").orEmpty()
    val index = indexString.toIntOrNull() ?: return@handler Response(Status.BAD_REQUEST)
    if (index != specialists.newNumber()) {
        return@handler Response(Status.BAD_REQUEST)
    }
    Response(Status.OK).with(htmlView(request) of ShowForm())
}

fun addSpecialistsLens(
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
    if (webForm.errors.isNotEmpty()) {
        Response(Status.OK).with(htmlView(request) of Form(webForm, errorName, errorPhone))
    } else if (!nameFormLens(webForm).matches(Regex("""[А-Я][а-я]{2,25}(-[А-Я][а-я]{2,25})?\s[А-Я][а-я]{2,25}(-[А-Я][а-я]{2,25})?(\s[А-Я][а-я]{2,25})?"""))) {
        errorName = "Неправильный формат ФИО"
        Response(Status.OK).with(htmlView(request) of Form(webForm, errorName, errorPhone))
    } else if (!phoneFormLens(webForm).matches(Regex("""[+]7-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}"""))) {
        errorPhone = "Неправильный формат номера телефона"
        Response(Status.OK).with(htmlView(request) of Form(webForm, errorName, errorPhone))
    } else {
        specialists.add(
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
    }
}
