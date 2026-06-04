package com.middlespp.lockey.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.middlespp.lockey.core.database.AppDatabase
import com.middlespp.lockey.core.database.DatabaseConfig
import com.middlespp.lockey.core.navigation.Navigator
import com.middlespp.lockey.feature.passes.data.local.PassDao
import com.middlespp.lockey.feature.passes.data.local.PassStore
import com.middlespp.lockey.feature.passes.data.local.RoomPassStore
import com.middlespp.lockey.feature.passes.data.remote.AccessApi
import com.middlespp.lockey.feature.passes.data.repository.DefaultAccessRepository
import com.middlespp.lockey.feature.passes.domain.parse.PassLinkParser
import com.middlespp.lockey.feature.passes.domain.repository.AccessRepository
import com.middlespp.lockey.feature.passes.domain.usecase.DeletePassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassesUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.ImportPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.OpenLockUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.SetPassPinnedUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.UpdatePassOrderUseCase
import com.middlespp.lockey.feature.scanner.domain.parse.LockQrParser
import com.middlespp.lockey.feature.scanner.domain.usecase.CheckLockCodeUseCase
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import androidx.sqlite.db.SupportSQLiteDatabase

@DependencyGraph
interface AppGraph {
    val navigator: Navigator
    val accessRepository: AccessRepository
    val getPassUseCase: GetPassUseCase
    val getPassesUseCase: GetPassesUseCase
    val importPassUseCase: ImportPassUseCase
    val deletePassUseCase: DeletePassUseCase
    val setPassPinnedUseCase: SetPassPinnedUseCase
    val updatePassOrderUseCase: UpdatePassOrderUseCase
    val lockQrParser: LockQrParser
    val openLockUseCase: OpenLockUseCase

    @Provides
    fun provideNavigator(): Navigator = Navigator()

    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    fun provideHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    @Provides
    fun provideDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DatabaseConfig.NAME
    ).addMigrations(
        Migration1To4,
        Migration2To4,
        Migration3To4
    ).build()

    @Provides
    fun providePassDao(database: AppDatabase): PassDao = database.passDao()

    @Provides
    fun providePassStore(dao: PassDao): PassStore = RoomPassStore(dao)

    @Provides
    fun provideAccessApi(httpClient: HttpClient): AccessApi = AccessApi(httpClient)

    @Provides
    fun provideAccessRepository(
        api: AccessApi,
        passStore: PassStore
    ): AccessRepository = DefaultAccessRepository(
        api = api,
        passStore = passStore
    )

    @Provides
    fun provideCheckLockCodeUseCase(): CheckLockCodeUseCase = CheckLockCodeUseCase()

    @Provides
    fun providePassLinkParser(): PassLinkParser = PassLinkParser()

    @Provides
    fun provideLockQrParser(): LockQrParser = LockQrParser()

    @Provides
    fun provideGetPassUseCase(accessRepository: AccessRepository): GetPassUseCase =
        GetPassUseCase(accessRepository)

    @Provides
    fun provideGetPassesUseCase(accessRepository: AccessRepository): GetPassesUseCase =
        GetPassesUseCase(accessRepository)

    @Provides
    fun provideDeletePassUseCase(accessRepository: AccessRepository): DeletePassUseCase =
        DeletePassUseCase(accessRepository)

    @Provides
    fun provideSetPassPinnedUseCase(accessRepository: AccessRepository): SetPassPinnedUseCase =
        SetPassPinnedUseCase(accessRepository)

    @Provides
    fun provideUpdatePassOrderUseCase(accessRepository: AccessRepository): UpdatePassOrderUseCase =
        UpdatePassOrderUseCase(accessRepository)

    @Provides
    fun provideImportPassUseCase(
        parser: PassLinkParser,
        accessRepository: AccessRepository
    ): ImportPassUseCase = ImportPassUseCase(
        parser = parser,
        accessRepository = accessRepository
    )

    @Provides
    fun provideOpenLockUseCase(
        checkLockCodeUseCase: CheckLockCodeUseCase,
        accessRepository: AccessRepository
    ): OpenLockUseCase = OpenLockUseCase(
        checkLockCode = checkLockCodeUseCase,
        accessRepository = accessRepository
    )

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): AppGraph
    }

}

private object Migration1To4 : Migration(1, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        addPassOrderColumns(db)
    }
}

private object Migration2To4 : Migration(2, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        addPassOrderColumns(db)
    }
}

private object Migration3To4 : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        addPassOrderColumns(db)
    }
}

private fun addPassOrderColumns(db: SupportSQLiteDatabase) {
    db.execSQL("ALTER TABLE passes ADD COLUMN is_pinned INTEGER NOT NULL DEFAULT 0")
    db.execSQL("ALTER TABLE passes ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0")
}

fun createAppGraph(context: Context): AppGraph = createGraphFactory<AppGraph.Factory>().create(context)
