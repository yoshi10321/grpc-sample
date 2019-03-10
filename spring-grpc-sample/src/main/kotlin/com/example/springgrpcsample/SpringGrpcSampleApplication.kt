package com.example.springgrpcsample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringGrpcSampleApplication

fun main(args: Array<String>) {
	runApplication<SpringGrpcSampleApplication>(*args)
}
