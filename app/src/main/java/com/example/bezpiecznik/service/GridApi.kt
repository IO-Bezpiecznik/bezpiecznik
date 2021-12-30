package com.example.bezpiecznik.service

import com.example.bezpiecznik.data.grid.GridEntity
import com.example.bezpiecznik.types.Grid
import com.example.bezpiecznik.types.GridCreateDto
import io.ktor.client.request.*
import io.ktor.http.*

class GridApi : Api() {
    suspend fun create(dto: GridCreateDto): Grid {
        val data: Grid = client.request {
            url(getUrl("grid"))
            contentType(ContentType.Application.Json)
            method = HttpMethod.Post
            body = dto
        }
        database.gridDao().insert(GridEntity(data._id))
        return data
    }

    suspend fun delete(id: String) {
        database.gridDao().delete(GridEntity(id))
    }

    suspend fun getAll(): List<Grid> {
        val data: List<Grid> = client.request {
            url(getUrl("grid"))
            method = HttpMethod.Get
        }
        val ownedId = database.gridDao().getAll().map { entity -> entity.id }
        return data.filter { grid -> ownedId.contains(grid._id) }
    }

    suspend fun getById(id: String): Grid {
        val data: Grid = client.request {
            url(getUrl("grid/${id}"))
            method = HttpMethod.Get
        }
        return data
    }
}