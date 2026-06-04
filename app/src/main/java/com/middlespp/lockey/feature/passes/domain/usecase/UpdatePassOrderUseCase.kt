package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository

class UpdatePassOrderUseCase(
    private val accessRepository: AccessRepository
) {
    suspend operator fun invoke(passes: List<AccessPass>) {
        passes.forEachIndexed { index, pass ->
            accessRepository.updatePassOrder(
                lockId = pass.lockId,
                sortOrder = index
            )
        }
    }
}
