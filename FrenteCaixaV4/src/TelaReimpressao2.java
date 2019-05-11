
import Classes.Conexao;
import Classes.Imprimir;
import Classes.Reimpressao;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class TelaReimpressao2 extends javax.swing.JFrame {

    DecimalFormat df = new DecimalFormat("0.00");
    Conexao con = new Conexao();
    ResultSet rs;
    Imprimir i = new Imprimir();
    static boolean PodeSair;
    String mov = "10", valor = "10";
    LinkedList<Reimpressao> lista;

    public TelaReimpressao2(String mov, String valor) {
        this.mov = mov;
        this.valor = valor;
        lista = new LinkedList<>();
        initComponents();
        jtTitulo.setText("Movimento " + mov);
        jTabela.getColumnModel().getColumn(0).setPreferredWidth(10);
        jTabela.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTabela.getColumnModel().getColumn(2).setPreferredWidth(200);
        jTabela.getColumnModel().getColumn(3).setPreferredWidth(25);
        ConfiguraESC();
        ConfiguraBACKSPACE();
        //jTabela.setShowGrid(false);
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        SetaTabela();
        ConfiguraCloseOperation();
        PodeSair = false;
        jbImprimir.setText("<html><div align=center> Imprimir <br> Todos</div></html>");
        jbImprimir.requestFocus();
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

    private void SetaTabela() {
        try {
            rs = con.Select("SELECT id_fc_movimento_item, datahora_movimento, descricao_movimento, chave_movimento, valor_movimento, id_fc_vendedor FROM fc_movimento_item WHERE id_fc_movimento = " + mov + " AND sts_movimento = 1 ORDER BY id_fc_movimento_item");
            int cont = 0;
            DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
            DecimalFormat format = new DecimalFormat("#,##0.00");
            while (rs.next()) {
                ResultSet rs2 = con.Select("SELECT chave_movimento FROM fc_movimento_item USE INDEX(chave_movimento) WHERE chave_movimento = '" + rs.getString("chave_movimento") + "' AND tipo_movimento = 50 LIMIT 1");
                if (!rs2.first()) {
                    Reimpressao r = new Reimpressao(rs.getString("datahora_movimento"), rs.getString("descricao_movimento"), rs.getString("chave_movimento"), rs.getString("id_fc_movimento_item"), rs.getString("valor_movimento"), rs.getInt("id_fc_vendedor"));
                    lista.add(r);
                    val.addRow(new String[]{lista.get(cont).getId(), lista.get(cont).getChave(), lista.get(cont).getDescricao(), format.format(Float.parseFloat(lista.get(cont).getValor()))});
                    cont++;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Reimpressão de Venda", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaReimpressao.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void ConfiguraBACKSPACE() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                voltarActionPerformed(arg0);
            }
        });
    }

    private void Reimprimir() throws SQLException {
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        int j = jTabela.getSelectedRow();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        long unixTime = System.currentTimeMillis() / 1000L;
        con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento, id_fc_vendedor) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 0, " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 11, 'Reimpressão Venda - Mov " + mov + "', 1," + valor.replace(",", ".") + ", 0, 0, '" + unixTime + "', 1, 0,"+lista.get(j).getFcVendedor()+")");
        rs = con.Select("SELECT nome_evento, impressao_produtos, valor_movimento, imp_valor_movimento, chave_movimento, id_fc_vendedor FROM fc_movimento_item i USE INDEX (id_fc_movimento_item) INNER JOIN fc_produtos p ON p.id_fc_produtos = i.id_fc_produtos INNER JOIN fc_evento e ON i.id_fc_evento = e.id_fc_evento WHERE id_fc_movimento_item = " + lista.get(j).getId());

        while (rs.next()) {
            String nome_evento = rs.getString("nome_evento");
            String nome_produto = rs.getString("impressao_produtos");
            String preco_produto = rs.getString("valor_movimento");
            preco_produto = df.format(Double.parseDouble(preco_produto.replace(",", "."))).replace(".", ",");
            String chave = rs.getString("chave_movimento");
            String imp_valor = rs.getString("imp_valor_movimento");

            i.ImprimeTicket(nome_evento, nome_produto, preco_produto, imp_valor, chave, TelaPrincipal.ImprimeTitulo, TelaPrincipal.Codabar, TelaPrincipal.pdvAtivo, rs.getInt("id_fc_vendedor"));
        }
    }

    private void ReimprimirTodos() throws SQLException {
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        long unixTime = System.currentTimeMillis() / 1000L;
        rs = con.Select("SELECT id_fc_vendedor FROM fc_movimento WHERE id_fc_movimento = "+mov+" LIMIT 1");
        rs.first();
        con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento, id_fc_vendedor) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 0, " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 11, 'Reimpressão Venda - Mov " + mov + "', " + lista.size() + ", " + valor.replace(",", ".") + ", 0, 0, '" + unixTime + "', 1, 0, "+rs.getInt("id_fc_vendedor")+")");
        rs = con.Select("SELECT nome_evento, impressao_produtos, valor_movimento, imp_valor_movimento, chave_movimento, id_fc_vendedor FROM fc_movimento_item i USE INDEX (id_fc_movimento) INNER JOIN fc_produtos p ON p.id_fc_produtos = i.id_fc_produtos INNER JOIN fc_evento e ON i.id_fc_evento = e.id_fc_evento WHERE id_fc_movimento = " + mov);

        while (rs.next()) {
            String nome_evento = rs.getString("nome_evento");
            String nome_produto = rs.getString("impressao_produtos");
            String preco_produto = rs.getString("valor_movimento");
            preco_produto = df.format(Double.parseDouble(preco_produto.replace(",", "."))).replace(".", ",");
            String chave = rs.getString("chave_movimento");
            String imp_valor = rs.getString("imp_valor_movimento");

            i.ImprimeTicket(nome_evento, nome_produto, preco_produto, imp_valor, chave, TelaPrincipal.ImprimeTitulo, TelaPrincipal.Codabar, TelaPrincipal.pdvAtivo, rs.getInt("id_fc_vendedor"));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtTitulo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabela = new javax.swing.JTable();
        jbImprimir = new javax.swing.JButton();
        voltar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LIFES CREATIVE - Reimpressão");
        setMinimumSize(new java.awt.Dimension(722, 290));
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jtTitulo.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jtTitulo.setText("Movimento 00");

        jScrollPane1.setPreferredSize(new java.awt.Dimension(700, 202));

        jTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Registro", "Chave", "Produto", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTabela.setRowHeight(25);
        jTabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTabelaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTabela);

        jbImprimir.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jbImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/bt3.png"))); // NOI18N
        jbImprimir.setText("Imprimir");
        jbImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbImprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbImprimirMouseClicked(evt);
            }
        });
        jbImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbImprimirActionPerformed(evt);
            }
        });

        voltar.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        voltar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/voltar.png"))); // NOI18N
        voltar.setText("Voltar");
        voltar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        voltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voltarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtTitulo)
                        .addGap(340, 340, 340)
                        .addComponent(voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jtTitulo)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
