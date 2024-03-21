package ru.yarsu.routes

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.template.TemplateRenderer
import ru.yarsu.domain.operations.Specialists
import ru.yarsu.models.DeleteConfirm
import ru.yarsu.templates.ContextRender

fun specialistsDeleteRouter(
    specialists: Specialists,
    htmlView: ContextRender,
) = routes(
    "/" bind Method.GET to showDeleteSpecialists(htmlView, specialists),
    "/" bind Method.POST to deleteSpecialists(specialists),
)

fun showDeleteSpecialists(
    htmlView: ContextRender,
    specialists: Specialists,
): HttpHandler = handler@{ request ->
    val indexString = request.path("num").orEmpty()
    val index = indexString.toIntOrNull() ?: return@handler Response(Status.BAD_REQUEST)
    val specialist = specialists.fetch(index) ?: return@handler Response(Status.BAD_REQUEST)
    Response(Status.OK).with(htmlView(request) of DeleteConfirm(specialist, index))
}

fun deleteSpecialists(specialists: Specialists): HttpHandler = {
    val path = it.path("num").toString().toInt()
    if (it.form().findSingle("confirm").equals("on")) {
        specialists.remove(path)
    }
    Response(Status.FOUND).header("Location", "/listofspecialists")
}
