package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository

class SetPassPinnedUseCase(
    private val accessRepository: AccessRepository
) {
    suspend operator fun invoke(lockId: String, isPinned: Boolean) {
        accessRepository.setPassPinned(lockId, isPinned)
    }
}
