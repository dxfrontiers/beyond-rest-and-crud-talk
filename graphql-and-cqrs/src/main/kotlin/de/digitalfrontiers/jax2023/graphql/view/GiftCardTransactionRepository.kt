package de.digitalfrontiers.jax2023.graphql.view

import de.digitalfrontiers.jax2023.graphql.domain.*
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.axonframework.eventhandling.EventHandler
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface GiftCardTransactionRepository : JpaRepository<GiftCardTransactionEntity, Long> {
  fun findByGiftCardIdOrderByTransactionId(giftCardId: String): List<GiftCardTransactionEntity>
}

@Entity
data class GiftCardTransactionEntity(
  @Id
  @GeneratedValue
  var id: Long? = null,
  var giftCardId: String,
  var transactionId: Int,
  var amount: Int,
  var remainingAmount: Int,
  @Column(length = 100)
  var description: String
)

@Component
class GiftCardTransactionRepositoryUpdater(private val repo: GiftCardTransactionRepository) {

  @EventHandler
  fun on(e: CardIssuedEvent) {
    // we create a "virtual" transaction that represents the issuing
    repo.save(
      GiftCardTransactionEntity(
        giftCardId = e.id,
        transactionId = 0,
        amount = e.amount,
        remainingAmount = e.amount,
        description = "Gift Card issued"
      )
    )
  }

  @EventHandler
  fun on(e: GiftCardTransaction) {
    repo.save(
      GiftCardTransactionEntity(
        giftCardId = e.id,
        transactionId = e.transactionId,
        amount = e.amount,
        remainingAmount = e.remainingAmount,
        description = when(e) {
          is RedeemedEvent -> "Redeemed"
          is CreditIncreasedEvent -> "Credit increased"
          is TransactionRevokedEvent -> "Transaction ${e.revokedTransactionId} revoked"
          else -> ""
        }
      )
    )
  }
}
