package de.digitalfrontiers.jax2023.rest

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class InsufficientCreditAmountException : Exception()

@ResponseStatus(HttpStatus.NOT_FOUND)
class GiftCardNotFoundException : Exception()

@ResponseStatus(HttpStatus.NOT_FOUND)
class TransactionNotFoundException: Exception()

@ResponseStatus(HttpStatus.FORBIDDEN)
class InvalidAmountException: Exception()
@ResponseStatus(HttpStatus.FORBIDDEN)
class UnknownGiftCardUpdateRequestException: Exception()
