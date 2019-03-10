package com.example.ktorgrpcsample

import com.example.grpc.helloworld.GreeterGrpc
import com.example.grpc.helloworld.HelloReply
import com.example.grpc.helloworld.HelloRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import java.util.concurrent.TimeUnit

/**
 * @author Yoshito Mitsui,
 *         CyberAgent. Inc
 */
class HelloWorldClient

internal constructor(private val channel: ManagedChannel) {
    private val blockingStub: GreeterGrpc.GreeterBlockingStub
            = GreeterGrpc.newBlockingStub(channel)

    constructor(host: String, port: Int): this(ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build()) {

    }

    @Throws(InterruptedException::class)
    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    /** say hello to server. */
    fun greet(name: String): String {
        println("Will try to greet $name")
        val request = HelloRequest.newBuilder().setName(name).build()
        val response: HelloReply = try {
            blockingStub.sayHello(request)
        } catch (e: StatusRuntimeException) {
            println("RPC failed: ${e.status}")
            return "error"
        }

        println("Greeting ${response.message}")
        return response.message
    }

}