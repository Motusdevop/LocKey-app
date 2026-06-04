package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import kotlinx.coroutines.flow.Flow

class GetPassesUseCase(
    private val accessRepository: AccessRepository
) {
    operator fun invoke(): Flow<List<AccessPass>> = accessRepository.observePasses()
}
