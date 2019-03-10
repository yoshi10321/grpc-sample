import io.ktor.application.*
import io.ktor.response.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    val client = HttpClient(CIO) {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!!!", contentType = ContentType.Text.Plain)
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/grpc/hello") {
            val client = HelloWorldClient("localhost", 6565)
            try {
                val result = client.greet("yoshito")
                call.respondText(result, contentType = ContentType.Text.Plain)
            } finally {
                client.shutdown()
            }
        }

        get("/grpc/route/list") {
            val client = RouteClient("localhost", 6565)
            try {
                client.listFeatures()

                call.respondText("success", contentType = ContentType.Text.Plain)
            } finally {
                client.shutdown()
            }
        }

        get("/grpc/route/client/list") {
            val client = RouteClient("localhost", 6565)
            try {
                client.recordRoute()

                call.respondText("success", contentType = ContentType.Text.Plain)
            } catch(e: Exception) {
                call.respondText("Failed", contentType = ContentType.Text.Plain)
            } finally {
                client.shutdown()
            }
        }
    }
}

