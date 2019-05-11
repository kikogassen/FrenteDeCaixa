
import Classes.Conexao;
import Classes.Imprimir;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class AberturaCaixa extends javax.swing.JFrame {

    Conexao con = new Conexao();
    ResultSet rs, rs2;
    double troco = 0;
    int id_evento = 0;
    String nome_evento = "";
    Imprimir i = new Imprimir();
    DecimalFormat df = new DecimalFormat("0.00");
    LinkedList<Integer> listaIds = new LinkedList<Integer>();

    public AberturaCaixa() throws SQLException {
        initComponents();
        jCBEvento.removeAllItems();
        jCBEvento.addItem("-- Selecione");
        rs = con.Select("SELECT data_evento, nome_evento, id_fc_evento FROM fc_evento USE INDEX (data_evento) WHERE status_evento = 1 ORDER BY data_evento DESC LIMIT 20");
        while (rs.next()) {
            String data[] = rs.getString("data_evento").split("-");
            String texto = data[2] + "/" + data[1] + "/" + data[0] + " - " + rs.getString("nome_evento");
            jCBEvento.addItem(texto);
            listaIds.add(rs.getInt("id_fc_evento"));
            System.out.println("Id adicionado: " + texto + rs.getInt("id_fc_evento"));
        }
        jCBEvento.requestFocus();
        EnterPraIrProTroco();
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

    private void EnterPraIrProTroco() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFTroco.requestFocus();
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

    public static void copyFile(File source, File destination) throws IOException {
        if (destination.exists()) {
            destination.delete();
        }
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen()) {
                sourceChannel.close();
            }
            if (destinationChannel != null && destinationChannel.isOpen()) {
                destinationChannel.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLEvento = new javax.swing.JLabel();
        jCBEvento = new javax.swing.JComboBox<>();
        jLTroco = new javax.swing.JLabel();
        jTFTroco = new javax.swing.JTextField();
        jBSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LIFES CREATIVE - Abertura de Caixa");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jLEvento.setText(" Evento:");

        jCBEvento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Selecione" }));
        jCBEvento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBEventoActionPerformed(evt);
            }
        });

        jLTroco.setText(" Valor Troco:");

        jTFTroco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTFTrocoFocusGained(evt);
            }
        });
        jTFTroco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTrocoActionPerformed(evt);
            }
        });
        jTFTroco.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTFTrocoKeyPressed(evt);
            }
        });

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
                            .addComponent(jLTroco)
                            .addComponent(jLEvento)
                            .addComponent(jCBEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTFTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLEvento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLTroco)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFTroco, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jBSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalvarActionPerformed
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (jCBEvento.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Por favor, selecione um evento", "LIFES CREATIVE - Abertura de Caixa", JOptionPane.ERROR_MESSAGE);
            jCBEvento.requestFocus();
            jTFTroco.setText("");
            EnterPraIrProTroco();
        } else {
            if (jTFTroco.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Por favor, informe um valor de troco", "LIFES CREATIVE - Abertura de Caixa", JOptionPane.ERROR_MESSAGE);
                jTFTroco.requestFocus();
            } else {
                try {
                    troco = Double.parseDouble(jTFTroco.getText().replace(",", "."));
                    String[] nome_do_evento = ((String) jCBEvento.getSelectedItem()).split("- ");
                    nome_evento = nome_do_evento[1];
                    id_evento = listaIds.get(jCBEvento.getSelectedIndex() - 1);
                    System.out.println("id do evento: " + id_evento);
                    try {
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        long unixTime = System.currentTimeMillis() / 1000L;

                        TelaPrincipal.Id_eventoAtivo = id_evento;
                        TelaPrincipal.nome_eventoAtivo = nome_evento;

                        con.Insert("INSERT INTO fc_caixa (id_fc_evento, id_fc_operador, pdv_caixa, abertura_caixa, fechamento_caixa, aberto_caixa, saldo_caixa, mktime_caixa, sts_caixa, integra_caixa, update_caixa) VALUES (" + id_evento + ", " + TelaPrincipal.id_operadorAtivo + ", " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', '0001-01-01 00:00:00', 0, " + troco + ", " + unixTime + ", 1, 0, 0)");
                        rs2 = con.Select("SELECT id_fc_caixa FROM fc_caixa USE INDEX (id_fc_caixa) ORDER BY id_fc_caixa DESC LIMIT 1");
                        rs2.first();

                        TelaPrincipal.Id_CaixaAberto = rs2.getInt("id_fc_caixa");
                        con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto,id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + rs2.getString("id_fc_caixa") + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ",1,0 ," + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 1, 'Abertura de Caixa', 0, " + troco + ", 0, 0, " + unixTime + ", 1, 0)");

                        System.out.println("debug5");

                        for (int x = 0; x < TelaPrincipal.QtAbertura; x++) {
                            i.ImprimeFuncao(id_evento, TelaPrincipal.nome_eventoAtivo, "ABERTURA CAIXA", df.format(troco), TelaPrincipal.OperadorAtivo, String.valueOf(String.format("%03d", TelaPrincipal.pdvAtivo)), TelaPrincipal.ImprimeTitulo);
                        }
                        rs = con.Select("SELECT imp_titulo_evento FROM fc_evento WHERE id_fc_evento = " + id_evento + " LIMIT 1");
                        if (rs.first()) {
                            TelaPrincipal.ImprimeTitulo = Integer.parseInt(rs.getString("imp_titulo_evento"));
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Erro de MySQL - Erro na hora de selecionar título.", "LIFES CREATIVE - Abertura de Caixa", HEIGHT);
                        }
                        TelaPrincipal.AbriuCaixa = true;
                        TelaPrincipal.AparecerTela = true;
                        TelaPrincipal.HoraAberturaCaixa = timeStamp;
                        dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Abertura de Caixa", JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex);
                    }
                } catch (NumberFormatException n) {
                    jTFTroco.setText("");
                    JOptionPane.showMessageDialog(null, "Por favor, informe um valor de troco válido", "LIFES CREATIVE - Abertura de Caixa", JOptionPane.ERROR_MESSAGE);
                    jTFTroco.requestFocus();
                }
            }
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jBSalvarActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        requestFocus();
        toFront();
    }//GEN-LAST:event_formWindowLostFocus

    private void jTFTrocoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTFTrocoFocusGained
        EnterPraSalvar();
    }//GEN-LAST:event_jTFTrocoFocusGained

    private void jCBEventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBEventoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBEventoActionPerformed

    private void jTFTrocoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTrocoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTrocoActionPerformed

    private void jTFTrocoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFTrocoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jBSalvar.doClick();
        }
    }//GEN-LAST:event_jTFTrocoKeyPressed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBSalvar;
    private javax.swing.JComboBox<String> jCBEvento;
    private javax.swing.JLabel jLEvento;
    private javax.swing.JLabel jLTroco;
    private javax.swing.JTextField jTFTroco;
    // End of variables declaration//GEN-END:variables
}
