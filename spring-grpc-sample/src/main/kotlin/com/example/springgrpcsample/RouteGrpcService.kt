package com.example.springgrpcsample

import com.example.grpc.routeguide.RouteGuideGrpc
import com.example.grpc.routeguide.RouteGuideOuterClass
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService

/**
 * @author Yoshito Mitsui,
 *         CyberAgent. Inc
 */
@GRpcService
class RouteGrpcService: RouteGuideGrpc.RouteGuideImplBase() {

    override fun getFeature(request: RouteGuideOuterClass.Point?, responseObserver: StreamObserver<RouteGuideOuterClass.Feature>?) {
        responseObserver?.onNext(checkFeature())
        responseObserver?.onCompleted()
    }

    private fun checkFeature(): RouteGuideOuterClass.Feature {
        val point = RouteGuideOuterClass.Point.newBuilder().setLatitude(100).setLongitude(200)
        return RouteGuideOuterClass.Feature.newBuilder().setName("yoshito").setLocation(point).build()
    }

    override fun recordRoute(responseObserver: StreamObserver<RouteGuideOuterClass.RouteSummary>?): StreamObserver<RouteGuideOuterClass.Point> {
        return  object : StreamObserver<RouteGuideOuterClass.Point> {
            override fun onNext(point: RouteGuideOuterClass.Point) {
                println("${point.longitude} ${point.latitude}")
            }

            override fun onError(t: Throwable) {
                println("error")
            }

            override fun onCompleted() {
                println("completed")
            }
        }
    }

    override fun listFeatures(request: RouteGuideOuterClass.Rectangle?, responseObserver: StreamObserver<RouteGuideOuterClass.Feature>?) {
        for (i in 0..10) {
            responseObserver?.onNext(checkFeature())
        }
        responseObserver?.onCompleted()
    }

}