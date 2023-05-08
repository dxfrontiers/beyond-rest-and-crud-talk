package de.digitalfrontiers.jax2023.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class GiftCardServiceTest {

  @Autowired
  lateinit var repo: GiftCardRepository

  @Test
  fun `issue a new gift card`() {
    val service = GiftCardService(repo)

    val card = service.issueCard(4711)

    assertNotNull(card)
    assertEquals(4711, card.amount)
  }

  @Test
  fun `redeem the full amount`() {
    val service = GiftCardService(repo)

    val cardId = service.issueCard(4711).id!!

    val transaction = service.redeemAmount(cardId, 4711)

    val card = repo.findById(cardId).orElseThrow()

    assertEquals(-4711, transaction.amount)
    assertEquals(0, card.amount)

  }

  @Test
  fun `trying to redeem a higher amount then fails`() {
    val service = GiftCardService(repo)

    val cardId = service.issueCard(20).id!!

    assertThrows(InsufficientCreditAmountException::class.java) {
      service.redeemAmount(cardId, 21)
    }
  }

  @Test
  fun `partial redeem results in updated amount`() {

    val service = GiftCardService(repo)

    val cardId = service.issueCard(20).id!!

    val transaction = service.redeemAmount(cardId, 8)

    assertEquals(12, transaction.giftCard.amount)
  }

  @Test
  fun `reverting a previous redeem transaction`() {

    val service = GiftCardService(repo)

    val cardId = service.issueCard(20).id!!

    val transactionId = service.redeemAmount(cardId, 17).id!!

    val revertTransaction = service.revertTransaction(cardId, transactionId)

    assertEquals(17, revertTransaction.amount)

    val giftCard = service.getGiftCard(cardId)

    assertEquals(20, giftCard.amount)
  }

  @Test
  fun `increase the credit amount`() {

    val service = GiftCardService(repo)
    val cardId = service.issueCard(20).id!!

    val transaction = service.increaseAmount(cardId, 55)
    val card = service.getGiftCard(cardId)

    assertEquals(75, card.amount)
    assertEquals(55, transaction.amount)
  }

  @Test
  fun `revoke increase credit amount transaction`() {

    val service = GiftCardService(repo)
    val cardId = service.issueCard(20).id!!

    val transactionId = service.increaseAmount(cardId, 55).id!!
    assertEquals(75, service.getGiftCard(cardId).amount)

    val transaction = service.revertTransaction(cardId, transactionId)
    assertEquals(-55, transaction.amount)
    Assertions.assertThat(transaction.description).containsIgnoringCase("revert")

    assertEquals(20, service.getGiftCard(cardId).amount)
  }

  @Test
  fun `revoke a gift card`() {

    val service = GiftCardService(repo)
    val cardId = service.issueCard(88).id!!

    val card = service.revokeCard(cardId)

    assertTrue(card.revoked)

  }

  @Test
  fun `increasing the amount to a specific value`() {

    val service = GiftCardService(repo)
    val cardId = service.issueCard(20).id!!
    
    val giftCard = service.increaseAmountTo(cardId, 30)
    assertEquals(30, giftCard.amount)
    val transaction = giftCard.transactions.last()
    assertEquals(10, transaction.amount)
  }

  @Test
  fun `redeeming a revoked card fails`() {
    val service = GiftCardService(repo)
    val cardId = service.issueCard(88).id!!
    
  }
}
