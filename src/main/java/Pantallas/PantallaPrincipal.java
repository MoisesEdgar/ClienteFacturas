package Pantallas;

import APIS.FacturaAPI;
import APIS.ClienteAPI;
import DTO.ClienteDTO;
import DTO.FacturaDTO;
import DTO.PartidaDTO;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.springframework.http.ResponseEntity;

public class PantallaPrincipal extends javax.swing.JFrame {

    private final ModeloFactura modeloFacturas = new ModeloFactura();
    private final ClienteAPI clienteAPI = new ClienteAPI();
    private final FacturaAPI facturaAPI = new FacturaAPI();
    private FacturaDTO facturaGlobal;

    public PantallaPrincipal() {
        initComponents();

        modeloFacturas.addTableModelListener(this::onModeloFacturasModificado);

        tblFactura.setModel(modeloFacturas);
        tblFactura.getColumnModel().getColumn(2).setCellRenderer(new DecimalesRenderer());
        tblFactura.getColumnModel().getColumn(3).setCellRenderer(new DecimalesRenderer());

        btnGuardarFactura.addActionListener(this::onButonGuardarFacturaClicked);
        btnEliminarFactura.addActionListener(this::onButonEliminarFacturaClicked);
        btnAgregarPartida.addActionListener(this::onButonAgregarPartidaClicked);
        btnEliminarPartida.addActionListener(this::onButonEliminarPartidaClicked);

        btnLimpiar.addActionListener(this::onButonLimpiarClicked);

        txtFolio.addKeyListener(new TextFieldFolioKeyAdapter());

        txtCodigo.addKeyListener(new TextFieldCodigoKeyAdapter());

        txtNombre.addKeyListener(new TextFieldNombreKeyAdapter());

        txtCantidad.addKeyListener(new TextFieldCantidadKeyAdapter());

        txtPrecio.addKeyListener(new TextFieldPrecioKeyAdapter());
    }

    private class TextFieldFolioKeyAdapter extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                limpiarInformacionlFactura();
                limpiarTabla();
                txtFolio.requestFocus();
                String folio = txtFolio.getText();

