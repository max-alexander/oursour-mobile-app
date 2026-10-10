package cl.oursour.pedido.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usuario_id", nullable = false)
	private Long usuarioId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoPedido estado = EstadoPedido.SOLICITADO;

	@Column(nullable = false)
	private Integer subtotal;

	@Column(name = "costo_despacho", nullable = false)
	private Integer costoDespacho;

	@Column(nullable = false)
	private Integer total;

	@Embedded
	private DireccionDespacho direccion;

	@Column(name = "codigo_pago", nullable = false, length = 40)
	private String codigoPago;

	@Column(nullable = false)
	private LocalDateTime fecha = LocalDateTime.now();

	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id")
	private List<PedidoItem> items = new ArrayList<>();

	public void agregarItem(Long productoId, String nombreProducto, int precioUnitario, int cantidad) {
		PedidoItem item = new PedidoItem();
		item.setPedido(this);
		item.setProductoId(productoId);
		item.setNombreProducto(nombreProducto);
		item.setPrecioUnitario(precioUnitario);
		item.setCantidad(cantidad);
		item.setSubtotal(precioUnitario * cantidad);
		items.add(item);
	}

}
