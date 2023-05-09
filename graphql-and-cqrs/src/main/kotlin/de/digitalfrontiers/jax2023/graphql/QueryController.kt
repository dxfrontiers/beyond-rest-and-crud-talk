package de.digitalfrontiers.jax2023.graphql

import de.digitalfrontiers.jax2023.graphql.view.GiftCardEntity
import de.digitalfrontiers.jax2023.graphql.view.GiftCardRepository
import de.digitalfrontiers.jax2023.graphql.view.GiftCardTransactionRepository
import graphql.GraphqlErrorException
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Controller

@Controller
class QueryController(
  private val giftCardRepository: GiftCardRepository,
  private val giftCardTransactionRepository: GiftCardTransactionRepository
) {

  @QueryMapping
  fun giftCard(@Argument id: String) = giftCardRepository
    .findById(id).orElseThrow {
      GraphqlErrorException.Builder()
        .errorClassification(ErrorType.NOT_FOUND)
        .message("Gift card with id $id not found")
        .build()
    }

  @SchemaMapping(typeName = "GiftCard", field = "transactions")
  fun transactions(parent: GiftCardEntity) =
    giftCardTransactionRepository.findByGiftCardIdOrderByTransactionId(parent.id)
}
