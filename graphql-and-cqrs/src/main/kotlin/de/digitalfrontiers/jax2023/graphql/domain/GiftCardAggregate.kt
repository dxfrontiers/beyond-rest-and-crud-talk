package de.digitalfrontiers.jax2023.graphql.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*
import kotlin.properties.Delegates

@Aggregate
class GiftCardAggregate {

  @AggregateIdentifier
  lateinit var id: String
  var amount by Delegates.notNull<Int>()
  var revoked = false
  val transactions = mutableListOf<TransactionData>()

  @CommandHandler
  @CreationPolicy(AggregateCreationPolicy.ALWAYS)
  fun handle(cmd: IssueCardCommand) {
    if (cmd.amount <= 0)
      throw IllegalArgumentException("invalid amount ${cmd.amount}")
    applyEvent(CardIssuedEvent(cmd.id, cmd.amount))
  }

  @EventSourcingHandler
  fun on(e: CardIssuedEvent) {
    id = e.id
    amount = e.amount
  }

  @CommandHandler
  fun handle(cmd: RedeemCardCommand): GiftCardTransaction {
    if (revoked)
      throw IllegalStateException("card has been revoked")
    if (cmd.amount <= 0)
      throw IllegalArgumentException("invalid amount ${cmd.amount}")
    if (cmd.amount > amount)
      throw IllegalArgumentException("insufficient card credit")

    val transactionId = newTransactionId()
    val e = RedeemedEvent(
      id = id,
      transactionId = transactionId,
      amount = -cmd.amount,
      remainingAmount = amount - cmd.amount
    )
    applyEvent(e)
    return e
  }

  private fun newTransactionId(): Int = (transactions
    .takeIf { it.isNotEmpty() }
    ?.maxOf { it.transactionId }
    ?: 0) + 1

  @EventSourcingHandler
  fun onTransaction(e: GiftCardTransaction) {
    amount = e.remainingAmount
    transactions += TransactionData(
      transactionId = e.transactionId,
      amount = e.amount
    )
  }

  @CommandHandler
  fun handle(cmd: IncreaseCreditCommand): GiftCardTransaction {
    if (revoked)
      throw IllegalStateException("card has been revoked")
    if (cmd.amount <= 0)
      throw IllegalArgumentException("invalid amount ${cmd.amount}")

    val e = CreditIncreasedEvent(
      id = id,
      transactionId = newTransactionId(),
      amount = cmd.amount,
      remainingAmount = amount + cmd.amount
    )
    applyEvent(e)
    return e
  }

  @CommandHandler
  fun handle(cmd: RevokeCardCommand) {
    if (!revoked)
      applyEvent(CardRevokedEvent(id))
  }

  @EventSourcingHandler
  fun on(e: CardRevokedEvent) {
    revoked = true
  }

  data class TransactionData(val transactionId: Int, val amount: Int)

  @CommandHandler
  fun handle(cmd: RevokeTransactionCommand): GiftCardTransaction {
    // only the last transaction can be revoked
    val lastTransaction = transactions.lastOrNull()
      ?: throw IllegalArgumentException("Transaction not found")

    if (lastTransaction.transactionId != cmd.transactionId)
      throw IllegalArgumentException("Only the last transaction can be revoked")

    val e = TransactionRevokedEvent(
      id = id,
      transactionId = newTransactionId(),
      revokedTransactionId = lastTransaction.transactionId,
      amount = -lastTransaction.amount,
      remainingAmount = amount - lastTransaction.amount
    )
    applyEvent(e)
    return e
  }
}

