package com.mycompany.clientefacturas;

import java.awt.event.ActionEvent;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;

public class PantallaPrincipal extends javax.swing.JFrame {

    public PantallaPrincipal() {
        initComponents();
        btnConsultar.addActionListener(this::onButonConsultarClicked);
    }

    private void onButonConsultarClicked(ActionEvent evt) {
        String folio = getFolio();

        if (validar(folio)) {
            try {
                peticionGet();
            } catch (Exception e) {
                e.printStackTrace();
            }

            limpiarTxt();
        }
    }

    private void peticionGet() throws Exception {

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
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        String folio = jsonObject.getString("folio");
        String fechaExpedicion = jsonObject.getString("fecha_expedicion");
        Double subtotoal = jsonObject.getDouble("subtotal");
        Double total = jsonObject.getDouble("total");
        Integer clienteId = jsonObject.getInt("cliente_id");

        JSONArray jsonArrayp = new JSONArray(jsonObject.getJSONArray("partidas"));
        JSONObject jsonObjectp = jsonArrayp.getJSONObject(0);

    }

    private String getFolio() {
        if (txtFolio.getText().isEmpty()) {
            return "";
        } else {
            return txtFolio.getText();
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
        btnEliminar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFacturas = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblTotalArticulos = new javax.swing.JLabel();
        lblTotalNeto = new javax.swing.JLabel();

        jPanel1.setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout jPanel4Layout = new java.awt.GridBagLayout();
        jPanel4Layout.columnWidths = new int[] {0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0};
        jPanel4Layout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        jPanel4.setLayout(jPanel4Layout);

        jLabel1.setText("Folio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 100);
        jPanel4.add(txtFolio, gridBagConstraints);

        btnConsultar.setText("Consultar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        jPanel4.add(btnConsultar, gridBagConstraints);

        btnEliminar.setText("Crear");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(btnEliminar, gridBagConstraints);

        jButton1.setText("Eliminar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jButton1, gridBagConstraints);

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        tblFacturas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblFacturas);

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

        lblTotalArticulos.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel5.add(lblTotalArticulos, gridBagConstraints);

        lblTotalNeto.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel5.add(lblTotalNeto, gridBagConstraints);

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
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotalArticulos;
    private javax.swing.JLabel lblTotalNeto;
    private javax.swing.JTable tblFacturas;
    private javax.swing.JTextField txtFolio;
    // End of variables declaration//GEN-END:variables
}
