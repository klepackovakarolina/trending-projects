package cz.blackchameleon.trendingprojects

import cz.blackchameleon.data.Result
import cz.blackchameleon.data.local.LocalRepositorySource
import cz.blackchameleon.data.remote.RemoteRepositorySource
import cz.blackchameleon.data.repository.RepositoryRepository
import cz.blackchameleon.domain.Contributor
import cz.blackchameleon.domain.Repository
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Test class for repository repository logic
 *
 * @author Karolina Klepackova on 27.11.2020.
 */
@RunWith(MockitoJUnitRunner::class)
class RepositoryRepositoryTest {
    @Mock
    lateinit var localRepositorySource: LocalRepositorySource

    @Mock
    lateinit var remoteRepositorySource: RemoteRepositorySource

    private lateinit var repositoryRepository: RepositoryRepository

    private val firstRepository = Repository(
        author = "firstAuthor",
        name = "firstName",
        avatar = "firstAvatar",
        url = "firstUrl",
        description = "firstDescription",
        language = "firstLanguage",
        languageColor = "firstLanguageColor",
        stars = "firstStars",
        forks = "firstForks",
        currentPeriodStars = "firstCurrentPeriodStars",
        builtBy = listOf(
            Contributor(
                "firstUsername", "firstHref", "firstAvatar"
            )
        )
    )

    private val secondRepository = Repository(
        author = "secondAuthor",
        name = "secondName",
        avatar = "secondAvatar",
        url = "secondUrl",
        description = "secondDescription",
        language = "secondLanguage",
        languageColor = "secondLanguageColor",
        stars = "secondStars",
        forks = "secondForks",
        currentPeriodStars = "secondCurrentPeriodStars",
        builtBy = listOf(
            Contributor(
                "secondUsername", "secondHref", "secondAvatar"
            )
        )
    )

    @Before
    fun setup() {
        repositoryRepository = RepositoryRepository(localRepositorySource, remoteRepositorySource)
    }

    @Test
    fun `pass when local source is cleaned`() {
        runBlocking {
            repositoryRepository.clean()
            verify(localRepositorySource, times(1)).clean()
        }
    }

    @Test
    fun `pass when repository is saved`() {
        runBlocking {
            repositoryRepository.saveRepository(firstRepository)
            verify(localRepositorySource, times(1)).saveRepository(firstRepository)
        }
    }

    @Test
    fun `pass when repositories are saved`() {
        runBlocking {
            repositoryRepository.saveRepositories(listOf(firstRepository, firstRepository))
            verify(localRepositorySource, times(2)).saveRepository(firstRepository)
        }
    }

    @Test
    fun `pass when repositories are loaded from database`() {
        runBlocking {
            `when`(localRepositorySource.getRepositories()).thenReturn(
                listOf(
                    firstRepository,
                    secondRepository
                )
            )

            val repositories = repositoryRepository.getRepositories(false)
            assert(
                repositories is Result.Success && repositories.data == listOf(
                    firstRepository,
                    secondRepository
                )
            )
        }
    }

    @Test
    fun `pass when repositories are loaded from api`() {
        runBlocking {
            doReturn(Single.just(listOf(firstRepository, secondRepository)))
                .`when`(remoteRepositorySource)
                .fetchRepositories()

            val repositories = repositoryRepository.getRepositories(true)
            assert(
                repositories is Result.Success && repositories.data == listOf(
                    firstRepository,
                    secondRepository
                )
            )
        }
    }

    @Test
    fun `pass when repositories loading throws exception`() {
        val exception = mock(RuntimeException::class.java)

        runBlocking {
            doThrow(exception)
                .`when`(remoteRepositorySource)
                .fetchRepositories()

            val repositories = repositoryRepository.getRepositories(true)
            assert(repositories is Result.Error)
        }
    }
}