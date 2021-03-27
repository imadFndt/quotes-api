package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.UserCredentials
import com.fndt.quote.controllers.factory.UsersUseCaseFactory
import com.fndt.quote.controllers.util.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class UserController(private val useCaseManager: UsersUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing {
        updateAvatar()
        authenticate()
        getUserInfo()
    }

    private fun Route.authenticate() {
        route(REGISTRATION_ENDPOINT) { get { call.registerAndRespond() } }
    }

    private fun Route.getUserInfo() {
        getExt(ROLE_ENDPOINT) { respond(it.user) }
    }

    val uploadDir = "./files"

    private fun Route.updateAvatar() = routePathWithAuth("/avatar") {
        postExt { principal ->
            val multipart = receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val (title, ext) = File(part.originalFileName).nameAndExtension
                        val file = File(
                            uploadDir,
                            "upload-${System.currentTimeMillis()}-${principal.user.id}-${title.hashCode()}.$ext"
                        )
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output -> input.copyToSuspend(output) }
                        }
                        // File is ready
                    }
                    else -> Unit
                }
                part.dispose()
            }
        }
    }

    private suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 4 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Long {
        return withContext(dispatcher) {
            val buffer = ByteArray(bufferSize)
            var bytesCopied = 0L
            var bytesAfterYield = 0L
            while (true) {
                val bytes = read(buffer).takeIf { it >= 0 } ?: break
                out.write(buffer, 0, bytes)
                if (bytesAfterYield >= yieldSize) {
                    yield()
                    bytesAfterYield %= yieldSize
                }
                bytesCopied += bytes
                bytesAfterYield += bytes
            }
            return@withContext bytesCopied
        }
    }

    private suspend fun ApplicationCall.registerAndRespond() {
        val credentials = receiveCatching<UserCredentials>() ?: return
        val result = try {
            useCaseManager.registerUseCase(credentials.login, credentials.password).run()
            SUCCESS to HttpStatusCode.OK
        } catch (e: Exception) {
            FAILURE to HttpStatusCode.NotAcceptable
        }
        respondText(text = result.first, status = result.second)
    }
}

private val File.nameAndExtension: Pair<String, String> get() = nameWithoutExtension to extension
