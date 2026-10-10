package cl.oursour.pedido.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDespacho {

	@Column(name = "despacho_calle", nullable = false, length = 150)
	private String calle;

	@Column(name = "despacho_numero", nullable = false, length = 20)
	private String numero;

	@Column(name = "despacho_comuna", nullable = false, length = 80)
	private String comuna;

	@Column(name = "despacho_ciudad", nullable = false, length = 80)
	private String ciudad;

	@Column(name = "despacho_referencia", length = 200)
	private String referencia;

}
