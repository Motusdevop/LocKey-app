package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository

class DeletePassUseCase(
    private val accessRepository: AccessRepository
) {
    suspend operator fun invoke(lockId: String) {
        accessRepository.deletePass(lockId)
    }
}
