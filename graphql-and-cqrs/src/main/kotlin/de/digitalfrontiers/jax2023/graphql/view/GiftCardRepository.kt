package de.digitalfrontiers.jax2023.graphql.view

import de.digitalfrontiers.jax2023.graphql.domain.CardIssuedEvent
import de.digitalfrontiers.jax2023.graphql.domain.CardRevokedEvent
import de.digitalfrontiers.jax2023.graphql.domain.GiftCardTransaction
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.axonframework.eventhandling.EventHandler
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.lang.IllegalStateException


@Repository
interface GiftCardRepository : JpaRepository<GiftCardEntity, String>

@Entity
data class GiftCardEntity(
  @Id
  var id: String,
  var revoked: Boolean = false,
  var amount: Int = 0
)

@Component
class GiftCardRepositoryUpdater(private val repo: GiftCardRepository) {

  @EventHandler
  fun on(e: CardIssuedEvent) {
    repo.save(GiftCardEntity(id = e.id, amount = e.amount))
  }

  @EventHandler
  fun on(e: CardRevokedEvent) {
    updateEntity(e.id) {
      it.revoked = true
    }
  }

  @EventHandler
  fun on(e: GiftCardTransaction) {
    updateEntity(e.id) {
      it.amount = e.remainingAmount
    }
  }

  private fun updateEntity(
    id: String,
    callback: (giftCard: GiftCardEntity) -> Unit
  ) {
    val giftCard = repo.findById(id).orElseThrow { IllegalStateException("inconsistent state") }
    callback(giftCard)
    repo.save(giftCard)
  }
}
