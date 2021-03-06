package cz.blackchameleon.trendingprojects.framework.remote

import cz.blackchameleon.data.remote.RemoteRepositorySource
import cz.blackchameleon.domain.DateRange
import cz.blackchameleon.domain.Language
import cz.blackchameleon.domain.Repository
import cz.blackchameleon.domain.SpokenLanguage
import cz.blackchameleon.trendingprojects.framework.RepositoryApi
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Remote source implementation for getting repository object from API
 * Implementation of [RemoteRepositorySource]
 *
 * @author Karolina Klepackova on 27.11.2020.
 */
class RemoteRepositorySourceImpl(private val repositoryApi: RepositoryApi) :
    RemoteRepositorySource {
    override suspend fun fetchRepositories(
        dateRange: DateRange?,
        language: Language?,
        spokenLanguage: SpokenLanguage?
    ): Single<List<Repository>> =
        repositoryApi.getRepositories(language?.urlParam, dateRange?.dateRange, spokenLanguage?.urlParam)
            .map { list ->
                list.map {
                    it.toRepository()
                }
            }
            .subscribeOn(Schedulers.io())
}