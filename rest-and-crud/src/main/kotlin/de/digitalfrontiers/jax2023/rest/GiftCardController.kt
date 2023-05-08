package de.digitalfrontiers.jax2023.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(path = ["/api/giftcards"])
class GiftCardController(private val service: GiftCardService) {

  @PostMapping
  fun issueGiftCard(@RequestBody input: IssueGiftCardInput) =
    service.issueCard(input.amount).asDto()

  @GetMapping("{id}")
  fun giftCard(@PathVariable id: Long) =
    service.getGiftCard(id).asDto()

  @PostMapping("{id}/transactions")
  fun redeem(@PathVariable id: Long, @RequestBody input: RedeemInput) =
    service.redeemAmount(id, input.amount).asDto()

  @GetMapping("{id}/transactions")
  fun transactions(@PathVariable id: Long) =
    service.getTransactions(id).map { it.asDto() }

  @PutMapping("{id}")
  fun updateGiftCard(
    @PathVariable id: Long, 
    @RequestBody input: GiftCardUpdateInput): GiftCardDto {

    return if (input.amount != null)
      service.increaseAmountTo(id, input.amount).asDto()
    else if (input.revoked == false)
      service.revokeCard(id).asDto()
    else
    // intention unclear
      throw UnknownGiftCardUpdateRequestException()
  }
}

data class IssueGiftCardInput(val amount: Int)

data class RedeemInput(val amount: Int)

data class GiftCardUpdateInput(
  val amount: Int?,
  val revoked: Boolean?
)

data class GiftCardDto(
  var id: Long,
  var amount: Int?
)

data class GiftCardTransactionDto(
  var id: Long,
  var amount: Int,
  var description: String
)

private fun GiftCard.asDto(): GiftCardDto = GiftCardDto(id = this.id!!, amount = this.amount)
private fun GiftCardTransaction.asDto() = GiftCardTransactionDto(id = id!!, amount = amount, description = description)
