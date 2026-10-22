package pe.edu.empresa.farmalerta.service;

import java.util.Map;

import pe.edu.empresa.farmalerta.model.Producto;
import pe.edu.empresa.farmalerta.model.Sucursal;


public interface PrediccionService {

    int diasHistorial(Producto producto, Sucursal sucursal);

    Double demandaEsperada(Producto producto, Sucursal sucursal);

    Map<Long, Integer> unidadesEnRiesgo(Producto producto, Sucursal sucursal, double demandaDiaria);
}
