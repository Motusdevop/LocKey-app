package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.PassDetails
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository

class GetPassUseCase(
    private val accessRepository: AccessRepository
) {
    suspend operator fun invoke(lockId: String): PassDetails? {
        val pass = accessRepository.getPass(lockId) ?: return null
        return PassDetails(pass = pass)
    }
}
