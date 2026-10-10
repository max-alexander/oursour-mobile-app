package cl.oursour.pedido.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido_items")
@Getter
@Setter
@NoArgsConstructor
public class PedidoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;

	@Column(name = "producto_id", nullable = false)
	private Long productoId;

	@Column(name = "nombre_producto", nullable = false, length = 120)
	private String nombreProducto;

	@Column(name = "precio_unitario", nullable = false)
	private Integer precioUnitario;

	@Column(nullable = false)
	private Integer cantidad;

	@Column(nullable = false)
	private Integer subtotal;

}
