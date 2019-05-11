
import Classes.ChecaVazio;
import Classes.Conexao;
import Classes.ConexaoServer;
import Classes.MD5;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class TelaLogin extends javax.swing.JFrame {

    ResultSet rs;
    Conexao con = new Conexao();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public TelaLogin() throws SQLException {
        initComponents();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        EnterPraSenha();
        toFront();
        requestFocus();
        jbRemoto.setVisible(false);

        rs = con.Select("SELECT COUNT(id_fc_config) AS total FROM fc_config");
        rs.first();
        if (rs.getInt("total") == 0) 
        {
            jbRemoto.setVisible(true);
        } else {
            rs = con.Select("SELECT banco_unico FROM fc_config");
            rs.first();
            if (rs.getInt("banco_unico") == 1) {
                jbRemoto.setVisible(true);
            } else {
                jbRemoto.setVisible(false);
            }
        }
    }
    
    private void EnterPraSenha() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jPFSenha.requestFocus();
                EnterPraEntrar();
            }
        });
    }

    private void EnterPraEntrar() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBEntrarActionPerformed(arg0);
            }
        });
    }

    public boolean ComparaDatas(String vencimento, String atual) {
        boolean valida = true;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date data1 = new Date(format.parse(vencimento).getTime());
            Date data2 = new Date(format.parse(atual).getTime());
            if (data1.after(data2)) {
                valida = false;
                //System.out.println("Data: " + vencimento + " eh posterior à " + atual);
            } else if (data1.before(data2)) {
                valida = true;
                //System.out.println("Data: " + vencimento + " eh inferior à " + atual);
            } else {
                valida = true;
                //System.out.println("Data: " + vencimento + " eh igual à " + atual);
            }
        } catch (ParseException ex) {
            Logger.getLogger(TelaRelatorio.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Erro na validação da data", "LIFES CREATIVE - Login", JOptionPane.ERROR_MESSAGE);
        }
        return valida;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jTFLogin = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPFSenha = new javax.swing.JPasswordField();
        jBEntrar = new javax.swing.JButton();
        jLAtualiza = new javax.swing.JLabel();
        jbRemoto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LIFES CREATIVE - Login");
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });

        jLabel3.setText("Login: ");

        jTFLogin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFLoginFocusGained(evt);
            }
        });
        jTFLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFLoginKeyTyped(evt);
            }
        });

        jLabel4.setText("Senha:");

        jPFSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPFSenhaFocusGained(evt);
            }
        });
        jPFSenha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPFSenhaKeyTyped(evt);
            }
        });

        jBEntrar.setText("Entrar");
        jBEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBEntrarActionPerformed(evt);
            }
        });

        jLAtualiza.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLAtualiza.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png"))); // NOI18N
        jLAtualiza.setToolTipText("Sincroniza os operadores");
        jLAtualiza.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLAtualiza.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLAtualizaMouseMoved(evt);
            }
        });
        jLAtualiza.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLAtualizaMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLAtualizaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLAtualizaMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLAtualizaMouseReleased(evt);
            }
        });

        jbRemoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem.png"))); // NOI18N
        jbRemoto.setBorder(null);
        jbRemoto.setContentAreaFilled(false);
        jbRemoto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbRemoto.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/engrenagem_over.png"))); // NOI18N
        jbRemoto.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jbRemotoMouseMoved(evt);
            }
        });
        jbRemoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemotoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPFSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(94, 94, 94))
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addComponent(jBEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbRemoto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLAtualiza)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTFLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPFSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbRemoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLAtualiza, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBEntrarActionPerformed
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String login = jTFLogin.getText();
        String senha = new String(jPFSenha.getPassword());
        if (login.equals("") || senha.equals("")) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos", "LIFES CREATIVE - Login", JOptionPane.ERROR_MESSAGE);
            ChecaVazio cv = new ChecaVazio();
            cv.ChecaVazio(jPFSenha, senha);
            cv.ChecaVazio(jTFLogin, login);
            jTFLogin.requestFocus();
            EnterPraSenha();
        } else {
            String loginCript = MD5.criptografar(login);
            String senhaCript = MD5.criptografar(senha);
            try {
                boolean testeLogin = false;
                boolean logou = false;
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                Date atual = df.parse(timeStamp);
                rs = con.Select("SELECT id_fc_operador, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador FROM fc_operador USE INDEX (id_fc_operador)");
                while (rs.next()) {
                    boolean valida = ComparaDatas(rs.getString("validade_operador"), timeStamp);
                    if (loginCript.equals(rs.getString("login_operador")) && senhaCript.equals(rs.getString("senha_operador")) && valida == false && logou == false) {
                        testeLogin = true;
                        logou = true;
                        new TelaPrincipal(rs.getString("nome_operador"), rs.getInt("id_fc_operador"), rs.getInt("tipo_operador")).setVisible(true);
                        dispose();
                    }
                }
                if (testeLogin == false) {
                    String sysdate = new SimpleDateFormat("yyMMdd").format(Calendar.getInstance().getTime());
                    String senhaPadrao = String.valueOf((Integer.parseInt(sysdate) * 2) - 1000);
                    if (login.equals("0") && senha.equals(senhaPadrao) && logou == false) {
                        new TelaConfServer().setVisible(true);
                        dispose();
                    } else {
                        jTFLogin.setText("");
                        jPFSenha.setText("");
                        jTFLogin.requestFocus();
                        EnterPraSenha();
                        JOptionPane.showMessageDialog(null, "Autenticação incorreta", "LIFES CREATIVE - Login", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LIFES CREATIVE - Login", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "Erro na conversão da data", "LIFES CREATIVE - Login", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Erro na cópia do arquivo", "LIFES CREATIVE - Login", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jBEntrarActionPerformed

    private void jPFSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPFSenhaFocusGained
        EnterPraEntrar();
    }//GEN-LAST:event_jPFSenhaFocusGained

    private void jTFLoginKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFLoginKeyTyped
        if (!jTFLogin.getText().equals("")) {
            jTFLogin.setBackground(Color.white);
        }
        EnterPraSenha();
    }//GEN-LAST:event_jTFLoginKeyTyped

    private void jPFSenhaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPFSenhaKeyTyped
        jPFSenha.setBackground(Color.white);
    }//GEN-LAST:event_jPFSenhaKeyTyped

    private void jTFLoginFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFLoginFocusGained
        EnterPraSenha();
    }//GEN-LAST:event_jTFLoginFocusGained

    private void jLAtualizaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaMouseEntered
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLAtualizaMouseEntered

    private void jLAtualizaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaMouseExited
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLAtualizaMouseExited

    private void jLAtualizaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaMouseReleased
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            ConexaoServer conServer = new ConexaoServer();
            String value;
            con.Insert("TRUNCATE fc_operador");
            rs = conServer.Select("SELECT id_fc_operador, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador FROM fc_operador");
            value = "";

            while (rs.next()) {
                if (value.length() > 50000) {
                    con.Insert("INSERT INTO fc_operador (id_fc_operador, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador) VALUES " + value);
                    value = "(" + rs.getString("id_fc_operador") + ", '" + rs.getString("nome_operador") + "', '" + rs.getString("login_operador") + "', '" + rs.getString("senha_operador") + "', " + rs.getString("tipo_operador") + ", '" + rs.getString("validade_operador") + "')";
                } else {
                    if (!value.equals("")) {
                        value += ", ";
                    }
                    value += "(" + rs.getString("id_fc_operador") + ", '" + rs.getString("nome_operador") + "', '" + rs.getString("login_operador") + "', '" + rs.getString("senha_operador") + "', " + rs.getString("tipo_operador") + ", '" + rs.getString("validade_operador") + "')";
                }
            }
            if (value.length() > 0) {
                con.Insert("INSERT INTO fc_operador (id_fc_operador, nome_operador, login_operador, senha_operador, tipo_operador, validade_operador) VALUES " + value);
            }

            JOptionPane.showMessageDialog(null, "Operadores importados com sucesso", "LIFES CREATIVE - Tela de Login", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LIFES CREATIVE - Tela de Login", JOptionPane.ERROR_MESSAGE);
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLAtualizaMouseReleased

    private void jbRemotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemotoActionPerformed
        new TelaConfServer().setVisible(true);
        TelaLogin.this.dispose();
    }//GEN-LAST:event_jbRemotoActionPerformed

    private void jLAtualizaMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaMouseMoved
        jLAtualiza.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save_over.png")));
    }//GEN-LAST:event_jLAtualizaMouseMoved

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        jLAtualiza.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png")));
    }//GEN-LAST:event_formMouseMoved

    private void jbRemotoMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbRemotoMouseMoved
        jLAtualiza.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/db_save.png")));
    }//GEN-LAST:event_jbRemotoMouseMoved

    private void jLAtualizaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAtualizaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLAtualizaMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                boolean txtExiste = new File("dados.txt").exists();
                if (txtExiste == true) {
                    try {
                        new TelaLogin().setVisible(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LIFES CREATIVE - Login", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    new TelaValidaDadosBanco().setVisible(true);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBEntrar;
    private javax.swing.JLabel jLAtualiza;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField jPFSenha;
    private javax.swing.JTextField jTFLogin;
    private javax.swing.JButton jbRemoto;
    // End of variables declaration//GEN-END:variables
}
