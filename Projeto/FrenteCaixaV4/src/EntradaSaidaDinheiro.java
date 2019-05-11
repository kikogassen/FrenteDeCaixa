
import Classes.Conexao;
import Classes.Imprimir;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class EntradaSaidaDinheiro extends javax.swing.JFrame {

    Conexao con = new Conexao();
    ResultSet rs;
    double valor = 0;
    Imprimir i = new Imprimir();
    DecimalFormat df = new DecimalFormat("0.00");

    public EntradaSaidaDinheiro() {
        initComponents();
        jCBMovimento.requestFocus();
        EnterPraIrProValor();
        ConfiguraESC();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        ConfiguraCloseOperation();
    }

    private void ConfiguraCloseOperation() {
        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                TelaPrincipal.AparecerTela = true;
                dispose();
            }
        }
        );
    }

    private void EnterPraIrProValor() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFValor.requestFocus();
                EnterPraSalvar();
            }
        });
    }

    private void EnterPraSalvar() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBSalvarActionPerformed(arg0);
            }
        });
    }

    private void ConfiguraESC() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                TelaPrincipal.AparecerTela = true;
                dispose();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCBMovimento = new javax.swing.JComboBox<>();
        jLMovimento = new javax.swing.JLabel();
        jTFValor = new javax.swing.JTextField();
        jLValor = new javax.swing.JLabel();
        jBSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LIFES CREATIVE - Movimento de Dinheiro");
        setMinimumSize(new java.awt.Dimension(355, 235));
        setPreferredSize(new java.awt.Dimension(355, 265));
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jCBMovimento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Selecione", "Sangria de Dinheiro", "Suprimento de Dinheiro" }));
        jCBMovimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMovimentoActionPerformed(evt);
            }
        });

        jLMovimento.setText(" Movimento:");

        jTFValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFValorFocusGained(evt);
            }
        });
        jTFValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFValorActionPerformed(evt);
            }
        });
        jTFValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFValorKeyPressed(evt);
            }
        });

        jLValor.setText(" Valor:");

        jBSalvar.setText("Confirmar");
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(42, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLValor)
                            .addComponent(jLMovimento)
                            .addComponent(jCBMovimento, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFValor, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLMovimento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBMovimento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLValor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFValor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalvarActionPerformed
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (jCBMovimento.getSelectedIndex() > 0) {
            try {
                valor = Double.parseDouble(jTFValor.getText().replace(",", "."));
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                if (valor > 0) {
                    switch (jCBMovimento.getSelectedIndex()) {
                        case 1:
                            if (TelaPrincipal.saldo_atual_dinheiro >= valor) {
                                long unixTime = System.currentTimeMillis() / 1000L;
                                try {
                                    
                                    con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 1, 0, " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 3, 'Sangria Caixa', 0, -" + valor + ", 0, 0, " + unixTime + ", 1, 0)");
                                    con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET saldo_caixa = (saldo_caixa - " + valor + ") WHERE id_fc_caixa=" + TelaPrincipal.Id_CaixaAberto + " LIMIT 1");

                                    for (int x = 0; x < TelaPrincipal.QtSangria; x++) {
                                        i.ImprimeFuncao(TelaPrincipal.Id_eventoAtivo, TelaPrincipal.nome_eventoAtivo, "SANGRIA CAIXA", "-" + df.format(valor), TelaPrincipal.OperadorAtivo, String.valueOf(String.format("%03d", TelaPrincipal.pdvAtivo)), TelaPrincipal.ImprimeTitulo);
                                    }

                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Entrada/Saída de Dinheiro", JOptionPane.ERROR_MESSAGE);
                                    Logger.getLogger(EntradaSaidaDinheiro.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                dispose();
                                TelaPrincipal.SangrouSuprimiu = true;
                                TelaPrincipal.AparecerTela = true;
                            } else {
                                JOptionPane.showMessageDialog(null, "Valor de sangria maior que o valor em caixa", "LIFES CREATIVE - Entrada/Saída de Dinheiro", JOptionPane.ERROR_MESSAGE);
                                jTFValor.setText("");
                                jTFValor.requestFocus();
                            }
                            break;
                        case 2:
                            try {
                                long unixTime = System.currentTimeMillis() / 1000L;
                                con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 1, 0, " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 5, 'Suprimento Caixa', 0, " + valor + ", 0, 0, " + unixTime + ", 1, 0)");
                                con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET saldo_caixa = (saldo_caixa + " + valor + ") WHERE id_fc_caixa=" + TelaPrincipal.Id_CaixaAberto + " LIMIT 1");

                                for (int x = 0; x < TelaPrincipal.QtSuprimento; x++) {
                                    i.ImprimeFuncao(TelaPrincipal.Id_eventoAtivo, TelaPrincipal.nome_eventoAtivo, "SUPRIMENTO CAIXA", df.format(valor), TelaPrincipal.OperadorAtivo, String.valueOf(String.format("%03d", TelaPrincipal.pdvAtivo)), TelaPrincipal.ImprimeTitulo);
                                }

                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Entrada/Saída de Dinheiro", JOptionPane.ERROR_MESSAGE);
                                Logger.getLogger(EntradaSaidaDinheiro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            dispose();
                            TelaPrincipal.SangrouSuprimiu = true;
                            TelaPrincipal.AparecerTela = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, informe um valor positivo", "LIFES CREATIVE - Entrada/Saída de Dinheiro", JOptionPane.ERROR_MESSAGE);
                    jTFValor.requestFocus();
                    jTFValor.setText("");
                }
            } catch (NumberFormatException n) {
                JOptionPane.showMessageDialog(null, "Por favor, informe um valor válido", "LIFES CREATIVE -  Entrada/Saída de Dinheiro", JOptionPane.ERROR_MESSAGE);
                jTFValor.requestFocus();
                jTFValor.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecione um movimento", "LIFES CREATIVE - Entrada/Saída de Dinheiro", JOptionPane.ERROR_MESSAGE);
            jCBMovimento.requestFocus();
            jTFValor.setText("");
            EnterPraIrProValor();
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jBSalvarActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        requestFocus();
        toFront();
    }//GEN-LAST:event_formWindowLostFocus

    private void jTFValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFValorFocusGained
        EnterPraSalvar();
    }//GEN-LAST:event_jTFValorFocusGained

    private void jCBMovimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMovimentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBMovimentoActionPerformed

    private void jTFValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFValorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFValorActionPerformed

    private void jTFValorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFValorKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jBSalvar.doClick();
        }
    }//GEN-LAST:event_jTFValorKeyPressed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBSalvar;
    private javax.swing.JComboBox<String> jCBMovimento;
    private javax.swing.JLabel jLMovimento;
    private javax.swing.JLabel jLValor;
    private javax.swing.JTextField jTFValor;
    // End of variables declaration//GEN-END:variables
}
