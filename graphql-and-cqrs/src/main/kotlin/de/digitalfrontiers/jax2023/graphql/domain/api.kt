package de.digitalfrontiers.jax2023.graphql.domain

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class IssueCardCommand(
  @TargetAggregateIdentifier
  val id: String,
  val amount: Int
)

data class RedeemCardCommand(
  @TargetAggregateIdentifier
  val id: String,
  val amount: Int
)

data class IncreaseCreditCommand(
  @TargetAggregateIdentifier
  val id: String,
  val amount: Int
)

data class RevokeCardCommand(
  @TargetAggregateIdentifier
  val id: String
)

data class RevokeTransactionCommand(
  @TargetAggregateIdentifier
  val id: String,
  val transactionId: Int
)


data class CardIssuedEvent(val id: String, val amount: Int)

interface GiftCardTransaction {
  val id: String
  val transactionId: Int
  val remainingAmount: Int
  val amount: Int
}

data class RedeemedEvent(
  override val id: String,
  override val transactionId: Int,
  override val amount: Int,
  override val remainingAmount: Int
) : GiftCardTransaction

data class CreditIncreasedEvent(
  override val id: String,
  override val transactionId: Int,
  override val amount: Int,
  override val remainingAmount: Int
) : GiftCardTransaction

data class TransactionRevokedEvent(
  override val id: String,
  override val transactionId: Int,
  val revokedTransactionId: Int,
  override val amount: Int,
  override val remainingAmount: Int
) : GiftCardTransaction

data class CardRevokedEvent(
  val id: String
)
