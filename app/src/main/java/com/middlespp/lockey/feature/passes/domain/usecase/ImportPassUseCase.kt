package com.middlespp.lockey.feature.passes.domain.usecase

import com.middlespp.lockey.feature.passes.domain.model.ImportPassResult
import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import com.middlespp.lockey.feature.passes.domain.parse.PassLinkParser
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository

class ImportPassUseCase(
    private val parser: PassLinkParser,
    private val accessRepository: AccessRepository
) {
    suspend operator fun invoke(link: String): ImportPassResult {
        val pass = parser.parse(link) ?: return ImportPassResult.InvalidLink

        save(pass)
        return ImportPassResult.Saved(pass)
    }

    suspend fun save(pass: AccessPass) {
        accessRepository.savePass(pass)
    }
}
