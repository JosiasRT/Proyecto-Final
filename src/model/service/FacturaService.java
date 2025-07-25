package model.service;

import model.dao.FacturaDAO;
import model.entity.Factura;
import java.sql.SQLException;
import java.util.List;

public class FacturaService {
    private FacturaDAO facturaDAO;

    public FacturaService() {
        this.facturaDAO = new FacturaDAO();
    }

    public void registrarFactura(Factura factura) throws SQLException {
        facturaDAO.insertFactura(factura);
    }

    public List<Factura> getAllFacturas() throws SQLException {
        return facturaDAO.getAllFacturas();
    }

    public boolean facturaExists(String facturaId) throws SQLException {
        return facturaDAO.facturaExists(facturaId);
    }
}