package com.example.ktorgrpcsample

import com.example.grpc.routeguide.RouteGuideGrpc
import com.example.grpc.routeguide.RouteGuideOuterClass
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch


/**
 * @author Yoshito Mitsui,
 *         CyberAgent. Inc
 */
class RouteClient

internal constructor(private val channel: ManagedChannel) {
    private val blockingStub: RouteGuideGrpc.RouteGuideBlockingStub
            = RouteGuideGrpc.newBlockingStub(channel)
    private val asyncStub = RouteGuideGrpc.newStub(channel)

    constructor(host: String, port: Int): this(ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build()) {
    }

    @Throws(InterruptedException::class)
    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    /** route request to server. */
    fun listFeatures() {
        val point = RouteGuideOuterClass.Point.newBuilder().setLatitude(100).setLongitude(300)
        val request = RouteGuideOuterClass.Rectangle.newBuilder().setHi(point).setLo(point).build()

        val response = try {
            val features: Iterator<RouteGuideOuterClass.Feature> = blockingStub.listFeatures(request)
            while (features.hasNext()) {
                val feature = features.next()
                println("${feature.location.latitude}  ${feature.location.longitude}")
            }
        } catch (e: StatusRuntimeException) {
            println("RPC failed: ${e.status}")
        }
    }


    fun recordRoute() {
        var finishLatch = CountDownLatch(1)

        val responseObserver = object : StreamObserver<RouteGuideOuterClass.RouteSummary> {
            override fun onNext(summary: RouteGuideOuterClass.RouteSummary) {
//                info(
//                    "Finished trip with {0} points. Passed {1} features. " + "Travelled {2} meters. It took {3} seconds.",
//                    summary.pointCount,
//                    summary.featureCount,
//                    summary.distance,
//                    summary.elapsedTime
//                )
            }

            override fun onError(t: Throwable) {
//                val status = Status.fromThrowable(t)
//                logger.log(Level.WARNING, "RecordRoute Failed: {0}", status)
                finishLatch.countDown()
            }

            override fun onCompleted() {
//                info("Finished RecordRoute")
                finishLatch.countDown()
            }
        }

        val requestObserver = asyncStub.recordRoute(responseObserver)

        try {
            for (i in 0..10) {
                val point = RouteGuideOuterClass.Point.newBuilder().setLatitude(i*10).setLongitude(i*30).build()
                requestObserver.onNext(point)
                Thread.sleep(1000)
            }
        } catch (e: RuntimeException) {
            throw e
        }

        requestObserver.onCompleted()

        finishLatch.await(1, TimeUnit.MINUTES)
    }
}