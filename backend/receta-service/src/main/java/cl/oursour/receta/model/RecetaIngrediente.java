package cl.oursour.receta.model;

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
@Table(name = "receta_ingredientes")
@Getter
@Setter
@NoArgsConstructor
public class RecetaIngrediente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "receta_id")
	private Receta receta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ingrediente_id")
	private Ingrediente ingrediente;

	@Column(length = 60)
	private String cantidad;

}
