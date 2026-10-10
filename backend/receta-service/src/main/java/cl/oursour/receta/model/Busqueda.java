package cl.oursour.receta.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "busquedas")
@Getter
@Setter
@NoArgsConstructor
public class Busqueda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime fecha = LocalDateTime.now();

	@Column(name = "resultados", nullable = false)
	private Integer resultados = 0;

	@ManyToMany
	@JoinTable(name = "busqueda_ingredientes",
			joinColumns = @JoinColumn(name = "busqueda_id"),
			inverseJoinColumns = @JoinColumn(name = "ingrediente_id"))
	private Set<Ingrediente> ingredientes = new HashSet<>();

}
