
import Classes.ChecaVazio;
import Classes.Conexao;
import Classes.Cript;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class TelaConfServer extends javax.swing.JFrame {

    /**
     * Creates new form TelaConfServer
     */
    
    Conexao con = new Conexao();
    ResultSet rs;
    
    public TelaConfServer() {
        initComponents();
        URL url = this.getClass().getResource("/imagens/ícone.png");  
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);  
        this.setIconImage(iconeTitulo);
        Iniciador();
    }
    
    private void Iniciador(){
        try {
            rs=con.Select("SELECT banco_unico, host2, banco2, login2, senha2 FROM fc_config LIMIT 1");
            while (rs.next()){
                if (rs.getInt("banco_unico")==0){
                    Cript cripta = new Cript();
                    jRBSimServidor.setSelected(true);
                    HabilitaTFs();
                    jTFHost.setText(rs.getString("host2"));
                    jTFBD.setText(rs.getString("banco2"));
                    jTFUser.setText(cripta.descrip(rs.getString("login2")));
                    jPFSenha.setText(cripta.descrip(rs.getString("senha2")));
                } else {
                    jRBNaoServidor.setSelected(true);
                    DesabilitaTFs();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            JOptionPane.showMessageDialog(null, "Erro na criptografia dos dados", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
        }
        ConfiguraCloseOperation();
    }
    
    private void ConfiguraCloseOperation(){
        addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    dispose();
                    try {
                        new TelaLogin().setVisible(true);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(TelaConfServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
	);
    }
    
    private void EnterProBD(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFBD.requestFocus();
                EnterProUser();
            }
        });
    }
    
    private void EnterProUser(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFUser.requestFocus();
                EnterPraSenha();
            }
        });
    }
    
    private void EnterPraSenha(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jPFSenha.requestFocus();
                EnterPraSalvar();
            }
        });
    }
    
    private void EnterPraSalvar(){
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBSalvarActionPerformed(arg0);
            }
        });
    }
    
    private void HabilitaTFs(){
        jLabel1.setEnabled(true);
        jLabel2.setEnabled(true);
        jLabel3.setEnabled(true);
        jLabel4.setEnabled(true);
        jTFBD.setEnabled(true);
        jTFHost.setEnabled(true);
        jTFUser.setEnabled(true);
        jPFSenha.setEnabled(true);
        jTFHost.requestFocus();
    }
    
    private void DesabilitaTFs(){
        jLabel1.setEnabled(false);
        jLabel2.setEnabled(false);
        jLabel3.setEnabled(false);
        jLabel4.setEnabled(false);
        jTFBD.setEnabled(false);
        jTFHost.setEnabled(false);
        jTFUser.setEnabled(false);
        jPFSenha.setEnabled(false);
        jTFBD.setText("");
        jTFHost.setText("");
        jTFUser.setText("");
        jPFSenha.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bGServidor = new javax.swing.ButtonGroup();
        jLServidorRemoto = new javax.swing.JLabel();
        jRBSimServidor = new javax.swing.JRadioButton();
        jRBNaoServidor = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jTFHost = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFBD = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPFSenha = new javax.swing.JPasswordField();
        jBSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LC - Configuração do Server");
        setResizable(false);

        jLServidorRemoto.setText("Servidor Remoto?");

        bGServidor.add(jRBSimServidor);
        jRBSimServidor.setText("Sim");
        jRBSimServidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSimServidorActionPerformed(evt);
            }
        });

        bGServidor.add(jRBNaoServidor);
        jRBNaoServidor.setText("Não");
        jRBNaoServidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBNaoServidorActionPerformed(evt);
            }
        });

        jLabel1.setText("Host MySQL");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jTFHost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFHostFocusGained(evt);
            }
        });

        jLabel2.setText("Banco de dados");

        jTFBD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFBDFocusGained(evt);
            }
        });

        jLabel3.setText("User");

        jTFUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFUserFocusGained(evt);
            }
        });

        jLabel4.setText("Senha");

        jPFSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPFSenhaFocusGained(evt);
            }
        });

        jBSalvar.setText("Salvar");
        jBSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLServidorRemoto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jRBSimServidor)
                                .addGap(18, 18, 18)
                                .addComponent(jRBNaoServidor))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPFSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFHost, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFBD, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFUser, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(48, 48, 48))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLServidorRemoto, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRBSimServidor)
                    .addComponent(jRBNaoServidor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPFSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBSalvar)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jRBSimServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSimServidorActionPerformed
        // TODO add your handling code here:
        HabilitaTFs();
    }//GEN-LAST:event_jRBSimServidorActionPerformed

    private void jRBNaoServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBNaoServidorActionPerformed
        // TODO add your handling code here:
        DesabilitaTFs();
    }//GEN-LAST:event_jRBNaoServidorActionPerformed

    private void jTFHostFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFHostFocusGained
        // TODO add your handling code here:
        EnterProBD();
    }//GEN-LAST:event_jTFHostFocusGained

    private void jTFBDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFBDFocusGained
        // TODO add your handling code here:
        EnterProUser();
    }//GEN-LAST:event_jTFBDFocusGained

    private void jTFUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFUserFocusGained
        // TODO add your handling code here:
        EnterPraSenha();
    }//GEN-LAST:event_jTFUserFocusGained

    private void jPFSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPFSenhaFocusGained
        // TODO add your handling code here:
        EnterPraSalvar();
    }//GEN-LAST:event_jPFSenhaFocusGained

    private void jBSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalvarActionPerformed
        // TODO add your handling code here:
        String host=jTFHost.getText();
        String BD=jTFBD.getText();
        String user=jTFUser.getText();
        String senha= new String(jPFSenha.getPassword());
        if ((host.equals("") || BD.equals("") || user.equals("") || senha.equals("")) && jRBSimServidor.isSelected()){
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
            ChecaVazio cv = new ChecaVazio();
            cv.ChecaVazio(jPFSenha, senha);
            cv.ChecaVazio(jTFBD, BD);
            cv.ChecaVazio(jTFUser, user);
            cv.ChecaVazio(jTFHost, host);
        } else {
            if (jRBNaoServidor.isSelected()){
                try {
                    con.Insert("UPDATE fc_config SET banco_unico=1, host2='', banco2='', login2='', senha2='' LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (jRBSimServidor.isSelected()){
                try {
                    Cript cripta = new Cript();
                    user=cripta.crip(user);
                    senha=cripta.crip(senha);
                    con.Insert("UPDATE fc_config SET banco_unico=0, host2='"+host+"', banco2='"+BD+"', login2='"+user+"', senha2='"+senha+"' LIMIT 1");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(TelaConfiguracoes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
                    JOptionPane.showMessageDialog(null, "Erro na criptografia dos dados", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                new TelaLogin().setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Configuração do Server", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaConfServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose();
        }
    }//GEN-LAST:event_jBSalvarActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bGServidor;
    private javax.swing.JButton jBSalvar;
    private javax.swing.JLabel jLServidorRemoto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField jPFSenha;
    private javax.swing.JRadioButton jRBNaoServidor;
    private javax.swing.JRadioButton jRBSimServidor;
    private javax.swing.JTextField jTFBD;
    private javax.swing.JTextField jTFHost;
    private javax.swing.JTextField jTFUser;
    // End of variables declaration//GEN-END:variables
}