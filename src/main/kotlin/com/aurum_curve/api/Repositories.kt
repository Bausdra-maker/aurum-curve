package com.aurum_curve.api

import org.springframework.data.jpa.repository.JpaRepository

interface CategoriaRepository : JpaRepository<Categoria, Long>
interface ClienteRepository : JpaRepository<Cliente, Long> {
    fun findByEmailIgnoreCase(email: String): Cliente?
}
interface ProductoRepository : JpaRepository<Producto, Long> {
    fun findAllByActivoTrueOrderByIdAsc(): List<Producto>
}
interface PedidoRepository : JpaRepository<Pedido, Long> {
    fun findAllByClienteIdOrderByIdDesc(clienteId: Long): List<Pedido>
}
interface ConsultaRepository : JpaRepository<Consulta, Long>
