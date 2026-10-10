package cl.oursour.pedido.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import cl.oursour.pedido.exception.RecursoNoEncontradoException;
import cl.oursour.pedido.exception.ReglaNegocioException;
import cl.oursour.pedido.exception.ServicioNoDisponibleException;

@Component
public class CatalogoClient {

	private final RestClient restClient;

	public CatalogoClient(@LoadBalanced RestClient.Builder builder,
			@Value("${catalogo.url:http://catalogo-service}") String url) {
		this.restClient = builder.baseUrl(url).build();
	}

	public ProductoCatalogo obtenerProducto(Long productoId) {
		try {
			return restClient.get()
					.uri("/api/catalogo/productos/{id}", productoId)
					.retrieve()
					.body(ProductoCatalogo.class);
		}
		catch (HttpClientErrorException.NotFound ex) {
			throw new RecursoNoEncontradoException("Producto " + productoId + " no existe en el catálogo");
		}
		catch (ResourceAccessException | HttpServerErrorException | IllegalStateException ex) {
			throw new ServicioNoDisponibleException("El catálogo no está disponible en este momento");
		}
	}

	public void descontarStock(Long productoId, int cantidad) {
		try {
			restClient.post()
					.uri("/api/catalogo/productos/{id}/descontar-stock", productoId)
					.body(Map.of("cantidad", cantidad))
					.retrieve()
					.toBodilessEntity();
		}
		catch (HttpClientErrorException.NotFound ex) {
			throw new RecursoNoEncontradoException("Producto " + productoId + " no existe en el catálogo");
		}
		catch (HttpClientErrorException.Conflict ex) {
			throw new ReglaNegocioException("Stock insuficiente para el producto " + productoId);
		}
		catch (ResourceAccessException | HttpServerErrorException | IllegalStateException ex) {
			throw new ServicioNoDisponibleException("El catálogo no está disponible en este momento");
		}
	}

}
