package com.fndt.quote.rest.controllers

import com.fndt.quote.domain.manager.UrlSchemeProvider
import com.fndt.quote.rest.dto.UserCredentials
import com.fndt.quote.rest.dto.out.toOutUser
import com.fndt.quote.rest.factory.UsersUseCaseFactory
import com.fndt.quote.rest.util.*
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

class UserController(
    private val useCaseManager: UsersUseCaseFactory,
    private val uploadDir: String
) : RoutingController {
    override fun route(routing: Routing) = routing {
        register()
        updateAvatar()
        getUserInfo()
    }

    private fun Route.register() {
        post(REGISTRATION_ENDPOINT) {
            call.processRequest {
                val credentials = receive<UserCredentials>()
                useCaseManager.registerUseCase(credentials.login, credentials.password).run()
            }.defaultPostChain(call)
        }
    }

    private fun Route.getUserInfo() = routePathWithAuth(ROLE_ENDPOINT) {
        getExt { respond(it.user.toOutUser(UrlSchemeProvider)) }
    }

    private fun Route.updateAvatar() = routePathWithAuth("/avatar") {
        postExt { principal ->
            processRequest {
                val file = downloadImage(uploadDir)
                useCaseManager.changeProfilePictureUseCase(file, principal.user).run()
            }.defaultPostChain(this)
        }
    }
}

private val File.nameAndExtension: Pair<String, String> get() = nameWithoutExtension to extension

private suspend fun ApplicationCall.downloadImage(uploadDir: String): File {
    val multipart = receiveMultipart()
    var result: File? = null
    multipart.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                if (part.contentType != ContentType.Image.PNG) throw IllegalArgumentException("bad image")
                part.contentDisposition
                val (title, ext) = File(part.originalFileName).nameAndExtension
                val file = File(
                    uploadDir, "upload-${System.currentTimeMillis()}-${title.hashCode()}.$ext"
                )
                part.streamProvider().use { input ->
                    file.outputStream().buffered().use { output -> input.copyToSuspend(output) }
                }
                result = file
            }
            else -> Unit
        }
        part.dispose()
    }
    return result ?: throw IllegalStateException()
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