//new TelaReimpressao().setVisible(true);
    //      this.dispose();
    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        // TODO add your handling code here:
        if (PodeSair == false) {
            requestFocus();
            toFront();
        }
    }//GEN-LAST:event_formWindowLostFocus

    private void jbImprimirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbImprimirMouseClicked
        int escolha = JOptionPane.showConfirmDialog(rootPane, "Deseja imprimir todos os tickets do movimento " + mov + "?", "Imprimir Todos", JOptionPane.YES_NO_OPTION);
        if (escolha == JOptionPane.YES_OPTION) {
            try {
                ReimprimirTodos();
            } catch (SQLException ex) {
                Logger.getLogger(TelaReimpressao2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbImprimirMouseClicked

    private void jbImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbImprimirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbImprimirActionPerformed

    private void jTabelaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabelaMouseReleased
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        switch (jTabela.getSelectedRow()) {
            case -1:
                break;
            default:
                try {
                    Reimprimir();
                } catch (SQLException ex) {
                    Logger.getLogger(TelaReimpressao2.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jTabelaMouseReleased

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved

    }//GEN-LAST:event_formMouseMoved

    private void voltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voltarActionPerformed
        new TelaReimpressao().setVisible(true);
        dispose();
    }//GEN-LAST:event_voltarActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabela;
    private javax.swing.JButton jbImprimir;
    private javax.swing.JLabel jtTitulo;
    private javax.swing.JButton voltar;
    // End of variables declaration//GEN-END:variables
}
