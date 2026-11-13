package com.aurum_curve.api

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    @Query("select distinct p from Product p left join fetch p.variants where p.active = true order by p.id")
    fun findAllActiveWithVariants(): List<Product>

    @Query("select p from Product p left join fetch p.variants where p.id = :id and p.active = true")
    fun findActiveWithVariantsById(@Param("id") id: Long): Product?
}
