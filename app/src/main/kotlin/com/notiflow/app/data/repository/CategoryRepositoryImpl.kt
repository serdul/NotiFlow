package com.notiflow.app.data.repository

import com.notiflow.app.data.local.dao.CategoryDao
import com.notiflow.app.data.mappers.toDomain
import com.notiflow.app.data.mappers.toEntity
import com.notiflow.app.domain.model.Category
import com.notiflow.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getById(id)?.toDomain()

    override suspend fun insertCategory(category: Category): Long =
        categoryDao.insert(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.update(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        categoryDao.delete(category.toEntity())

    override suspend fun getCategoryByName(name: String): Category? =
        categoryDao.getByName(name)?.toDomain()
}
