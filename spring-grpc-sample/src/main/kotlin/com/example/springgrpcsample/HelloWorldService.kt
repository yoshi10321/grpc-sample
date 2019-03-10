package com.example.springgrpcsample


import com.example.grpc.helloworld.GreeterGrpc
import com.example.grpc.helloworld.HelloReply
import com.example.grpc.helloworld.HelloRequest
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService

/**
 * @author Yoshito Mitsui,
 *         CyberAgent. Inc
 */
@GRpcService
class HelloWorldService: GreeterGrpc.GreeterImplBase() {
    override fun sayHello(request: HelloRequest?, responseObserver: StreamObserver<HelloReply>?) {
        println("access from ${request?.name}")
        val reply = HelloReply.newBuilder().setMessage("Hello ${request?.name}").build()
        responseObserver?.onNext(reply)
        responseObserver?.onCompleted()
    }


}