package cz.blackchameleon.trendingprojects.framework.database.dao

import androidx.room.Dao
import androidx.room.Query
import cz.blackchameleon.trendingprojects.framework.database.db.RepositoryDb

/**
 * DB operations for repositories
 * Completes CRUD functions together with [BaseDao] [https://www.codecademy.com/articles/what-is-crud]
 * @see BaseDao
 *
 * @author Karolina Klepackova on 27.11.2020.
 */
@Dao
interface RepositoryDao : BaseDao<RepositoryDb> {
    @Query("SELECT * FROM repositories")
    suspend fun getRepositories(): List<RepositoryDb>

    @Query("SELECT * FROM repositories WHERE url=:url ")
    suspend fun getRepository(url: String): RepositoryDb

    @Query("DELETE FROM repositories")
    suspend fun deleteAll()
}