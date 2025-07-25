package controller;

import view.VentaView;
import model.entity.Cliente;
import model.entity.Combo;
import model.entity.Componente;
import model.entity.Factura;
import model.service.ClienteService;
import model.service.ComboService;
import model.service.ComponenteService;
import model.service.FacturaService;
import model.service.TransactionService;
import model.dao.ProductoDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import java.sql.Date;
import java.util.ArrayList;
import java.math.BigDecimal;
import util.InvoiceIdGenerator;

public class VentaController implements ActionListener {
    private VentaView ventaView;
    private ClienteService clienteService;
    private ComboService comboService;
    private ComponenteService componenteService;
    private HomeController homeController;
    private FacturaService facturaService = new FacturaService();
    private ProductoDAO productoDAO = new ProductoDAO();
    private TransactionService transactionService = new TransactionService();
    private List<Cliente> clientesCache;
    private List<Combo> combosCache;
    private List<Componente> componentesCache;

    public VentaController(VentaView ventaView, HomeController homeController) {
        this.ventaView = ventaView;
        this.homeController = homeController;
        this.clienteService = new ClienteService();
        this.comboService = new ComboService();
        this.componenteService = new ComponenteService();
        this.facturaService = new FacturaService();
        cargarClientes();
        cargarCombos();
        cargarComponentes();
        ventaView.addRegistrarVentaListener(this);
        ventaView.addVolverButtonListener(this);
        ventaView.addNuevoClienteListener(this);
        ventaView.addAgregarComponenteListener(this);
        ventaView.getComboComboBox().addActionListener(e -> actualizarTotal());
        ventaView.getSeleccionadosList().addListSelectionListener(e -> actualizarTotal());
    }

