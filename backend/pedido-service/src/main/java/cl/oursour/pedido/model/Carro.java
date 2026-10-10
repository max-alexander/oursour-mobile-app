package cl.oursour.pedido.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "carros")
@Getter
@Setter
@NoArgsConstructor
public class Carro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usuario_id", nullable = false, unique = true)
	private Long usuarioId;

	@Column(name = "fecha_actualizacion", nullable = false)
	private LocalDateTime fechaActualizacion = LocalDateTime.now();

	@OneToMany(mappedBy = "carro", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id")
	private List<CarroItem> items = new ArrayList<>();

	public Carro(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Optional<CarroItem> buscarItem(Long productoId) {
		return items.stream().filter(item -> item.getProductoId().equals(productoId)).findFirst();
	}

	public int subtotal() {
		return items.stream().mapToInt(CarroItem::subtotal).sum();
	}

	public void tocar() {
		fechaActualizacion = LocalDateTime.now();
	}

}
