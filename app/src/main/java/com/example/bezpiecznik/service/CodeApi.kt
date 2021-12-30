package com.example.bezpiecznik.service

import com.example.bezpiecznik.data.code.CodeEntity
import com.example.bezpiecznik.types.Code
import com.example.bezpiecznik.types.CodeCreateDto
import io.ktor.client.request.*
import io.ktor.http.*

class CodeApi : Api() {
    suspend fun create(dto: CodeCreateDto): Code {
        val data: Code = client.request {
            url(getUrl("code"))
            contentType(ContentType.Application.Json)
            method = HttpMethod.Post
            body = dto
        }
        database.codeDao().insert(CodeEntity(data._id))
        return data
    }

    suspend fun delete(id: String) {
        database.codeDao().delete(CodeEntity(id))
    }

    suspend fun getAll(): List<Code> {
        val data: List<Code> = client.request {
            url(getUrl("code"))
            method = HttpMethod.Get
        }
        val ownedId = database.codeDao().getAll().map { entity -> entity.id }
        return data.filter { code -> ownedId.contains(code._id) }
    }

    suspend fun getById(id: String): Code {
        val data: Code = client.request {
            url(getUrl("code/${id}"))
            method = HttpMethod.Get
        }
        return data
    }
}