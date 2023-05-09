package de.digitalfrontiers.jax2023.graphql

import de.digitalfrontiers.jax2023.graphql.domain.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class MutationController(private val gateway: CommandGateway) {
  @MutationMapping
  fun issueCard(@Argument amount: Int): IssueCardResult {
    val id = UUID.randomUUID().toString()
    gateway.sendAndWait<Unit>(
      IssueCardCommand(
        id = id,
        amount = amount
      )
    )
    return IssueCardResult(id, amount)
  }

  @MutationMapping
  fun redeemCard(@Argument cardId: String, @Argument amount: Int): RedeemCardResult {
    val transaction = gateway.sendAndWait<GiftCardTransaction>(
      RedeemCardCommand(
        id = cardId,
        amount = amount
      )
    )
    return RedeemCardResult(transaction.remainingAmount)
  }

  @MutationMapping
  fun increaseCredit(@Argument cardId: String, @Argument amount: Int): IncreaseCreditResult {
    val transaction = gateway.sendAndWait<GiftCardTransaction>(
      IncreaseCreditCommand(
        id = cardId,
        amount = amount
      )
    )
    return IncreaseCreditResult(transaction.remainingAmount)
  }

  @MutationMapping
  fun revokeCard(@Argument cardId: String): CardRevocationResult {
    gateway.sendAndWait<Unit>(RevokeCardCommand(cardId))
    return CardRevocationResult(true)
  }

  @MutationMapping
  fun revokeTransaction(@Argument cardId: String, @Argument transactionId: Int): TransactionRevocationResult {
    val transaction = gateway.sendAndWait<GiftCardTransaction>(
      RevokeTransactionCommand(
        id = cardId,
        transactionId = transactionId
      )
    )
    return TransactionRevocationResult(transaction.remainingAmount)
  }
}


data class IssueCardResult(val id: String, val amount: Int)
data class RedeemCardResult(val remainingAmount: Int)
data class IncreaseCreditResult(val remainingAmount: Int)
data class CardRevocationResult(val revoked: Boolean)
data class TransactionRevocationResult(val remainingAmount: Int)
