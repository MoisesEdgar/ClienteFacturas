package Pantallas;

import APIS.ClienteAPI;
import DTO.ClienteDTO;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JOptionPane;

public class Cliente extends javax.swing.JFrame {

    private final ClienteAPI clienteAPI = new ClienteAPI();

    public Cliente() {
        initComponents();
        btnAgregar.addActionListener(this::onButonAgregarClicked);
        
        txtNombre.addKeyListener( new TextFieldNombreKeyAdapter());
        txtTelefono.addKeyListener(new TextFieldTelefonoKeyAdapter());
        
    }

    private class TextFieldNombreKeyAdapter extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent evt) {
            char c = evt.getKeyChar();
            if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z')) && (c != ' ')) {
                evt.consume();
            }
        }
    }

    private class TextFieldTelefonoKeyAdapter extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt) {
            char c = evt.getKeyChar();
            if (c < '0' || c > '9') {
                evt.consume();
            }
        }
    }

    private void onButonAgregarClicked(ActionEvent evt) {

        try {
            if (validarDatosCliente()) {
                if (clienteExistente() == false) {
                    String nombre = txtNombre.getText();
                    String telefono = txtTelefono.getText();
                    String direccion = txtDireccion.getText();

                    clienteAPI.save(nombre, telefono, direccion);

                    List<ClienteDTO> clientes = clienteAPI.getAll();
                    String codigo = clientes.stream().
                            filter(cliente -> cliente.nombre.equalsIgnoreCase(nombre)).
                            map(cliente -> cliente.codigo).
                            findFirst().
                            orElse("");

                    JOptionPane.showMessageDialog(this, "Se agrego un nuevo cliente con el codigo: " + codigo);
                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "El nombre del cliente ya esta registrado");
                    txtNombre.setText("");
                    txtNombre.requestFocus();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
        }
    }

    private boolean clienteExistente() throws Exception {
        List<ClienteDTO> clientes = clienteAPI.getAll();
        boolean existenciaCliente = clientes.stream().
                anyMatch(cliente -> cliente.nombre.equalsIgnoreCase(txtNombre.getText()));

        return existenciaCliente;
    }

    private boolean validarDatosCliente() {

        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se especifico el nombre del cliente");
            txtNombre.setText("");
            txtNombre.requestFocus();
            return false;
        }

        if (txtTelefono.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se especifico el telefono del cliente");
            txtTelefono.setText("");
            txtTelefono.requestFocus();
            return false;
        }

        if (txtTelefono.getText().length() != 10) {
            JOptionPane.showMessageDialog(this, "Numero de telefono no valido");
            txtTelefono.setText("");
            txtTelefono.requestFocus();
            return false;
        }

        if (txtDireccion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se espesifico la direccion");
            txtDireccion.setText("");
            txtDireccion.requestFocus();
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel4 = new javax.swing.JPanel();
        txtTelefono = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 86;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 0, 0);
        jPanel4.add(txtTelefono, gridBagConstraints);

        btnAgregar.setText("Agregar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 38, 0);
        jPanel4.add(btnAgregar, gridBagConstraints);

        jLabel2.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(27, 36, 0, 0);
        jPanel4.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Telefono:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 36, 0, 0);
        jPanel4.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Direccion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 36, 0, 0);
        jPanel4.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 170;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 0, 0);
        jPanel4.add(txtDireccion, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 160;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 4, 0, 30);
        jPanel4.add(txtNombre, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Datos del Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 4, 0, 0);
        jPanel4.add(jLabel1, gridBagConstraints);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cliente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