    private void cargarClientes() {
        try {
            clientesCache = clienteService.getAllClientes();

            if (clientesCache == null) {
                throw new RuntimeException("El servicio de clientes devolvió null");
            }

            ventaView.getClienteComboBox().removeAllItems();

            if (clientesCache.isEmpty()) {
                ventaView.getClienteComboBox().addItem("No hay clientes disponibles");
                JOptionPane.showMessageDialog(ventaView,
                        "No se encontraron clientes en el sistema.\nPor favor agregue al menos un cliente antes de realizar ventas.",
                        "Sin Clientes", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Cliente c : clientesCache) {
                if (c == null) {
                    System.err.println("WARNING: Cliente null encontrado en la lista");
                    continue;
                }

                String nombre = c.getNombre();
                String apellido = c.getApellido();

                // Validate client data
                if (nombre == null || nombre.trim().isEmpty()) {
                    nombre = "Sin nombre";
                }
                if (apellido == null || apellido.trim().isEmpty()) {
                    apellido = "Sin apellido";
                }

                ventaView.getClienteComboBox().addItem(nombre + " " + apellido);
            }

        } catch (Exception ex) {
            handleDataLoadError("clientes", ex);
            // Add fallback option
            ventaView.getClienteComboBox().removeAllItems();
            ventaView.getClienteComboBox().addItem("Error al cargar clientes");
        }
    }

    private void cargarCombos() {
        try {
            combosCache = comboService.getAllCombos();

            if (combosCache == null) {
                throw new RuntimeException("El servicio de combos devolvió null");
            }

            ventaView.getComboComboBox().removeAllItems();
            ventaView.getComboComboBox().addItem("Ninguno");

            if (combosCache.isEmpty()) {
                System.out.println("INFO: No hay combos disponibles en el sistema");
                return;
            }

            for (Combo combo : combosCache) {
                if (combo == null) {
                    System.err.println("WARNING: Combo null encontrado en la lista");
                    continue;
                }

                try {
                    // Validate combo data
                    if (combo.getDetalles() == null || combo.getDetalles().isEmpty()) {
                        System.err.println("WARNING: Combo ID " + combo.getComboID() + " no tiene detalles válidos");
                        continue;
                    }

                    // Calculate combo price with validation
                    BigDecimal precioOriginal = BigDecimal.ZERO;
                    boolean hasValidComponents = false;

                    for (var detalle : combo.getDetalles()) {
                        if (detalle == null) {
                            System.err.println("WARNING: Detalle null en combo ID " + combo.getComboID());
                            continue;
                        }

                        if (detalle.getComponente() != null && detalle.getComponente().getPrecio() != null) {
                            BigDecimal componentPrice = detalle.getComponente().getPrecio()
                                    .multiply(new BigDecimal(detalle.getCantidad()));
                            precioOriginal = precioOriginal.add(componentPrice);
                            hasValidComponents = true;
                        }
                    }

                    if (!hasValidComponents) {
                        System.err.println("WARNING: Combo ID " + combo.getComboID()
                                + " no tiene componentes válidos con precios");
                        continue;
                    }

                    BigDecimal precioFinal = combo.calculateDiscountedPrice(precioOriginal);

                    if (precioFinal == null || precioFinal.compareTo(BigDecimal.ZERO) < 0) {
                        System.err.println("WARNING: Combo ID " + combo.getComboID() + " tiene precio inválido");
                        continue;
                    }

                    // Add null check for combo name with fallback display logic
                    String comboName = combo.getNombre();
                    if (comboName == null || comboName.trim().isEmpty()) {
                        comboName = "Combo #" + combo.getComboID();
                        System.out.println("WARNING: Combo with ID " + combo.getComboID()
                                + " has null or empty name. Using fallback name: " + comboName);
                    }

                    ventaView.getComboComboBox()
                            .addItem(comboName + " ($" + precioFinal.setScale(2, BigDecimal.ROUND_HALF_UP) + ")");

                } catch (Exception comboEx) {
                    System.err.println(
                            "ERROR: Failed to process combo ID " + combo.getComboID() + ": " + comboEx.getMessage());
                    // Continue with next combo instead of failing completely
                }
            }

        } catch (Exception ex) {
            handleDataLoadError("combos", ex);
            // Ensure "Ninguno" option is still available
            ventaView.getComboComboBox().removeAllItems();
            ventaView.getComboComboBox().addItem("Ninguno");
            ventaView.getComboComboBox().addItem("Error al cargar combos");
        }
    }

    private void cargarComponentes() {
        try {
            componentesCache = componenteService.getAllComponentes();

            if (componentesCache == null) {
                throw new RuntimeException("El servicio de componentes devolvió null");
            }

            DefaultListModel<String> model = new DefaultListModel<>();

            if (componentesCache.isEmpty()) {
                model.addElement("No hay componentes disponibles");
                ventaView.getComponentesList().setModel(model);
                JOptionPane.showMessageDialog(ventaView,
                        "No se encontraron componentes en el sistema.\nNo se podrán realizar ventas individuales.",
                        "Sin Componentes", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Componente c : componentesCache) {
                if (c == null) {
                    System.err.println("WARNING: Componente null encontrado en la lista");
                    continue;
                }

                try {
                    // Validate component data
                    if (c.getTipoComponente() == null || c.getTipoComponente().trim().isEmpty()) {
                        System.err
                                .println("WARNING: Componente ID " + c.getComponenteID() + " tiene tipo null o vacío");
                        continue;
                    }

                    if (c.getNumeroSerie() == null || c.getNumeroSerie().trim().isEmpty()) {
                        System.err.println("WARNING: Componente ID " + c.getComponenteID()
                                + " tiene número de serie null o vacío");
                        continue;
                    }

                    if (c.getPrecio() == null || c.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                        System.err.println("WARNING: Componente ID " + c.getComponenteID() + " tiene precio inválido");
                        continue;
                    }

                    // Use ProductoDAO to get current stock level with error handling
                    int currentStock;
                    try {
                        currentStock = productoDAO.getStockLevel(c.getComponenteID());
                    } catch (Exception stockEx) {
                        System.err.println("WARNING: Error al obtener stock para componente ID " + c.getComponenteID()
                                + ": " + stockEx.getMessage());
                        // Fallback to component's own stock field
                        currentStock = c.getCantidadDisponible();
                    }

                    // Display stock levels with warnings for insufficient stock
                    String stockStatus;
                    if (currentStock < 0) {
                        stockStatus = " (ERROR STOCK)";
                        System.err.println("ERROR: Componente ID " + c.getComponenteID() + " tiene stock negativo: "
                                + currentStock);
                    } else if (currentStock == 0) {
                        stockStatus = " (SIN STOCK)";
                    } else if (currentStock <= 5) {
                        stockStatus = " (Stock BAJO: " + currentStock + ")";
                    } else {
                        stockStatus = " (Stock: " + currentStock + ")";
                    }

                    String displayText = c.getTipoComponente() + " - " + c.getNumeroSerie() +
                            " - $" + c.getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP) + stockStatus;
                    model.addElement(displayText);

                } catch (Exception componentEx) {
                    System.err.println("ERROR: Failed to process component ID " + c.getComponenteID() + ": "
                            + componentEx.getMessage());
                    // Add error entry for this component
                    model.addElement("ERROR: Componente ID " + c.getComponenteID() + " (datos inválidos)");
                }
            }

            if (model.isEmpty()) {
                model.addElement("No hay componentes válidos disponibles");
            }

            ventaView.getComponentesList().setModel(model);

        } catch (Exception ex) {
            handleDataLoadError("componentes", ex);
            // Add fallback model
            DefaultListModel<String> errorModel = new DefaultListModel<>();
            errorModel.addElement("Error al cargar componentes");
            ventaView.getComponentesList().setModel(errorModel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventaView.getBtnRegistrarVenta()) {
            registrarVenta();
        } else if (e.getSource() == ventaView.getBtnVolver()) {
            ventaView.hideView();
            homeController.showHomeView();
        } else if (e.getSource() == ventaView.getBtnNuevoCliente()) {
            agregarNuevoCliente();
        } else if (e.getSource() == ventaView.getBtnAgregarComponente()) {
            agregarComponenteASeleccionados();
        }
    }

    private void agregarComponenteASeleccionados() {
        try {
            List<String> seleccionados = ventaView.getComponentesList().getSelectedValuesList();

            if (seleccionados == null || seleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(ventaView,
                        "Por favor seleccione al menos un componente de la lista.",
                        "Selección Requerida", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DefaultListModel<String> model = ventaView.getSeleccionadosModel();
            int addedCount = 0;
            int errorCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (String s : seleccionados) {
                try {
                    // Validate selection text
                    if (s == null || s.trim().isEmpty()) {
                        errorCount++;
                        errorMessages.append("- Selección vacía o inválida\n");
                        continue;
                    }

                    // Check for error entries
                    if (s.startsWith("ERROR:") || s.contains("Error al cargar") || s.contains("No hay componentes")) {
                        JOptionPane.showMessageDialog(ventaView,
                                "No se puede agregar: " + s,
                                "Selección Inválida", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    // Validate product availability before adding
                    Componente componente = findComponenteByDisplayText(s);
                    if (componente == null) {
                        errorCount++;
                        errorMessages.append("- No se pudo encontrar el componente: ").append(s).append("\n");
                        continue;
                    }

                    // Enhanced stock validation
                    if (!validateComponentForAddition(componente, s, model)) {
                        errorCount++;
                        continue; // Error message already shown in validation method
                    }

                    model.addElement(s);
                    addedCount++;

                } catch (Exception componentEx) {
                    errorCount++;
                    errorMessages.append("- Error procesando: ").append(s).append(" (").append(componentEx.getMessage())
                            .append(")\n");
                    System.err.println("ERROR: Failed to add component: " + s + " - " + componentEx.getMessage());
                }
            }

            // Show summary if there were errors
            if (errorCount > 0) {
                String message = "Se agregaron " + addedCount + " componentes exitosamente.\n" +
                        errorCount + " componentes no pudieron ser agregados:\n\n" + errorMessages.toString();
                JOptionPane.showMessageDialog(ventaView, message, "Resumen de Adición", JOptionPane.WARNING_MESSAGE);
            } else if (addedCount > 0) {
                // Only show success message if no errors and items were added
                System.out.println("INFO: Successfully added " + addedCount + " components to selection");
            }

            actualizarTotal();

        } catch (Exception ex) {
            handlePurchaseError("Error al agregar componentes a la selección", ex);
        }
    }

    /**
     * Validates if a component can be added to the selection
     */
    private boolean validateComponentForAddition(Componente componente, String displayText,
            DefaultListModel<String> model) {
        try {
            // Use ProductoDAO for enhanced stock validation
            int currentStock = productoDAO.getStockLevel(componente.getComponenteID());

            if (currentStock < 0) {
                JOptionPane.showMessageDialog(ventaView,
                        "Error de datos: El componente " + componente.getTipoComponente() + " - " +
                                componente.getNumeroSerie() + " tiene stock negativo.",
                        "Error de Datos", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (currentStock <= 0 || productoDAO.isOutOfStock(componente.getComponenteID())) {
                JOptionPane.showMessageDialog(ventaView,
                        "El componente " + componente.getTipoComponente() + " - " + componente.getNumeroSerie() +
                                " no tiene stock disponible.",
                        "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Check if already selected and count occurrences
            int selectedCount = 0;
            for (int i = 0; i < model.size(); i++) {
                if (displayText.equals(model.get(i))) {
                    selectedCount++;
                }
            }

            // Validate if we can add one more unit
            if (!productoDAO.isStockAvailable(componente.getComponenteID(), selectedCount + 1)) {
                JOptionPane.showMessageDialog(ventaView,
                        "No se puede agregar más unidades de " + componente.getTipoComponente() + " - " +
                                componente.getNumeroSerie() + ".\n\n" +
                                "Stock disponible: " + currentStock + "\n" +
                                "Ya seleccionadas: " + selectedCount,
                        "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventaView,
                    "Error al validar stock para " + componente.getTipoComponente() + " - " +
                            componente.getNumeroSerie() + ":\n" + ex.getMessage(),
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private Componente findComponenteByDisplayText(String displayText) {
        if (displayText == null || displayText.trim().isEmpty()) {
            System.err.println("WARNING: findComponenteByDisplayText called with null or empty displayText");
            return null;
        }

        if (componentesCache == null || componentesCache.isEmpty()) {
            System.err.println("WARNING: componentesCache is null or empty");
            return null;
        }

        for (Componente c : componentesCache) {
            if (c == null) {
                System.err.println("WARNING: null component found in componentesCache");
                continue;
            }

            try {
                // Validate component data
                if (c.getTipoComponente() == null || c.getNumeroSerie() == null || c.getPrecio() == null) {
                    System.err.println("WARNING: Component ID " + c.getComponenteID() + " has null required fields");
                    continue;
                }

                // Try with ProductoDAO stock level first
                try {
                    int currentStock = productoDAO.getStockLevel(c.getComponenteID());

                    String stockStatus;
                    if (currentStock < 0) {
                        stockStatus = " (ERROR STOCK)";
                    } else if (currentStock == 0) {
                        stockStatus = " (SIN STOCK)";
                    } else if (currentStock <= 5) {
                        stockStatus = " (Stock BAJO: " + currentStock + ")";
                    } else {
                        stockStatus = " (Stock: " + currentStock + ")";
                    }

                    String expectedText = c.getTipoComponente() + " - " + c.getNumeroSerie() +
                            " - $" + c.getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP) + stockStatus;
                    if (expectedText.equals(displayText)) {
                        return c;
                    }
                } catch (Exception stockEx) {
                    // Fallback to component's own stock field if ProductoDAO fails
                    System.err.println("WARNING: ProductoDAO failed for component ID " + c.getComponenteID()
                            + ", using fallback: " + stockEx.getMessage());

                    String stockStatus;
                    int fallbackStock = c.getCantidadDisponible();
                    if (fallbackStock < 0) {
                        stockStatus = " (ERROR STOCK)";
                    } else if (fallbackStock == 0) {
                        stockStatus = " (SIN STOCK)";
                    } else {
                        stockStatus = " (Stock: " + fallbackStock + ")";
                    }

                    String expectedText = c.getTipoComponente() + " - " + c.getNumeroSerie() +
                            " - $" + c.getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP) + stockStatus;
                    if (expectedText.equals(displayText)) {
                        return c;
                    }
                }

            } catch (Exception componentEx) {
                System.err.println("ERROR: Failed to process component ID " + c.getComponenteID()
                        + " in findComponenteByDisplayText: " + componentEx.getMessage());
                continue;
            }
        }

        System.err.println("WARNING: Component not found for display text: " + displayText);
        return null;
    }

    private void actualizarTotal() {
        try {
            BigDecimal total = BigDecimal.ZERO;
            boolean hasErrors = false;

            // Add combo price if applicable
            int comboIdx = ventaView.getComboComboBox().getSelectedIndex();
            if (comboIdx > 0 && combosCache != null && comboIdx <= combosCache.size()) {
                try {
                    Combo combo = combosCache.get(comboIdx - 1);
                    if (combo != null && combo.getDetalles() != null) {
                        BigDecimal precioOriginal = BigDecimal.ZERO;

                        for (var detalle : combo.getDetalles()) {
                            if (detalle != null && detalle.getComponente() != null
                                    && detalle.getComponente().getPrecio() != null) {
                                BigDecimal componentPrice = detalle.getComponente().getPrecio()
                                        .multiply(new BigDecimal(detalle.getCantidad()));
                                precioOriginal = precioOriginal.add(componentPrice);
                            }
                        }

                        BigDecimal precioFinal = combo.calculateDiscountedPrice(precioOriginal);
                        if (precioFinal != null && precioFinal.compareTo(BigDecimal.ZERO) >= 0) {
                            total = total.add(precioFinal);
                        } else {
                            hasErrors = true;
                            System.err.println("WARNING: Combo price calculation returned invalid result");
                        }
                    }
                } catch (Exception comboEx) {
                    hasErrors = true;
                    System.err.println("ERROR: Failed to calculate combo price: " + comboEx.getMessage());
                }
            }

            // Add prices of selected individual components
            DefaultListModel<String> model = ventaView.getSeleccionadosModel();
            if (model != null) {
                for (int i = 0; i < model.size(); i++) {
                    try {
                        String desc = model.get(i);
                        if (desc != null && !desc.trim().isEmpty()) {
                            Componente c = findComponenteByDisplayText(desc);
                            if (c != null && c.getPrecio() != null && c.getPrecio().compareTo(BigDecimal.ZERO) >= 0) {
                                total = total.add(c.getPrecio());
                            } else {
                                hasErrors = true;
                                System.err.println("WARNING: Component not found or has invalid price: " + desc);
                            }
                        }
                    } catch (Exception componentEx) {
                        hasErrors = true;
                        System.err.println("ERROR: Failed to add component price: " + componentEx.getMessage());
                    }
                }
            }

            // Update display
            String displayText = "Total: $" + total.setScale(2, BigDecimal.ROUND_HALF_UP);
            if (hasErrors) {
                displayText += " (*)";
                System.err.println("WARNING: Total calculation completed with errors. Some items may not be included.");
            }

            ventaView.getLblTotal().setText(displayText);

        } catch (Exception ex) {
            System.err.println("ERROR: Failed to update total: " + ex.getMessage());
            ex.printStackTrace();
            ventaView.getLblTotal().setText("Total: ERROR");
        }
    }

    private void registrarVenta() {
        try {
            // Step 1: Validate customer selection
            if (!validateCustomerSelection()) {
                return;
            }
            Cliente cliente = clientesCache.get(ventaView.getClienteComboBox().getSelectedIndex());

            // Step 2: Validate product selection
            if (!validateProductSelection()) {
                return;
            }

            // Step 3: Collect and validate purchase data
            PurchaseData purchaseData = collectPurchaseData();
            if (purchaseData == null) {
                return; // Error already shown in collectPurchaseData
            }

            // Step 4: Show purchase confirmation dialog
            if (!showPurchaseConfirmation(cliente, purchaseData)) {
                return; // User cancelled
            }

            // Step 5: Execute complete purchase transaction
            boolean success = executePurchaseTransaction(cliente, purchaseData);

            if (success) {
                handlePurchaseSuccess(purchaseData.idFactura);
            }

        } catch (Exception ex) {
            handlePurchaseError("Error inesperado al procesar la venta", ex);
        }
    }

    /**
     * Validates customer selection
     */
    private boolean validateCustomerSelection() {
        int clienteIdx = ventaView.getClienteComboBox().getSelectedIndex();
        if (clienteIdx < 0) {
            JOptionPane.showMessageDialog(ventaView,
                    "Por favor seleccione un cliente antes de continuar.",
                    "Cliente Requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Validates that at least one product is selected
     */
    private boolean validateProductSelection() {
        int comboIdx = ventaView.getComboComboBox().getSelectedIndex();
        DefaultListModel<String> model = ventaView.getSeleccionadosModel();

        if (comboIdx <= 0 && model.isEmpty()) {
            JOptionPane.showMessageDialog(ventaView,
                    "Por favor seleccione al menos un combo o componente individual para la venta.",
                    "Productos Requeridos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Inner class to hold purchase data
     */
    private static class PurchaseData {
        String idFactura;
        Combo combo;
        float total;
        ArrayList<Componente> componentesVenta;
        List<int[]> productQuantities;

        PurchaseData(String idFactura, Combo combo, float total,
                ArrayList<Componente> componentesVenta, List<int[]> productQuantities) {
            this.idFactura = idFactura;
            this.combo = combo;
            this.total = total;
            this.componentesVenta = componentesVenta;
            this.productQuantities = productQuantities;
        }
    }

    /**
     * Collects and validates all purchase data
     */
    private PurchaseData collectPurchaseData() {
        try {
            int comboIdx = ventaView.getComboComboBox().getSelectedIndex();
            Combo combo = null;
            float total = 0f;
            ArrayList<Componente> componentesVenta = new ArrayList<>();

            // Collect combo components and calculate price
            if (comboIdx > 0) { // 0 es "Ninguno"
                combo = combosCache.get(comboIdx - 1);

                // Validate combo data
                if (combo.getDetalles() == null || combo.getDetalles().isEmpty()) {
                    JOptionPane.showMessageDialog(ventaView,
                            "El combo seleccionado no tiene componentes válidos.",
                            "Error de Combo", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                BigDecimal precioOriginal = BigDecimal.ZERO;
                for (var detalle : combo.getDetalles()) {
                    if (detalle.getComponente() != null && detalle.getComponente().getPrecio() != null) {
                        precioOriginal = precioOriginal.add(
                                detalle.getComponente().getPrecio().multiply(new BigDecimal(detalle.getCantidad())));
                    }
                }
                BigDecimal precioFinal = combo.calculateDiscountedPrice(precioOriginal);
                total += precioFinal.floatValue();

                // Add combo components to sale list
                for (var detalle : combo.getDetalles()) {
                    if (detalle.getComponente() == null) {
                        JOptionPane.showMessageDialog(ventaView,
                                "Error: El combo contiene componentes inválidos.",
                                "Error de Datos", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    for (int i = 0; i < detalle.getCantidad(); i++) {
                        componentesVenta.add(detalle.getComponente());
                    }
                }
            }

            // Collect individual components
            DefaultListModel<String> model = ventaView.getSeleccionadosModel();
            for (int i = 0; i < model.size(); i++) {
                String desc = model.get(i);
                Componente componente = findComponenteByDisplayText(desc);
                if (componente == null) {
                    JOptionPane.showMessageDialog(ventaView,
                            "Error: No se pudo encontrar el componente: " + desc,
                            "Error de Datos", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                componentesVenta.add(componente);
                total += componente.getPrecio().floatValue();
            }

            // Calculate product quantities for stock validation
            List<int[]> productQuantities = calculateProductQuantities(componentesVenta);

            // Validate stock availability
            if (!validateStockAvailability(productQuantities)) {
                return null; // Error already shown
            }

            String idFactura = InvoiceIdGenerator.generateUniqueInvoiceId();
            return new PurchaseData(idFactura, combo, total, componentesVenta, productQuantities);

        } catch (Exception ex) {
            handlePurchaseError("Error al procesar los datos de la venta", ex);
            return null;
        }
    }

    /**
     * Calculates product quantities from components list
     */
    private List<int[]> calculateProductQuantities(ArrayList<Componente> componentesVenta) {
        List<int[]> productQuantities = new ArrayList<>();

        for (Componente c : componentesVenta) {
            // Count how many times this component appears in the sale
            int quantity = 0;
            for (Componente comp : componentesVenta) {
                if (comp.getComponenteID() == c.getComponenteID()) {
                    quantity++;
                }
            }

            // Add to validation list (avoid duplicates)
            boolean found = false;
            for (int[] pq : productQuantities) {
                if (pq[0] == c.getComponenteID()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                productQuantities.add(new int[] { c.getComponenteID(), quantity });
            }
        }

        return productQuantities;
    }

    /**
     * Validates stock availability for all products
     */
    private boolean validateStockAvailability(List<int[]> productQuantities) {
        try {
            if (!productoDAO.validateMultipleStock(productQuantities)) {
                StringBuilder errorMsg = new StringBuilder("Stock insuficiente para los siguientes productos:\n\n");

                for (int[] pq : productQuantities) {
                    int componenteID = pq[0];
                    int requiredQuantity = pq[1];
                    int currentStock = productoDAO.getStockLevel(componenteID);

                    if (currentStock < requiredQuantity) {
                        // Find component name for error message
                        for (Componente c : componentesCache) {
                            if (c.getComponenteID() == componenteID) {
                                errorMsg.append("• ").append(c.getTipoComponente()).append(" - ")
                                        .append(c.getNumeroSerie()).append("\n")
                                        .append("  Necesario: ").append(requiredQuantity)
                                        .append(", Disponible: ").append(currentStock).append("\n\n");
                                break;
                            }
                        }
                    }
                }

                JOptionPane.showMessageDialog(ventaView, errorMsg.toString(),
                        "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        } catch (Exception ex) {
            handlePurchaseError("Error al validar disponibilidad de stock", ex);
            return false;
        }
    }

    /**
     * Shows purchase confirmation dialog
     */
    private boolean showPurchaseConfirmation(Cliente cliente, PurchaseData purchaseData) {
        StringBuilder confirmMsg = new StringBuilder();
        confirmMsg.append("CONFIRMACIÓN DE VENTA\n\n");
        confirmMsg.append("Cliente: ").append(cliente.getNombre()).append(" ").append(cliente.getApellido())
                .append("\n");
        confirmMsg.append("Factura ID: ").append(purchaseData.idFactura).append("\n\n");

        if (purchaseData.combo != null) {
            String comboName = purchaseData.combo.getNombre();
            if (comboName == null || comboName.trim().isEmpty()) {
                comboName = "Combo #" + purchaseData.combo.getComboID();
            }
            confirmMsg.append("Combo: ").append(comboName).append("\n");
        }

        if (!ventaView.getSeleccionadosModel().isEmpty()) {
            confirmMsg.append("Componentes individuales: ").append(ventaView.getSeleccionadosModel().size())
                    .append("\n");
        }

        confirmMsg.append("\nTotal: $").append(String.format("%.2f", purchaseData.total));
        confirmMsg.append("\n\n¿Confirma el registro de esta venta?");

        int option = JOptionPane.showConfirmDialog(ventaView, confirmMsg.toString(),
                "Confirmar Venta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        return option == JOptionPane.YES_OPTION;
    }

    /**
     * Executes the complete purchase transaction
     */
    private boolean executePurchaseTransaction(Cliente cliente, PurchaseData purchaseData) {
        try {
            Date fecha = new Date(System.currentTimeMillis());
            Factura factura = new Factura(purchaseData.idFactura, cliente, purchaseData.total,
                    purchaseData.combo, fecha);
            factura.setComponentes(purchaseData.componentesVenta);

            // Execute complete purchase transaction atomically
            boolean transactionSuccess = transactionService.executeCompletePurchaseTransaction(factura,
                    purchaseData.productQuantities);

            if (!transactionSuccess) {
                JOptionPane.showMessageDialog(ventaView,
                        "No se pudo completar la transacción. Verifique la disponibilidad de stock y la conexión a la base de datos.",
                        "Error de Transacción", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Validate transaction consistency
            if (!transactionService.validateTransactionConsistency(purchaseData.idFactura,
                    purchaseData.productQuantities)) {
                JOptionPane.showMessageDialog(ventaView,
                        "ADVERTENCIA: La transacción se completó pero la validación de consistencia falló.\n" +
                                "Por favor verifique manualmente la venta con ID: " + purchaseData.idFactura,
                        "Advertencia de Consistencia", JOptionPane.WARNING_MESSAGE);
            }

            return true;

        } catch (Exception ex) {
            handlePurchaseError("Error al ejecutar la transacción de venta", ex);
            return false;
        }
    }

    /**
     * Handles successful purchase completion
     */
    private void handlePurchaseSuccess(String facturaId) {
        try {
            // Refresh the components cache to reflect updated stock levels
            cargarComponentes();

            // Clear the selection after successful registration
            ventaView.getSeleccionadosModel().clear();
            ventaView.getComboComboBox().setSelectedIndex(0); // Reset to "Ninguno"
            ventaView.getLblTotal().setText("Total: $0.00");

            // Show success message with invoice ID
            JOptionPane.showMessageDialog(ventaView,
                    "¡Venta registrada exitosamente!\n\nID de Factura: " + facturaId +
                            "\n\nEl inventario ha sido actualizado automáticamente.",
                    "Venta Completada", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            // Even if refresh fails, the sale was successful
            JOptionPane.showMessageDialog(ventaView,
                    "Venta registrada exitosamente (ID: " + facturaId + ")\n\n" +
                            "Nota: Error al actualizar la vista. Por favor recargue la pantalla manualmente.",
                    "Venta Completada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Handles purchase errors with detailed logging and user feedback
     */
    private void handlePurchaseError(String userMessage, Exception ex) {
        // Log detailed error for debugging
        System.err.println("Purchase Error: " + userMessage);
        System.err.println("Exception: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        if (ex.getCause() != null) {
            System.err.println(
                    "Caused by: " + ex.getCause().getClass().getSimpleName() + " - " + ex.getCause().getMessage());
        }
        ex.printStackTrace();

        // Show user-friendly error message
        String displayMessage = userMessage;
        if (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) {
            displayMessage += "\n\nDetalles técnicos: " + ex.getMessage();
        }

        JOptionPane.showMessageDialog(ventaView, displayMessage, "Error de Venta", JOptionPane.ERROR_MESSAGE);
    }

    private void agregarNuevoCliente() {
        try {
            String[] datos = ventaView.showNuevoClienteDialog();
            if (datos == null) {
                return; // User cancelled
            }

            // Validate client data
            if (!validateClientData(datos)) {
                return; // Validation failed, error already shown
            }

            Cliente nuevo = new Cliente(datos[0].trim(), datos[1].trim(), datos[2].trim(), datos[3].trim());

            // Attempt to add client
            clienteService.addCliente(nuevo);

            // Reload clients and select the new one
            int previousCount = ventaView.getClienteComboBox().getItemCount();
            cargarClientes();

            // Verify the client was added successfully
            if (ventaView.getClienteComboBox().getItemCount() > previousCount) {
                // Select the newly added client (should be last)
                ventaView.getClienteComboBox().setSelectedIndex(ventaView.getClienteComboBox().getItemCount() - 1);
                JOptionPane.showMessageDialog(ventaView,
                        "Cliente agregado exitosamente y seleccionado automáticamente.",
                        "Cliente Agregado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ventaView,
                        "El cliente fue procesado pero no aparece en la lista. Por favor recargue la pantalla.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            handlePurchaseError("Error al agregar nuevo cliente", ex);
        }
    }

    /**
     * Validates client data before creation
     */
    private boolean validateClientData(String[] datos) {
        if (datos.length != 4) {
            JOptionPane.showMessageDialog(ventaView,
                    "Error interno: Datos de cliente incompletos.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String nombre = datos[0] != null ? datos[0].trim() : "";
        String apellido = datos[1] != null ? datos[1].trim() : "";
        String email = datos[2] != null ? datos[2].trim() : "";
        String telefono = datos[3] != null ? datos[3].trim() : "";

        StringBuilder errors = new StringBuilder();

        // Validate required fields
        if (nombre.isEmpty()) {
            errors.append("- El nombre es requerido\n");
        } else if (nombre.length() > 50) {
            errors.append("- El nombre no puede exceder 50 caracteres\n");
        }

        if (apellido.isEmpty()) {
            errors.append("- El apellido es requerido\n");
        } else if (apellido.length() > 50) {
            errors.append("- El apellido no puede exceder 50 caracteres\n");
        }

        // Validate email format (basic validation)
        if (!email.isEmpty()) {
            if (!email.contains("@") || !email.contains(".") || email.length() > 100) {
                errors.append("- El formato del email no es válido\n");
            }
        }

        // Validate phone (basic validation)
        if (!telefono.isEmpty()) {
            if (telefono.length() > 20) {
                errors.append("- El teléfono no puede exceder 20 caracteres\n");
            }
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(ventaView,
                    "Por favor corrija los siguientes errores:\n\n" + errors.toString(),
                    "Datos Inválidos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Generic error handler for data loading operations
     */
    private void handleDataLoadError(String dataType, Exception ex) {
        String userMessage = "Error al cargar " + dataType + " desde la base de datos.";

        // Log detailed error
        System.err.println(
                "Data Load Error (" + dataType + "): " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        if (ex.getCause() != null) {
            System.err.println(
                    "Caused by: " + ex.getCause().getClass().getSimpleName() + " - " + ex.getCause().getMessage());
        }
        ex.printStackTrace();

        // Show user-friendly error
        String displayMessage = userMessage + "\n\nVerifique la conexión a la base de datos y vuelva a intentar.";
        if (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) {
            displayMessage += "\n\nDetalles técnicos: " + ex.getMessage();
        }

        JOptionPane.showMessageDialog(ventaView, displayMessage,
                "Error de Carga de Datos", JOptionPane.ERROR_MESSAGE);
    }
}