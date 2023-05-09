package de.digitalfrontiers.jax2023.graphql

import de.digitalfrontiers.jax2023.graphql.domain.*
import org.axonframework.extension.kotlin.test.aggregateTestFixture
import org.axonframework.extension.kotlin.test.whenever
import org.junit.jupiter.api.Test

class GiftCardAggregateTest {

  @Test
  fun `issue new gift card`() {
    aggregateTestFixture<GiftCardAggregate>()
      .givenNoPriorActivity()
      .whenever(
        IssueCardCommand(
          "id1",
          47
        )
      )
      .expectEvents(
        CardIssuedEvent(
          "id1",
          47
        )
      )
  }

  @Test
  fun `partially redeem gift card`() {
    aggregateTestFixture<GiftCardAggregate>()
      .given(
        CardIssuedEvent(
          "id1",
          47
        )
      )
      .whenever(
        RedeemCardCommand(
          "id1",
          22
        )
      )
      .expectEvents(
        RedeemedEvent(
          id ="id1",
          transactionId = 1,
          amount = -22,
          remainingAmount = 25
        )
      )
  }

  @Test
  fun `overcharging a gift card fails`() {
    aggregateTestFixture<GiftCardAggregate>()
      .given(
        CardIssuedEvent(
          "id1",
          47
        )
      )
      .whenever(
        RedeemCardCommand(
          "id1",
          48
        )
      )
      .expectException(IllegalArgumentException::class.java)
  }

  @Test
  fun `revoke a gift card transaction`() {
    aggregateTestFixture<GiftCardAggregate>()
      .given(
        CardIssuedEvent(
          "id1",
          47
        ),
        RedeemedEvent(
          id ="id1",
          transactionId = 1,
          amount = -22,
          remainingAmount = 25
        )
      )
      .whenever(
        RevokeTransactionCommand(
          id="id1",
          transactionId = 1
        )
      )
      .expectEvents(
        TransactionRevokedEvent(
          id ="id1",
          transactionId = 2,
          revokedTransactionId = 1,
          amount = 22,
          remainingAmount = 47
        )
      )
  }

  @Test
  fun `revoke a card`() {
    aggregateTestFixture<GiftCardAggregate>()
      .given(
        CardIssuedEvent(
          "id1",
          47
        )
      )
      .whenever(
        RevokeCardCommand(
          id="id1"
        )
      )
      .expectEvents(
        CardRevokedEvent(
          id="id1"
        )
      )
  }
}