                try {

                    if (!txtFolio.getText().isEmpty()) {
                        FacturaDTO factura = facturaAPI.getByFolio(folio);
                        if (factura != null) {
                            facturaGlobal = factura;
                            llenarTabla(facturaGlobal);
                        } else {
                            if (validarFormatoFolio(folio)) {
                                lblFolio.setText(folio);
                                String fecha = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                                lblFecha.setText(fecha);
                                facturaGlobal = new FacturaDTO();
                                facturaGlobal.partidas = new ArrayList<>();
                                txtCodigo.requestFocus();
                            }

                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "No se espesifico el folio de la factura");
                        limpiarTabla();
                        txtFolio.requestFocus();
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "No se pudo consultar la factura. Verifique su conexion.");
                }
            }
        }

    }

    private class TextFieldCodigoKeyAdapter extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt
        ) {
            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Se debe espesificar un folio");
                evt.consume();
                txtFolio.requestFocus();
            } else if (evt.getKeyChar() == KeyEvent.VK_ENTER) {

                if (!txtCodigo.getText().isEmpty()) {
                    try {
                        String codigo = txtCodigo.getText();

                        if (validarFormatoCodigo(codigo)) {

                            ClienteDTO cliente = clienteAPI.getByCodigo(codigo);

                            if (cliente == null) {
                                JOptionPane.showMessageDialog(null, "No existe un cliente con ese codigo");
                                PantallaCliente clientePantalla = new PantallaCliente();
                                clientePantalla.setVisible(true);
                            } else {
                                facturaGlobal.cliente_id = cliente.id;
                                txtNombre.requestFocus();
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "No se pudo conectar con el servidor. Verifique su conexion.");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "No se espesifico el codigo del cliente");
                    txtCodigo.requestFocus();
                }
            }
        }

    }

    private class TextFieldNombreKeyAdapter extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt
        ) {
            char c = evt.getKeyChar();

            if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z'))) {
                evt.consume();
            }

            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Se debe espesificar un folio");
                evt.consume();
                txtFolio.requestFocus();
            } else if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Se debe espesificar un codigo de cliente");
                evt.consume();
                txtCodigo.requestFocus();
            }
        }

    }

    private class TextFieldCantidadKeyAdapter extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt
        ) {
            char c = evt.getKeyChar();

            if (c < '0' || c > '9') {
                evt.consume();
            }

            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Se debe espesificar un folio");
                evt.consume();
                txtFolio.requestFocus();
            } else if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Se debe espesificar un codigo de cliente");
                evt.consume();
                txtCodigo.requestFocus();
            }
        }

    }

    private class TextFieldPrecioKeyAdapter extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt
        ) {
            char c = evt.getKeyChar();

            if ((c < '0' || c > '9') && (c != '.')) {
                evt.consume();
            }
            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Se debe espesificar un folio");

                evt.consume();
                txtFolio.requestFocus();
            } else if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Se debe espesificar un codigo de cliente");

                evt.consume();
                txtCodigo.requestFocus();
            }
        }

    }

    //*****************************BOTONES*****************************
    private void onButonGuardarFacturaClicked(ActionEvent evt) {
        String folio = txtFolio.getText();
        List<PartidaDTO> partidas = facturaGlobal.partidas;

        try {

            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se espesifico el folio de la factura");
                limpiarTabla();
                txtFolio.requestFocus();
                return;
            }

            if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se espesifico el codigo del cliente");
                txtCodigo.requestFocus();
                return;
            }

            if (partidas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La factura debe contener al menos una partida");
                txtNombre.requestFocus();
                return;
            }

            if (facturaGlobal.id == null) {
                facturaGlobal.folio = folio;
                facturaGlobal.fecha_expedicion = Calendar.getInstance().getTime();

                ResponseEntity<FacturaDTO> facturaGuardada = facturaAPI.save(facturaGlobal);
                limpiarTodo();
                txtFolio.requestFocus();
                JOptionPane.showMessageDialog(this, "Se agrego una nueva factura con el folio: " + facturaGuardada.getBody().folio);

            } else {

                ResponseEntity<FacturaDTO> facturaActualizada = facturaAPI.update(facturaGlobal);
                limpiarTodo();
                txtFolio.requestFocus();
                JOptionPane.showMessageDialog(this, "Se modifico la factura con el folio: " + facturaActualizada.getBody().folio);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar la factura. Verifique su conexión");
        }
    }

    private void onButonEliminarFacturaClicked(ActionEvent evt) {
        String folio = txtFolio.getText();
        if (folio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El folio del la factura no fue especificado");
            txtFolio.requestFocus();
        } else {
            try {

                FacturaDTO factura = facturaAPI.getByFolio(folio);
                if (factura != null) {
                    facturaAPI.delete(Math.toIntExact(factura.id));
                    JOptionPane.showMessageDialog(this, "Se elimino la factura con el folio " + folio);
                    limpiarTodo();
                    txtFolio.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontro el folio de la factura. Verifique que la factura ya fue creada");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la factura. Verifique su conexión.");
            }
        }

    }

    private void onButonAgregarPartidaClicked(ActionEvent evt) {

        if (validarDatosPartida()) {
            if (!nombreExiste(txtNombre.getText())) {

                PartidaDTO partidaDTO = new PartidaDTO();
                partidaDTO.nombre_articulo = txtNombre.getText();
                partidaDTO.cantidad = Integer.parseInt(txtCantidad.getText());
                partidaDTO.precio = Double.parseDouble(txtPrecio.getText());

                Partida partida = new Partida();
                partida.nombreArticulo = txtNombre.getText();
                partida.cantidad = Integer.parseInt(txtCantidad.getText());
                partida.precio = Double.parseDouble(txtPrecio.getText());

                modeloFacturas.agregar(partida);
                facturaGlobal.partidas.add(partidaDTO);

                limpiarCapturaPartida();
                txtNombre.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Ese articulo ya esta registrado");
                limpiarCapturaPartida();
                txtNombre.requestFocus();
            }
        }

    }

    private void onButonEliminarPartidaClicked(ActionEvent evt) {

        int index = tblFactura.getSelectedRow();
        if (index > -1) {
            Partida partida = modeloFacturas.getPartida(index);
            String nombre = partida.nombreArticulo;
            modeloFacturas.eliminar(index);

            JOptionPane.showMessageDialog(this, "Se elimino la partida con el articulo: " + nombre);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el articulo a Eliminar");
        }

    }

    private void onModeloFacturasModificado(TableModelEvent evt) {
        int rowIndex = evt.getFirstRow();
        int colIndex = evt.getColumn();

        switch (evt.getType()) {
            case TableModelEvent.UPDATE:
                boolean opc = false;

                if (colIndex == 0) {
                    String nombreArticulo = (String) modeloFacturas.getValueAt(rowIndex, 0);
                    if (nombreArticulo.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "El campo nombre no puede estar vacio");
                        modeloFacturas.setValueAt(facturaGlobal.partidas.get(rowIndex).nombre_articulo, rowIndex, 0);
                    } else {

                        for (int i = 0; i < modeloFacturas.getRowCount(); i++) {
                            if (nombreArticulo.equalsIgnoreCase((String) modeloFacturas.getValueAt(i, 0))) {
                                if (i != rowIndex) {
                                    JOptionPane.showMessageDialog(this, "El articulos ya esta registrado");
                                    modeloFacturas.setValueAt(facturaGlobal.partidas.get(rowIndex).nombre_articulo, rowIndex, 0);
                                } else {
                                    opc = true;
                                }
                            }
                        }
                    }
                }

                if (colIndex == 1) {
                    Partida partida = modeloFacturas.getPartida(rowIndex);
                    Integer cantidad = partida.cantidad;

                    if (cantidad == null) {
                        JOptionPane.showMessageDialog(this, "El campo cantidad no puede estar vacio");
                        modeloFacturas.setValueAt(facturaGlobal.partidas.get(rowIndex).cantidad, rowIndex, 1);
                    } else if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(this, "La cantidad deve ser mayor a 0");
                        modeloFacturas.setValueAt(facturaGlobal.partidas.get(rowIndex).cantidad, rowIndex, 1);
                    } else {
                        opc = true;
                    }
                }

                if (colIndex == 2) {
                    Partida partida = modeloFacturas.getPartida(rowIndex);
                    Double precio = partida.precio;

                    if (precio == null) {
                        JOptionPane.showMessageDialog(this, "El campo precio no puede estar vacio");
                        modeloFacturas.setValueAt(facturaGlobal.partidas.get(rowIndex).precio, rowIndex, 2);
                    } else if (precio < 0.1) {
                        JOptionPane.showMessageDialog(this, "El precio deve ser mayora a 0.1");
                        modeloFacturas.setValueAt(facturaGlobal.partidas.get(rowIndex).precio, rowIndex, 2);
                    } else {
                        opc = true;
                    }
                }

                if (opc == true) {

                    String nombreArticulo = (String) modeloFacturas.getValueAt(rowIndex, 0);
                    Integer cantidad = (Integer) modeloFacturas.getValueAt(rowIndex, 1);
                    Double precio = (Double) modeloFacturas.getValueAt(rowIndex, 2);

                    PartidaDTO partidaMoidificada = facturaGlobal.partidas.get(rowIndex);

                    partidaMoidificada.nombre_articulo = nombreArticulo;
                    partidaMoidificada.cantidad = cantidad;
                    partidaMoidificada.precio = precio;

                    facturaGlobal.partidas.set(rowIndex, partidaMoidificada);

                }

                break;

            case TableModelEvent.DELETE:
                facturaGlobal.partidas.remove(rowIndex);
                break;
        }
        txtNombre.requestFocus();
        calcularTotales();
    }

    private void onButonLimpiarClicked(ActionEvent evt) {
        limpiarTodo();
        txtFolio.requestFocus();
    }

    private void llenarTabla(FacturaDTO factura) throws Exception {
        lblSubtotal.setText(factura.subtotal.toString());
        lblTotalIva.setText(factura.total.toString());
        lblFolio.setText(factura.folio);

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(factura.fecha_expedicion);
        lblFecha.setText(fecha);
        ClienteDTO cliente = clienteAPI.getById(facturaGlobal.cliente_id);
        txtCodigo.setText(cliente.codigo);

        for (int j = 0; j < factura.partidas.size(); j++) {
            PartidaDTO partida = factura.partidas.get(j);

            Partida partidasMostrar = new Partida();

            partidasMostrar.nombreArticulo = partida.nombre_articulo;
            partidasMostrar.cantidad = partida.cantidad;
            partidasMostrar.precio = partida.precio;

            modeloFacturas.agregar(partidasMostrar);
        }

    }

    //*****************************VALIDACIONES*****************************
    private boolean nombreExiste(String nombre) {
        for (int i = 0; i < modeloFacturas.getRowCount(); i++) {
            if (nombre.equalsIgnoreCase((String) modeloFacturas.getValueAt(i, 0))) {
                return true;
            }
        }
        return false;
    }

    private boolean validarDatosPartida() {

        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del articulo no fue especificado");
            txtNombre.requestFocus();
            return false;
        }

        if (txtCantidad.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cantidad no fue espesificada");
            txtCantidad.requestFocus();
            return false;
        }

        if (txtPrecio.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El precio no fue espesificado");
            txtPrecio.requestFocus();
            return false;
        }

        if (Integer.parseInt(txtCantidad.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0");
            txtCantidad.requestFocus();
            return false;
        }

        if (Double.parseDouble(txtPrecio.getText()) < 0.1) {
            JOptionPane.showMessageDialog(this, "El precio debe ser mayor o igual a 0.1");
            txtPrecio.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarFormatoFolio(String folio) {
        try {
            if (folio.matches("^F-\\d\\d\\d")) {

                String folioAnterior = facturaAPI.getAnterior();

                if (folioAnterior == null) {
                    if (folio.equalsIgnoreCase("F-001")) {
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(this, "El formato del folio no es valido. La nuemracion debe ser: F-001");
                        txtFolio.setText("F-001");
                        return false;
                    }

                } else {

                    Integer numeracionNueva = getNumeracionFolio(folio);
                    Integer numeracionAnterior = getNumeracionFolio(folioAnterior);

                    if (numeracionNueva != numeracionAnterior + 1) {
                        String con = String.valueOf(numeracionAnterior + 1);
                        String ceros = "";

                        for (int i = con.length(); i < 3;) {
                            i++;
                            ceros = ceros + "0";
                        }

                        JOptionPane.showMessageDialog(this, "El formato del folio no es valido. La nuemracion debe ser F-" + ceros + (numeracionAnterior + 1));
                        txtFolio.setText("F-" + ceros + (numeracionAnterior + 1));
                        return false;
                    }
                    return true;
                }
            } else {
                JOptionPane.showMessageDialog(this, "El formato del folio no es valido. Debe seguir el formato de F-000");
                txtFolio.setText("");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
        }
        return false;
    }

    private boolean validarFormatoCodigo(String codigo) {
        try {
            if (codigo.matches("^C-\\d\\d\\d")) {
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "El formato del codigo no es valido. Debe seguir el formato de C-000");
                txtCodigo.setText("");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
        }
        return false;
    }

    private Integer getNumeracionFolio(String folio) {
        String[] salto = folio.split("-");
        return Integer.parseInt(salto[1]);
    }

    private void calcularTotales() {
        Double totalNeto = 0.0;
        Double totalIva = 0.0;

        for (int rowIndex = 0; rowIndex < modeloFacturas.getRowCount(); rowIndex++) {
            Double total = (Double) modeloFacturas.getValueAt(rowIndex, 3);
            totalNeto += total;
        }

        totalIva = Math.round((totalNeto + totalNeto * .16) * 100) / 100d;

        lblSubtotal.setText(String.valueOf(Math.round(totalNeto * 100) / 100d));
        lblTotalIva.setText(String.valueOf(Math.round(totalIva * 100) / 100d));

    }

    //*****************************MODELO*****************************
    private static class Partida {

        public String nombreArticulo;
        public Integer cantidad;
        public Double precio;
        public Double total;
    }

    private static class ModeloFactura extends AbstractTableModel {

        private final List<Partida> partidas = new ArrayList<>();

        @Override
        public int getRowCount() {
            return partidas.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Class<?> getColumnClass(int columIndex) {
            switch (columIndex) {
                case 0:
                    return String.class;
                case 1:
                    return Integer.class;
                case 2:
                    return Double.class;
                case 3:
                    return Double.class;
            }
            return null;
        }

        @Override
        public String getColumnName(int colum) {
            switch (colum) {
                case 0:
                    return "Nombre del articulo";
                case 1:
                    return "Cantidad";
                case 2:
                    return "Precio";
                case 3:
                    return "Total";
            }
            return null;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            Partida partida = partidas.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return partida.nombreArticulo;
                case 1:
                    return partida.cantidad;
                case 2:
                    return partida.precio;
                case 3:
                    return (partida.cantidad * partida.precio);
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Partida partida = partidas.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    partida.nombreArticulo = (String) aValue;
                    fireTableCellUpdated(rowIndex, columnIndex);
                    break;
                case 1:
                    partida.cantidad = (Integer) aValue;
                    fireTableCellUpdated(rowIndex, columnIndex);
                    fireTableCellUpdated(rowIndex, 3);
                    break;
                case 2:
                    partida.precio = (Double) aValue;
                    fireTableCellUpdated(rowIndex, columnIndex);
                    fireTableCellUpdated(rowIndex, 3);
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 3;
        }

        public void agregar(Partida partida) {
            partidas.add(partida);
            fireTableRowsInserted(getRowCount(), getRowCount());
        }

        public Partida getPartida(int rowIndex) {
            return partidas.get(rowIndex);
        }

        public void eliminar(int index) {
            partidas.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }

    private static class DecimalesRenderer extends DefaultTableCellRenderer {

        private final DecimalFormat formatter = new DecimalFormat("#.00");

        public DecimalesRenderer() {
            super.setHorizontalAlignment(RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setValue(formatter.format(value));
            return this;
        }

    }

//*****************************TEXTO*****************************
    private void limpiarCapturaPartida() {
        txtNombre.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtNombre.requestFocus();
    }

    private void limpiarInformacionlFactura() {
        lblFecha.setText("");
        lblFolio.setText("");
        lblTotalIva.setText("");
        lblSubtotal.setText("");
    }

    private void limpiarTabla() {
        modeloFacturas.partidas.clear();
        modeloFacturas.fireTableDataChanged();
    }

    private void limpiarTodo() {
        txtFolio.setText("");
        txtCodigo.setText("");
        limpiarCapturaPartida();
        limpiarInformacionlFactura();
        limpiarTabla();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFolio = new javax.swing.JTextField();
        btnGuardarFactura = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        btnEliminarFactura = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        lblTotalIva = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFactura = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnEliminarPartida = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        btnAgregarPartida = new javax.swing.JButton();
        txtNombre = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblFolio = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();

        jPanel1.setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout jPanel4Layout1 = new java.awt.GridBagLayout();
        jPanel4Layout1.columnWidths = new int[] {0, 4, 0, 4, 0};
        jPanel4Layout1.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        jPanel4.setLayout(jPanel4Layout1);

        jLabel1.setText("Folio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(txtFolio, gridBagConstraints);

        btnGuardarFactura.setText("Guardar Factura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 150, 0, 150);
        jPanel4.add(btnGuardarFactura, gridBagConstraints);

        jLabel2.setText("Codigo del cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel4.add(txtCodigo, gridBagConstraints);

        btnEliminarFactura.setText("Eliminar Factura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 150, 0, 150);
        jPanel4.add(btnEliminarFactura, gridBagConstraints);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Subtotal:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 3, 0);
        jPanel5.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Total con IVA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanel5.add(jLabel5, gridBagConstraints);

        lblSubtotal.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel5.add(lblSubtotal, gridBagConstraints);

        lblTotalIva.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel5.add(lblTotalIva, gridBagConstraints);

        getContentPane().add(jPanel5, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.BorderLayout());

        tblFactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblFactura);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {0, 4, 0, 4, 0, 4, 0};
        jPanel3Layout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0};
        jPanel3.setLayout(jPanel3Layout);

        btnEliminarPartida.setText("Eliminar Partida");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 147, 0, 160);
        jPanel3.add(btnEliminarPartida, gridBagConstraints);

        jLabel6.setText("Nombre del articulo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel3.add(jLabel6, gridBagConstraints);

        jLabel8.setText("Cantidad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel8, gridBagConstraints);

        jLabel9.setText("Precio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(txtCantidad, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(txtPrecio, gridBagConstraints);

        btnAgregarPartida.setText("Agregar Partida");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 147, 0, 160);
        jPanel3.add(btnAgregarPartida, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(txtNombre, gridBagConstraints);

        jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Folio de la factura:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel6.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel6.add(lblFolio, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Fecha de expedicion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel6.add(jLabel10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel6.add(lblFecha, gridBagConstraints);

        btnLimpiar.setText("Limpiar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 31;
        gridBagConstraints.insets = new java.awt.Insets(9, 200, 0, 10);
        jPanel6.add(btnLimpiar, gridBagConstraints);

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PantallaPrincipal().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarPartida;
    private javax.swing.JButton btnEliminarFactura;
    private javax.swing.JButton btnEliminarPartida;
    private javax.swing.JButton btnGuardarFactura;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFolio;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotalIva;
    private javax.swing.JTable tblFactura;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtFolio;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
