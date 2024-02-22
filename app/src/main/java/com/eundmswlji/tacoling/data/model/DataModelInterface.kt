package com.eundmswlji.tacoling.data.model

interface DataModelInterface<T> {
    fun toDomainModel(): T
}