package ru.yarsu

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.invalidateCookie
import org.http4k.filter.ServerFilters
import org.http4k.lens.*
import org.http4k.routing.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.ac.uniyar.web.filters.JwtTools
import ru.yarsu.domain.operations.*
import ru.yarsu.files.*
import ru.yarsu.models.*
import ru.yarsu.routes.*
import ru.yarsu.templates.ContextRender
import ru.yarsu.templates.ContextTemplate
import kotlin.io.path.Path

fun main() {
    val contexts = RequestContexts()
    val permissionLens: RequestContextLens<Permission?> = RequestContextKey.required(contexts, "permission")
    val userLens: RequestContextLens<User?> = RequestContextKey.optional(contexts, "user")
    val creation = initCreation()
    val operateCreation = OperateCreation(creation, Path("settings.json"))
    val renderer = ContextTemplate().hotReload("src/main/resources")
    val specialists = Specialists()
    val jwtTools = JwtTools(operateCreation.settings.salt, issuer = "ru.ac.uniyar.WebApplication")
    specialists.creation()
    val htmlView = ContextRender(renderer, ContentType.TEXT_HTML)
    htmlView.associateContextLens("user", userLens)
    htmlView.associateContextLens("permission", permissionLens)
    val createHandlers = CreateHandlers(
        operateCreation,
        permissionLens,
        htmlView,
        jwtTools
    )
    val dynamicRouter = formRouter(createHandlers, specialists, htmlView, permissionLens)
    val staticFilesHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public"))
    val routes = routes(
        dynamicRouter,
        staticFilesHandler,
    )
    val authorizedApp = authenticationFilter(
        userLens,
        operateCreation.fetchUserId,
        jwtTools,
    ).then(
        authorizationFilter(
            userLens,
            permissionLens,
            operateCreation.fetchPermissionId
        ).then(routes)
    )
    val app = routes(authorizedApp, staticFilesHandler)
    val printingApp: HttpHandler = ServerFilters.InitialiseRequestContext(contexts)
        .then(ErrorInformation(htmlView))
        .then(app)
    val server = printingApp.asServer(Netty(3608)).start()
    println("Server started on http://localhost:" + server.port())
}

fun formRouter(
    createHandlers: CreateHandlers,
    specialists: Specialists,
    htmlView: ContextRender,
    permissionLens: RequestContextLens<Permission?>
) = routes(
    "/" bind GET to redirectToStartPage(),
    "/searchforaspecialist" bind GET to showStartPage(htmlView),
    "/listofspecialists" bind GET to showListOfSpecialists(specialists, htmlView, permissionLens),
    "/specialists/page={num}" bind GET to showAdvertisement(specialists, htmlView),
    "/add/page={num}" bind specialistsAddRouter(specialists, htmlView),
    "/edit/page={num}" bind specialistsEditRouter(specialists, htmlView),
    "/delete/page={num}" bind specialistsDeleteRouter(specialists, htmlView),
    "/registration" bind GET to createHandlers.showRegistrationForm,
    "/registration" bind Method.POST to createHandlers.addUser,
    "/login" bind GET to createHandlers.showLoginForm,
    "/login" bind Method.POST to createHandlers.authenticateUser,
    "/logout" bind GET to LogoutUser(),
    "/users" bind GET to createHandlers.showUsers,
    "/users/{id}" bind GET to createHandlers.showUser,
    static(ResourceLoader.Classpath("/ru/yarsu/public/")),
)

fun redirectToStartPage(): HttpHandler = {
    Response(FOUND).header("Location", "/searchforaspecialist")
}

fun showStartPage(htmlView: ContextRender): HttpHandler = {request ->
    val str = "Здесь Вы можете ознакомиться с характеристиками наших специалистов.\n" +
            "            При необходимости Вы можете выбрать одного или нескольких из них.\n" +
            "            Также Вы можете узнать описание их профессий."
    Response(OK).with(htmlView(request) of StartPage(str))
}

fun showListOfSpecialists(
    specialists: Specialists,
    htmlView: ContextRender,
    permissionLens: RequestContextLens<Permission?>,
): HttpHandler = {request ->
    val permission = permissionLens(request)
    if (!permission!!.showSpecialists) {
        Response(Status.UNAUTHORIZED)
    } else {
        val uri = request.uri
        val specialistsCopy = Specialists()
        var countPage = 5
        specialistsCopy.specialists.addAll(specialists.specialists)
        var parametr = uri.queries().findSingle("page")
        if (parametr.isNullOrEmpty()) {
            parametr = "0"
        }
        if (!uri.queries().findSingle("records").isNullOrEmpty()) {
            countPage = uri.queries().findSingle("records")!!.toInt()
        }
        val pages = Pagination(parametr.toInt(), specialistsCopy.amountPage(countPage), countPage)
        val specialistsPageAmount = specialistsCopy.byPageNumber(parametr.toInt(), countPage)
        if (specialistsPageAmount.isEmpty()) {
            val viewModel =
                DataList(specialists.byPageNumber(parametr.toInt(), countPage), pages, specialists.newNumber())
            Response(OK).with(htmlView(request) of viewModel)
        } else {
            val viewModel =
                DataList(specialistsCopy.byPageNumber(parametr.toInt(), countPage), pages, specialists.newNumber())
            Response(OK).with(htmlView(request) of viewModel)
        }
    }
}

fun showAdvertisement(
        specialists: Specialists,
        htmlView: ContextRender,
): HttpHandler = handler@{ request ->
    val indexString = request.path("num").orEmpty()
    val index = indexString.toIntOrNull() ?: return@handler Response(Status.BAD_REQUEST)
    val specialist = specialists.fetch(index) ?: return@handler Response(Status.BAD_REQUEST)
    Response(OK).with(htmlView(request) of DetailedInformation(specialist, index))
}

class LogoutUser: HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.FOUND)
            .header("Location", "/")
            .invalidateCookie("token")
    }
}

val serviceFormLens = FormField.nonEmptyString().required("service")
val categoryFormLens = FormField.nonEmptyString().required("category")
val descriptionFormLens = FormField.string().required("description")
val nameFormLens = FormField.nonEmptyString().required("name")
val educationFormLens = FormField.string().required("education")
val phoneFormLens = FormField.nonEmptyString().required("phone")
val specialistsFormLens = Body.webForm(
    Validator.Feedback,
    serviceFormLens,
    categoryFormLens,
    descriptionFormLens,
    nameFormLens,
    educationFormLens,
    phoneFormLens,
).toLens()
