package cl.oursour.mobile

import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.util.ReglasDespacho
import cl.oursour.mobile.util.formatearPrecio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MotorRecomendacionTest {

    @Test
    fun recetaConTodosLosIngredientesTiene100PorCiento() {
        // Mango Tonic: Our Sour Mango (3), Agua tónica (50), Hielo (60)
        val resultado = CatalogoRepository.recomendarRecetas(setOf(3, 50, 60))
        val mangoTonic = resultado.first { it.receta.titulo == "Mango Tonic" }

        assertEquals(100, mangoTonic.porcentaje)
        assertTrue(mangoTonic.faltantes.isEmpty())
    }

    @Test
    fun recetasOrdenadasDeMayorAMenorCoincidencia() {
        val resultado = CatalogoRepository.recomendarRecetas(setOf(10, 60))
        val porcentajes = resultado.map { it.porcentaje }

        assertEquals(porcentajes.sortedDescending(), porcentajes)
    }

    @Test
    fun soloDevuelveRecetasConAlMenosUnIngredienteEnComun() {
        // Licor de café (21) no se usa en ninguna receta
        val resultado = CatalogoRepository.recomendarRecetas(setOf(21))
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun despachoGratisSobreElMontoMinimo() {
        assertEquals(ReglasDespacho.COSTO_DESPACHO, ReglasDespacho.calcular(20000, esRetiroEnTienda = false))
        assertEquals(0, ReglasDespacho.calcular(50000, esRetiroEnTienda = false))
        assertEquals(0, ReglasDespacho.calcular(20000, esRetiroEnTienda = true))
    }

    @Test
    fun formateaPreciosEnPesosChilenos() {
        assertEquals("$12.990", formatearPrecio(12990))
    }
}
