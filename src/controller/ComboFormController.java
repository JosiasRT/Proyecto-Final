// En controller/ComboFormController.java (reemplazo casi total)
package controller;

import model.entity.Componente;
import model.entity.Combo;
import model.entity.ComboDetalle;
import model.service.ComboService;
import model.service.ComponenteService;
import view.ComboFormView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComboFormController implements ActionListener {
    private final ComboFormView formView;
    private final ComboService comboService;
    private final ComponenteService componenteService;
    private final int comboIdToEdit;
    private final controller.ComboController comboListController;

    public ComboFormController(ComboFormView formView, ComboService comboService, ComponenteService componenteService, controller.ComboController comboListController, int comboIdToEdit) {
        this.formView = formView;
        this.comboService = comboService;
        this.componenteService = componenteService;
        this.comboIdToEdit = comboIdToEdit;
        this.comboListController = comboListController;
        this.formView.getBtnGuardar().addActionListener(this);
        this.formView.getBtnCancelar().addActionListener(this);
        // Filtrado dinámico: escuchar cambios en Tarjeta Madre
        this.formView.getCmbTarjetaMadre().addActionListener(e -> filtrarComponentesPorTarjetaMadre());
        loadAllComponentsAsync();
    }

    public ComboFormController(ComboFormView formView, ComboService comboService, ComponenteService componenteService, controller.ComboController comboListController) {
        this(formView, comboService, componenteService, comboListController, 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == formView.getBtnGuardar()) {
            saveOrUpdateCombo();
        } else if (e.getSource() == formView.getBtnCancelar()) {
            formView.dispose();
        }
    }

    private List<Componente> allMicroprocesadores = null;
    private List<Componente> allMemoriasRAM = null;
    private List<Componente> allDiscosDuros = null;

    private void filtrarComponentesPorTarjetaMadre() {
        try {
            Componente selected = (Componente) formView.getCmbTarjetaMadre().getSelectedItem();
            if (selected instanceof model.entity.TarjetaMadre) {
                model.entity.TarjetaMadre tm = (model.entity.TarjetaMadre) selected;
                // Filtrar microprocesadores por socket
                List<Componente> compatiblesMP = new java.util.ArrayList<>();
                for (Componente c : allMicroprocesadores) {
                    if (c instanceof model.entity.Microprocesador && tm.getSocketMicro().equals(((model.entity.Microprocesador) c).getSocket())) {
                        compatiblesMP.add(c);
                    }
                }
                formView.populateComboBox(compatiblesMP, "Microprocesador");
                // Filtrar memorias RAM por tipo
                List<Componente> compatiblesRAM = new java.util.ArrayList<>();
                for (Componente c : allMemoriasRAM) {
                    if (c instanceof model.entity.MemoriaRAM && tm.getTipoMemoriaRam().equals(((model.entity.MemoriaRAM) c).getTipoMemoria())) {
                        compatiblesRAM.add(c);
                    }
                }
                formView.populateComboBox(compatiblesRAM, "MemoriaRAM");
                // Filtrar discos duros por conexiones soportadas
                List<Componente> compatiblesDD = new java.util.ArrayList<>();
                for (Componente c : allDiscosDuros) {
                    if (c instanceof model.entity.DiscoDuro && tm.getConexionesDisco() != null && tm.getConexionesDisco().contains(((model.entity.DiscoDuro) c).getTipoConexion())) {
                        compatiblesDD.add(c);
                    }
                }
                formView.populateComboBox(compatiblesDD, "DiscoDuro");
            } else {
                // Si no hay TM seleccionada, restaurar todos los componentes
                formView.populateComboBox(allMicroprocesadores, "Microprocesador");
                formView.populateComboBox(allMemoriasRAM, "MemoriaRAM");
                formView.populateComboBox(allDiscosDuros, "DiscoDuro");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            formView.showMessage("Error al filtrar componentes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllComponentsAsync() {
        formView.showLoadingIndicator();
        new SwingWorker<Map<String, List<Componente>>, Void>() {
            @Override
            protected Map<String, List<Componente>> doInBackground() throws Exception {
                Map<String, List<Componente>> allComponents = new java.util.HashMap<>();
                allComponents.put("TarjetaMadre", componenteService.getComponentesByType("TarjetaMadre"));
                allComponents.put("Microprocesador", componenteService.getComponentesByType("Microprocesador"));
                allComponents.put("MemoriaRAM", componenteService.getComponentesByType("MemoriaRAM"));
                allComponents.put("DiscoDuro", componenteService.getComponentesByType("DiscoDuro"));
                return allComponents;
            }
            @Override
            protected void done() {
                try {
                    Map<String, List<Componente>> result = get();
                    // Guardar todas las listas originales para el filtrado dinámico
                    allMicroprocesadores = result.get("Microprocesador");
                    allMemoriasRAM = result.get("MemoriaRAM");
                    allDiscosDuros = result.get("DiscoDuro");
                    formView.populateComboBox(result.get("TarjetaMadre"), "TarjetaMadre");
                    formView.populateComboBox(allMicroprocesadores, "Microprocesador");
                    formView.populateComboBox(allMemoriasRAM, "MemoriaRAM");
                    formView.populateComboBox(allDiscosDuros, "DiscoDuro");
                    if (comboIdToEdit != 0) {
                        loadComboForEdit(comboIdToEdit);
                    } else {
                        formView.clearForm();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    formView.showMessage("Error al cargar componentes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    formView.dispose();
                } finally {
                    formView.hideLoadingIndicator();
                }
            }
        }.execute();
    }

    private void loadComboForEdit(int id) {
        new SwingWorker<Combo, Void>() {
            @Override
            protected Combo doInBackground() throws Exception {
                return comboService.getComboById(id);
            }
            @Override
            protected void done() {
                try {
                    Combo combo = get();
                    if (combo != null) {
                        formView.setComboData(combo.getDescuentoPorciento(), combo.getDetalles());
                    } else {
                        formView.showMessage("Combo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                        formView.dispose();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    formView.showMessage("Error al cargar el combo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    formView.dispose();
                }
            }
        }.execute();
    }

    private void saveOrUpdateCombo() {
        try {
            String descuentoText = formView.getTxtDescuentoPorciento().getText().trim();
            if (descuentoText.isEmpty()) {
                formView.showMessage("Debe ingresar un descuento (puede ser 0).", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BigDecimal descuento;
            try {
                descuento = new BigDecimal(descuentoText);
            } catch (NumberFormatException ex) {
                formView.showMessage("Descuento inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<ComboDetalle> detalles = new ArrayList<>();
            Map<String, JComboBox<Componente>> combos = formView.getComponentComboBoxes();
            for (String tipo : combos.keySet()) {
                Componente comp = (Componente) combos.get(tipo).getSelectedItem();
                int cantidad = 1; // Siempre 1 para cada componente
                if (comp == null) {
                    formView.showMessage("Debe seleccionar un componente de cada tipo.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                ComboDetalle detalle = new ComboDetalle();
                detalle.setComponente(comp);
                detalle.setComponenteID(comp.getComponenteID());
                detalle.setCantidad(cantidad);
                detalles.add(detalle);
            }
            if (comboIdToEdit == 0) {
                comboService.crearCombo(descuento, detalles);
                formView.showMessage("Combo creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                comboService.actualizarCombo(comboIdToEdit, descuento, detalles);
                formView.showMessage("Combo actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            if (comboListController != null) {
                comboListController.refreshTable();
            }
            formView.dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            formView.showMessage("Error al guardar el combo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}