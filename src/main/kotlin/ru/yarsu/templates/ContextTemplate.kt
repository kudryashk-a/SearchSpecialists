package ru.yarsu.templates


import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.error.LoaderException
import io.pebbletemplates.pebble.loader.FileLoader
import org.http4k.template.ViewModel
import org.http4k.template.ViewNotFound
import java.io.StringWriter

typealias ContextRenderer = (Map<String, Any?>, ViewModel) -> String

class ContextTemplate(
    private val configure: (PebbleEngine.Builder) -> PebbleEngine.Builder = { it },
    private val classLoader: ClassLoader = ClassLoader.getSystemClassLoader()
) {

    private class ContextTemplateRenderer(
        private val engine: PebbleEngine,
    ) : ContextRenderer {
        override fun invoke(context: Map<String, Any?>, viewModel: ViewModel): String = try {
            val writer = StringWriter()
            val wholeContext = context + mapOf("model" to viewModel)
            engine.getTemplate(viewModel.template() + ".peb").evaluate(writer, wholeContext)
            writer.toString()
        } catch (_: LoaderException) {
            throw ViewNotFound(viewModel)
        }
    }

    fun caching(baseTemplateDir: String): ContextRenderer {
        val loader = FileLoader()
        loader.prefix = baseTemplateDir
        return ContextTemplateRenderer(
            configure(
                PebbleEngine.Builder().cacheActive(true).loader(loader),
            ).build()
        )
    }

    fun hotReload(baseTemplateDir: String): ContextRenderer {
        val loader = FileLoader()
        loader.prefix = baseTemplateDir
        return ContextTemplateRenderer(
            configure(
                PebbleEngine.Builder().cacheActive(false).loader(loader),
            ).build()
        )
    }
}