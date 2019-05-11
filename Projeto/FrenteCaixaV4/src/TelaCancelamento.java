
import Classes.Cancelamento;
import Classes.Conexao;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class TelaCancelamento extends javax.swing.JFrame {

    Conexao con = new Conexao();
    ResultSet rs;
    ArrayList movimentos;
    Boolean ScrollVisible = false;

    public TelaCancelamento() {
        initComponents();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        jbCancelar.setText("<html><div align=center> Confirmar<br>Cancelamento (F1)</div></html>");
        jTChave.requestFocus();
        setLocationRelativeTo(null);
        ConfiguraCloseOperation();
        ConfiguraESC();
        ConfiguraF1();
        ConfiguraMAIS();
        ConfiguraENTER();
        ConfiguraSeta();
        jTabela.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTabela.getColumnModel().getColumn(1).setPreferredWidth(10);
        jTabela.getColumnModel().getColumn(2).setPreferredWidth(10);
        jTabela.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTabela.getColumnModel().getColumn(4).setPreferredWidth(10);
        movimentos = new ArrayList();
        jPainelScrollLista.setVisible(false);
    }

    private void ConfiguraCloseOperation() {
        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                TelaPrincipal.AparecerTela = true;
                TelaPrincipal.FechouCompra = true;
                dispose();
            }
        }
        );
    }

    private void ConfiguraF1() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jbCancelarActionPerformed(arg0);
            }
        });
    }

    private void ConfiguraENTER() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward4");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward4", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (ScrollVisible) {
                    jTChave.setText(jLista.getSelectedValue());
                    jPainelScrollLista.setVisible(false);
                    ScrollVisible = false;
                    jTChave.requestFocus();
                    jbAddActionPerformed(arg0);
                } else {
                    jbAddActionPerformed(arg0);
                }
            }
        });
    }

    private void ConfiguraSeta() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "forward5");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward5", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jLista.requestFocus();
                jLista.setSelectedIndex(0);
            }
        });
    }

    private void ConfiguraMAIS() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "forward3");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward3", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jbAddActionPerformed(arg0);
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

        jPainelScrollLista = new javax.swing.JScrollPane();
        jLista = new javax.swing.JList<>();
        jScroll = new javax.swing.JScrollPane();
        jTabela = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jTChave = new javax.swing.JTextField();
        jbAdd = new javax.swing.JButton();
        jbCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LIFES CREATIVE - Cancelamento de Ticket");
        setMinimumSize(new java.awt.Dimension(720, 290));
        setPreferredSize(new java.awt.Dimension(720, 290));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListaMouseReleased(evt);
            }
        });
        jPainelScrollLista.setViewportView(jLista);

        getContentPane().add(jPainelScrollLista, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 310, 70));

        jScroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollMouseClicked(evt);
            }
        });

        jTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Chave", "Data", "Hora", "Produto", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTabela.getTableHeader().setReorderingAllowed(false);
        jTabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabelaMouseClicked(evt);
            }
        });
        jScroll.setViewportView(jTabela);

        getContentPane().add(jScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 700, 210));

        jLabel1.setText("Chave:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jTChave.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTChave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTChaveKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTChaveKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTChaveKeyTyped(evt);
            }
        });
        getContentPane().add(jTChave, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 310, 32));

        jbAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bullet_add.png"))); // NOI18N
        jbAdd.setBorder(null);
        jbAdd.setBorderPainted(false);
        jbAdd.setContentAreaFilled(false);
        jbAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbAdd.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bullet_add_over.png"))); // NOI18N
        jbAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddActionPerformed(evt);
            }
        });
        getContentPane().add(jbAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 20, 30));

        jbCancelar.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jbCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancel.png"))); // NOI18N
        jbCancelar.setText("Cancelamento");
        jbCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelarActionPerformed(evt);
            }
        });
        getContentPane().add(jbCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, 150, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddActionPerformed
        if (!jTChave.getText().equals("")) {
            if (!TestaSeJaTem(jTChave.getText())) {
                try {

                    rs = con.Select("SELECT id_fc_movimento_item FROM fc_movimento_item USE INDEX (chave_movimento, tipo_movimento, sts_movimento) WHERE chave_movimento = '" + jTChave.getText() + "' AND tipo_movimento = 50 AND sts_movimento = 1 LIMIT 1");
                    if (rs.first()) {
                        JOptionPane.showMessageDialog(this, "Este ticket já foi cancelado", "LIFES CREATIVE - Cancelamento de Ticket", JOptionPane.ERROR_MESSAGE);
                    } else {
                        rs = con.Select("SELECT chave_movimento, datahora_movimento, descricao_movimento, valor_movimento, id_fc_movimento FROM fc_movimento_item USE INDEX (chave_movimento, tipo_movimento, sts_movimento) WHERE chave_movimento = '" + jTChave.getText() + "' AND tipo_movimento = 10 AND sts_movimento = 1 LIMIT 1");
                        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
                        DecimalFormat format = new DecimalFormat("#,##0.00");
                        if (rs.first()) {
                            movimentos.add(rs.getString("id_fc_movimento"));
                            String datahora = rs.getString("datahora_movimento");
                            String datahoraArray[] = datahora.split(" ");
                            String dataArray[] = datahoraArray[0].split("-");
                            String dataCerta = dataArray[2] + "/" + dataArray[1] + "/" + dataArray[0];
                            String horaCerta = datahoraArray[1].replace(".0", "");
                            val.addRow(new String[]{rs.getString("chave_movimento"), dataCerta, horaCerta, rs.getString("descricao_movimento"), String.valueOf(format.format(Float.parseFloat(rs.getString("valor_movimento"))))});
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(TelaCancelamento.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Este ticket já está na tabela", "LIFES CREATIVE - Cancelamento de Ticket", JOptionPane.ERROR_MESSAGE);
            }
            jTChave.setText("");
        } else {
            JOptionPane.showMessageDialog(rootPane, "Digite alguma chave", "LIFES CREATIVE - Cancelamento de Ticket", JOptionPane.ERROR_MESSAGE);
            jTChave.requestFocus();
        }

    }//GEN-LAST:event_jbAddActionPerformed

    private void jTChaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTChaveKeyPressed

    }//GEN-LAST:event_jTChaveKeyPressed

    private void jbCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelarActionPerformed
        jTabela.selectAll();
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        int[] x;
        x = jTabela.getSelectedRows();
        int teste = jTabela.getRowCount();
        if (teste == 0) {
            JOptionPane.showMessageDialog(rootPane, "Adicione algum ticket", "Tabela Vazia", JOptionPane.ERROR_MESSAGE);
        } else {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            long unixTime = System.currentTimeMillis() / 1000L;
            ResultSet rs2;

            try {
                float ValorCancelado = 0;
                con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento, id_fc_vendedor) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 1," + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 50, 'Cancelamento de Ticket', 0, '0', '0', '0', '" + unixTime + "', 0, 0,0)");
                rs = con.Select("SELECT id_fc_movimento FROM fc_movimento USE INDEX (id_fc_movimento) ORDER BY id_fc_movimento DESC LIMIT 1");
                rs.first();

                int i;
                for (i = 0; i < jTabela.getRowCount(); i++) {
                    rs2 = con.Select("SELECT id_fc_caixa, id_fc_evento, id_fc_produtos, pdv_movimento, descricao_movimento, valor_movimento, id_fc_vendedor FROM fc_movimento_item USE INDEX (chave_movimento ) WHERE chave_movimento = '" + val.getValueAt(x[i], 0) + "' LIMIT 1");
                    rs2.first();
                    con.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, valor_movimento, imp_valor_movimento, chave_movimento, mktime_movimento, sts_movimento, integra_movimento, id_fc_vendedor) VALUES (" + TelaPrincipal.id_operadorAtivo + ", " + rs2.getInt("id_fc_caixa") + ", " + rs2.getInt("id_fc_evento") + ", " + rs.getInt("id_fc_movimento") + ", " + rs2.getInt("id_fc_produtos") + ", " + rs2.getInt("pdv_movimento") + ", '" + timeStamp + "', 50, '# " + rs2.getString("descricao_movimento") + " | cancelamento', '-" + rs2.getString("valor_movimento") + "', '0', '" + val.getValueAt(x[i], 0) + "', " + unixTime + ", 0, 0,"+rs2.getString("id_fc_vendedor")+")");
                    ValorCancelado += Float.parseFloat(rs2.getString("valor_movimento"));
                }

                con.Insert("UPDATE fc_movimento USE INDEX (id_fc_movimento) SET itens_movimento = " + i + ", valor_movimento = '-" + ValorCancelado + "', sts_movimento = 1 WHERE id_fc_movimento = " + rs.getInt("id_fc_movimento") + " LIMIT 1");
                con.Insert("UPDATE fc_movimento_item USE INDEX (id_fc_movimento) SET sts_movimento = 1 WHERE id_fc_movimento = " + rs.getInt("id_fc_movimento"));
                con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET saldo_caixa = (saldo_caixa - " + ValorCancelado + ") WHERE id_fc_caixa=" + TelaPrincipal.Id_CaixaAberto + " LIMIT 1");

                val.setNumRows(0);
                movimentos.clear();

            } catch (SQLException ex) {
                Logger.getLogger(TelaCancelamento.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbCancelarActionPerformed

    private void jTChaveKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTChaveKeyTyped
        String caracteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }
    }//GEN-LAST:event_jTChaveKeyTyped

    private void jScrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollMouseClicked

    }//GEN-LAST:event_jScrollMouseClicked

    private void jTabelaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabelaMouseClicked
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        val.removeRow(jTabela.getSelectedRow());
        movimentos.clear();
    }//GEN-LAST:event_jTabelaMouseClicked

    private void jTChaveKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTChaveKeyReleased
        if (!jTChave.getText().equals("")) {
            if (jTChave.getText().length() > 3) {
                try {
                    jPainelScrollLista.setVisible(true);
                    ScrollVisible = true;
                    rs = con.Select("SELECT chave_movimento FROM fc_movimento_item USE INDEX(chave_movimento, tipo_movimento, sts_movimento) WHERE chave_movimento LIKE '" + jTChave.getText() + "%' AND tipo_movimento = 10 AND sts_movimento = 1");
                    DefaultListModel model = new DefaultListModel();
                    boolean teste = false;
                    while (rs.next()) {
                        this.jLista.setModel(model);
                        model.addElement(rs.getString("chave_movimento"));
                        teste = true;
                    }
                    if (!teste) {
                        this.jLista.setModel(model);
                        jPainelScrollLista.setVisible(false);
                        ScrollVisible = false;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(TelaCancelamento.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            jPainelScrollLista.setVisible(false);
            ScrollVisible = false;
        }
    }//GEN-LAST:event_jTChaveKeyReleased

    private void jListaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListaMouseReleased
        jTChave.setText(jLista.getSelectedValue());
        jPainelScrollLista.setVisible(false);
        ScrollVisible = false;
        jTChave.requestFocus();
    }//GEN-LAST:event_jListaMouseReleased
    Boolean TestaSeJaTem(String chave) {
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        Boolean teste = false;
        for (int i = 0; i < jTabela.getRowCount(); i++) {
            if (chave.equals(val.getValueAt(i, 0).toString())) {
                teste = true;
            }
        }
        return teste;
    }
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jLista;
    private javax.swing.JScrollPane jPainelScrollLista;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JTextField jTChave;
    private javax.swing.JTable jTabela;
    private javax.swing.JButton jbAdd;
    private javax.swing.JButton jbCancelar;
    // End of variables declaration//GEN-END:variables
}
