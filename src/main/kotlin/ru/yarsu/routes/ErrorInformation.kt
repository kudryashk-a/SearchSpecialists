package ru.yarsu.routes

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.with
import org.http4k.lens.BiDiBodyLens
import org.http4k.template.ViewModel
import ru.yarsu.models.ErrorRequest
import ru.yarsu.models.LoginError
import ru.yarsu.templates.ContextRender

const val UNAUTHORISED_CODE = 401

class ErrorInformation(
    val htmlView: ContextRender,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler = { request ->
        val response = next(request)
        if (response.status.successful || response.header("content-type") != null && response.body.length != 0L) {
            response
        } else if(response.status.code == UNAUTHORISED_CODE) {
            response.with(htmlView(request) of LoginError(""))
        } else {
            response.with(htmlView(request) of ErrorRequest(request.uri))
        }
    }
}