package pe.edu.empresa.farmalerta.service;

import java.util.List;

import pe.edu.empresa.farmalerta.model.ConfiguracionEmpresa;
import pe.edu.empresa.farmalerta.model.Empresa;
import pe.edu.empresa.farmalerta.model.Sucursal;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface EmpresaService {

    List<Empresa> listarTodas();

    Empresa obtenerPorId(Long id);

    Empresa registrar(Usuario actual, Empresa empresa, Sucursal sucursal, Usuario gerente);

    Sucursal agregarSucursal(Usuario actual, Long empresaId, Sucursal sucursal);

    List<Sucursal> listarSucursales(Long empresaId);

    Sucursal obtenerSucursal(Long id);

    ConfiguracionEmpresa obtenerConfiguracion(Long empresaId);

    ConfiguracionEmpresa actualizarConfiguracion(Usuario actual, ConfiguracionEmpresa datos);
}
