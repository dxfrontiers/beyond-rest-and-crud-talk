package de.digitalfrontiers.jax2023.rest

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Entity
data class GiftCard(
  @Id
  @GeneratedValue
  var id: Long? = null,
  @Column(nullable = false)
  var amount: Int,
  @OneToMany(mappedBy = "giftCard", cascade = [CascadeType.ALL])
  var transactions: MutableList<GiftCardTransaction> = mutableListOf(),
  var revoked: Boolean = false
)

@Entity
data class GiftCardTransaction(
  @Id
  @GeneratedValue
  var id: Long? = null,
  @ManyToOne(cascade = [CascadeType.ALL])
  var giftCard: GiftCard,
  @Column(nullable = false)
  var amount: Int,
  @Column(nullable = false)
  var description: String
)

@Repository
interface GiftCardRepository : JpaRepository<GiftCard, Long> {

}
