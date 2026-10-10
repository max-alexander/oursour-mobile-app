package cl.oursour.receta.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recetas")
@Getter
@Setter
@NoArgsConstructor
public class Receta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String titulo;

	@Column(length = 500)
	private String descripcion;

	@Column(name = "sin_alcohol", nullable = false)
	private Boolean sinAlcohol = false;

	@Column(name = "producto_id")
	private Long productoId;

	@OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id")
	private List<RecetaIngrediente> ingredientes = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "receta_pasos", joinColumns = @JoinColumn(name = "receta_id"))
	@OrderColumn(name = "orden")
	@Column(name = "descripcion", nullable = false, length = 500)
	private List<String> pasos = new ArrayList<>();

	public void agregarIngrediente(Ingrediente ingrediente, String cantidad) {
		RecetaIngrediente item = new RecetaIngrediente();
		item.setReceta(this);
		item.setIngrediente(ingrediente);
		item.setCantidad(cantidad);
		ingredientes.add(item);
	}

}
