package de.digitalfrontiers.jax2023.rest

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class GiftCardService(private val repo: GiftCardRepository) {

  @Transactional
  fun issueCard(amount: Int): GiftCard {
    return GiftCard(amount = amount,).let { repo.save(it) }
  }

  @Transactional
  fun redeemAmount(id: Long, amount: Int): GiftCardTransaction {

    if (amount <= 0) throw InvalidAmountException()

    return findAndUpdate(id) {
      if (it.amount >= amount) {
        it.transactions.add(GiftCardTransaction(giftCard = it, amount = -amount, description = "Redeem"))
        it.amount -= amount
      } else
        throw InsufficientCreditAmountException()
    }.transactions.last()
  }

  private fun findAndUpdate(id: Long, callback: (giftCard: GiftCard) -> Unit): GiftCard {
    val giftCard = repo.findById(id).orElse(null)

    return giftCard?.let {
      callback(it)
      return repo.save(it)
    }
      ?: throw GiftCardNotFoundException()
  }

  @Transactional
  fun increaseAmountTo(id: Long, amount: Int): GiftCard {

    if (amount <= 0) throw InvalidAmountException()

    return findAndUpdate(id) {
      if(amount <= it.amount ) throw InvalidAmountException()
      
      it.transactions.add(GiftCardTransaction(giftCard = it, amount = amount - it.amount, description = "increased credit"))
      it.amount = amount
    }

  }
  
  @Transactional
  fun increaseAmount(id: Long, amount: Int): GiftCardTransaction {

    if (amount <= 0) throw InvalidAmountException()

    return findAndUpdate(id) {
      it.transactions.add(GiftCardTransaction(giftCard = it, amount = amount, description = "increased credit"))
      it.amount += amount
    }.transactions.last()
  }

  @Transactional
  fun revertTransaction(giftCardId: Long, transactionId: Long): GiftCardTransaction {
    return findAndUpdate(giftCardId) {
      it.transactions
        .find { transaction -> transaction.id == transactionId }
        ?.also { transaction ->
          val amount = -transaction.amount
          it.transactions += GiftCardTransaction(
            giftCard = it,
            amount = amount,
            description = "Reverting transaction ${transaction.id}"
          )
          it.amount += amount
        }
        ?: throw TransactionNotFoundException()
    }.transactions.last()
  }

  @Transactional
  fun revokeCard(id: Long): GiftCard {
    return findAndUpdate(id) {
      it.revoked
    }
  }

  fun getGiftCard(id: Long) = repo.findById(id).orElseThrow { GiftCardNotFoundException() }
  fun getTransactions(id: Long) =
    getGiftCard(id).transactions

}
