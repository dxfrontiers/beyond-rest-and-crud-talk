package de.digitalfrontiers.jax2023.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GraphqlAndCqrsApplication

fun main(args: Array<String>) {
	runApplication<GraphqlAndCqrsApplication>(*args)
}
