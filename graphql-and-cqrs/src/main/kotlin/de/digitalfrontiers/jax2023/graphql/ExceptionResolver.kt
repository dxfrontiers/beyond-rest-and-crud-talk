package de.digitalfrontiers.jax2023.graphql

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

@Component
class ExceptionResolver : DataFetcherExceptionResolverAdapter() {
  override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? {
    return when(ex) {
      is IllegalArgumentException -> GraphqlErrorBuilder.newError()
        .errorType(ErrorType.BAD_REQUEST)
        .message(ex.message)
        .path(env.executionStepInfo.path)
        .location(env.field.sourceLocation)
        .build()
      is IllegalStateException -> GraphqlErrorBuilder.newError()
        .errorType(ErrorType.FORBIDDEN)
        .message(ex.message)
        .path(env.executionStepInfo.path)
        .location(env.field.sourceLocation)
        .build()
      else -> null
    }
  }
}
