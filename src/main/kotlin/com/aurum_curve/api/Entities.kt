package com.aurum_curve.api

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "categorias")
open class Categoria(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Long? = null,
    @Column(nullable = false) open var nombre: String = "",
    @Column(nullable = false) open var slug: String = ""
)

@Entity
@Table(name = "clientes")
open class Cliente(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Long? = null,
    @Column(name = "razon_social") open var razonSocial: String = "",
    @Column(name = "cuil") open var cuil: String? = null,
    @Column(nullable = false, unique = true) open var email: String = "",
    @Column(nullable = false) open var password: String = "demo1234",
    @Column(nullable = false) open var rol: String = "buyer"
)

@Entity
@Table(name = "productos")
open class Producto(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Long? = null,
    @Column(nullable = false) open var nombre: String = "",
    @Column(name = "slug") open var slug: String? = null,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "categoria_id") open var categoria: Categoria? = null,
    @Column(nullable = false) open var talle: String = "Único",
    @Column(nullable = false) open var color: String = "Único",
    @Column(nullable = false) open var descripcion: String = "",
    @Column(nullable = false) open var seccion: String = "featured",
    @Column(name = "precio_mayorista", nullable = false) open var precioMayorista: BigDecimal = BigDecimal.ZERO,
    @Column(name = "cantidad_minima", nullable = false) open var cantidadMinima: Int = 6,
    @Column(nullable = false) open var stock: Int = 0,
    @Column(name = "imagen_url", nullable = false) open var imagenUrl: String = "/images/products/prendas-unisex.png",
    @Column(nullable = false) open var activo: Boolean = true
)

@Entity
@Table(name = "pedidos")
open class Pedido(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Long? = null,
    @Column(name = "cliente_id", nullable = false) open var clienteId: Long = 0,
    @Column(nullable = false) open var fecha: LocalDate = LocalDate.now(),
    @Column(nullable = false) open var estado: String = "Pendiente",
    @Column(nullable = false) open var total: BigDecimal = BigDecimal.ZERO,
    @Column(name = "metodo_entrega", nullable = false) open var metodoEntrega: String = "pickup",
    @Column(name = "metodo_pago", nullable = false) open var metodoPago: String = "transfer",
    @Column(name = "direccion") open var direccion: String? = null,
    @Column(name = "notas") open var notas: String? = null,
    @OneToMany(mappedBy = "pedido", cascade = [CascadeType.ALL], orphanRemoval = true) open var detalles: MutableList<DetallePedido> = mutableListOf()
)

@Entity
@Table(name = "detalle_pedidos")
open class DetallePedido(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pedido_id", nullable = false) open var pedido: Pedido? = null,
    @Column(name = "producto_id", nullable = false) open var productoId: Long = 0,
    @Column(nullable = false) open var talle: String = "",
    @Column(name = "color") open var color: String? = null,
    @Column(nullable = false) open var cantidad: Int = 0,
    @Column(name = "precio_unitario", nullable = false) open var precioUnitario: BigDecimal = BigDecimal.ZERO
)

@Entity
@Table(name = "consultas")
open class Consulta(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Long? = null,
    @Column(nullable = false) open var nombre: String = "",
    @Column(nullable = false) open var email: String = "",
    @Column(nullable = false) open var mensaje: String = "",
    @Column(nullable = false) open var fecha: LocalDateTime = LocalDateTime.now()
)
