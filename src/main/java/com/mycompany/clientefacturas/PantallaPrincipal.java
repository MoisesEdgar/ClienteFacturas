package com.mycompany.clientefacturas;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

public class PantallaPrincipal extends javax.swing.JFrame {

    private final ModeloFactura modeloFacturas = new ModeloFactura();

    public PantallaPrincipal() {
        initComponents();

        modeloFacturas.addTableModelListener(this::onModeloFacturasModificado);

        tblFactura.setModel(modeloFacturas);
        tblFactura.getColumnModel().getColumn(2).setCellRenderer(new DecimalesRenderer());
        tblFactura.getColumnModel().getColumn(3).setCellRenderer(new DecimalesRenderer());

        btnConsultar.addActionListener(this::onButonConsultarClicked);
        btnAgregarPartida.addActionListener(this::onButonAgregarPartidaClicked);
        btnEliminar.addActionListener(this::onButonEliminarClicked);
    }

    private void onButonAgregarPartidaClicked(ActionEvent evt) {

        Partida partida = new Partida();
        partida.nombreArticulo = getNombre();
        partida.cantidad = getCantidad();
        partida.precio = getPrecio();

        modeloFacturas.agregar(partida);

    }

    private void onButonConsultarClicked(ActionEvent evt) {
        String folio = getFolio();
        if (validar(folio)) {
            try {
                if (peticionGetFactura(folio) == false) {
                    JOptionPane.showMessageDialog(this, "No se encontro el folio de la factura");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            limpiarTxt();
        }

    }

    private void onButonEliminarClicked(ActionEvent evt) {
        int index = tblFactura.getSelectedRow();
        if (index > -1) {
            modeloFacturas.eliminar(index);
        }else{
            JOptionPane.showMessageDialog(this,"Seleccione la partida a Eliminar");
        }
    }

    private void onModeloFacturasModificado(TableModelEvent evt) {
        switch (evt.getType()) {
            case TableModelEvent.UPDATE:
                int rowIndex = evt.getFirstRow();
                int colIndex = evt.getColumn();

                if (colIndex == 1 || colIndex == 2) {
                    Partida partida = modeloFacturas.getPartida(rowIndex);
                    Integer cantidad = partida.cantidad;
                    Double precio = partida.precio;

                    if (precio < 0.1) {
                        JOptionPane.showMessageDialog(this, "El precio deve ser mayora a 0.1");
                        modeloFacturas.setValueAt(1.0, evt.getFirstRow(), 2);
                    }

                    if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(this, "La cantidad deve ser mayor a cero");
                        modeloFacturas.setValueAt(1, evt.getFirstRow(), 1);
                    }
                }

                break;
            //case TableModelEvent.INSERT: 
            //case TableModelEvent.DELETE:

        }
        calcularTotales();
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
        lblTotalIva.setText(String.valueOf(totalIva));

    }

    private boolean peticionGetFactura(String folio) throws Exception {

        URL url = new URL("http://localhost:8080/facturas");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        conexion.connect();

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder jsonFactura = new StringBuilder();

        while (scanner.hasNext()) {
            jsonFactura.append(scanner.nextLine());
        }

        scanner.close();

        JSONArray jsonArray = new JSONArray(jsonFactura.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (folio.equalsIgnoreCase(jsonObject.getString("folio"))) {

                // lblFolio.setText(jsonObject.getString("folio"));
                //  lblFecha.setText(jsonObject.getString("fecha_expedicion"));
                lblSubtotal.setText(String.valueOf(jsonObject.getDouble("subtotal")));
                lblTotalIva.setText(String.valueOf(jsonObject.getDouble("total")));
                // lblCliente.setText(String.valueOf(jsonObject.getInt("cliente_id")));

                JSONArray jsonArrayp = jsonObject.getJSONArray("partidas");

                for (int j = 0; j < jsonArrayp.length(); j++) {
                    JSONObject jsonObjectp = jsonArrayp.getJSONObject(j);

                    Partida partida = new Partida();
                    partida.nombreArticulo = jsonObjectp.getString("nombre_articulo");
                    partida.cantidad = jsonObjectp.getInt("cantidad");
                    partida.precio = jsonObjectp.getDouble("precio");

                    modeloFacturas.agregar(partida);
                }
                return true;
            }
        }
        return false;
    }

    private String getFolio() {
        if (txtFolio.getText().isEmpty()) {
            return "";
        } else {
            return txtFolio.getText();
        }
    }

    private String getNombre() {
        if (txtNombre.getText().isEmpty()) {
            return "";

        } else {
            return txtNombre.getText();
        }
    }

    private Integer getCantidad() {
        if (txtCantidad.getText().isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(txtCantidad.getText());
        }
    }

    private Double getPrecio() {
        if (txtPrecio.getText().isEmpty()) {
            return 0.0;
        } else {
            return Double.parseDouble(txtPrecio.getText());
        }
    }

    private boolean validar(String folio) {
        if (folio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se espesifico el folio de la factura");
            return false;
        }
        return true;
    }

    private void limpiarTxt() {
        txtFolio.setText("");
        txtFolio.requestFocus();
    }

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFolio = new javax.swing.JTextField();
        btnConsultar = new javax.swing.JButton();
        btnCrear = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtCantidad = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btnAgregarPartida = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFactura = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        lblTotalIva = new javax.swing.JLabel();

        jPanel1.setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout jPanel4Layout = new java.awt.GridBagLayout();
        jPanel4Layout.columnWidths = new int[] {0, 4, 0, 4, 0};
        jPanel4Layout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        jPanel4.setLayout(jPanel4Layout);

        jLabel1.setText("Folio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(txtFolio, gridBagConstraints);

        btnConsultar.setText("Consultar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(btnConsultar, gridBagConstraints);

        btnCrear.setText("Crear");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(btnCrear, gridBagConstraints);

        btnEliminar.setText("Eliminar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(btnEliminar, gridBagConstraints);

        jLabel6.setText("Nombre del articulo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel4.add(jLabel6, gridBagConstraints);

        jLabel8.setText("Cantidad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel8, gridBagConstraints);

        jLabel9.setText("Precio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        jPanel4.add(txtNombre, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(txtCantidad, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(txtPrecio, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Partida");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel10, gridBagConstraints);

        btnAgregarPartida.setText("Agregar Partida");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        jPanel4.add(btnAgregarPartida, gridBagConstraints);

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_START);

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

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Subtotal:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 3, 0);
        jPanel5.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Total con IVA");
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

        jPanel2.add(jPanel5, java.awt.BorderLayout.PAGE_END);

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
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotalIva;
    private javax.swing.JTable tblFactura;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtFolio;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
