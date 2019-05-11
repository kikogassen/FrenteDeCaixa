
import Classes.Conexao;
import Classes.Imprimir;
import Classes.Pagto;
import Classes.Vendedor;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class FechamentoCompra extends javax.swing.JFrame {

    DecimalFormat df = new DecimalFormat("0.00");
    Conexao con = new Conexao();
    Statement st;
    ResultSet rs;
    ResultSet rs2;
    int itens;
    double resultado;
    float dinheiro = 0;
    double ValorCompra;
    JTable tabela = new JTable();
    Imprimir i = new Imprimir();
    int setado = 0;
    LinkedList<Pagto> listaCombo;
    LinkedList<Vendedor> listaComboVendedor;
    boolean TemVendedor = false;

    public FechamentoCompra(double ValorCompra, int itens, JComponent tabela) {
        initComponents();
        ConfiguraESC();
        ConfiguraF8();
        ConfiguraF9();
        jTFValorCompra.setText(df.format(ValorCompra).replace(".", ","));
        this.ValorCompra = ValorCompra;
        EnterPraGerarTroco();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        this.itens = itens;
        this.tabela = (JTable) tabela;
        ConfiguraCloseOperation();
        setDinheiro();
        listaCombo = new LinkedList<>();
        listaComboVendedor = new LinkedList<>();
        ListaCombo();
        jTFDinheiro.requestFocus();
        jLVendedor.setVisible(false);
        jCBVendedor.setVisible(false);
        ListaVendedores();

    }

    private void ListaCombo() {
        try {
            rs = con.Select("SELECT nome_pgto, id_fc_forma_pgto FROM fc_forma_pgto WHERE sts_pgto = 1 AND tipo_pgto = 'C' ORDER BY nome_pgto DESC");
            jCBCartao.removeAllItems();
            listaCombo.clear();
            int contador = 1;
            jCBCartao.addItem("-- Selecione");
            Pagto p2 = new Pagto("-- Selecione", 1010);
            listaCombo.add(p2);
            while (rs.next()) {
                Pagto p = new Pagto(rs.getString("nome_pgto"), Integer.parseInt(rs.getString("id_fc_forma_pgto")));
                listaCombo.add(p);
                jCBCartao.addItem(listaCombo.get(contador).getNome());
                contador++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FechamentoCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    private static String geraChave() {
        int qtdeMaximaCaracteres = 8;
        String[] caracteres = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        StringBuilder senha = new StringBuilder();
        for (int i = 0; i < qtdeMaximaCaracteres; i++) {
            int posicao = (int) (Math.random() * caracteres.length);
            senha.append(caracteres[posicao]);
        }
        return senha.toString();
    }

    private void EnterPraGerarTroco() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (setado == 1) {
                    jBConfirmarActionPerformed(arg0);
                } else {
                    boolean testeRuim = false;
                    if (jTFDinheiro.getText().split(",").length > 1) {
                        try {
                            String[] dinheiroVetor = jTFDinheiro.getText().split(",");
                            dinheiro = Float.parseFloat(dinheiroVetor[0] + "." + dinheiroVetor[1]);
                        } catch (NumberFormatException n) {
                            JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                            jTFDinheiro.setText("");
                            jTFDinheiro.requestFocus();
                        }
                    } else {
                        try {
                            dinheiro = Float.parseFloat(jTFDinheiro.getText());
                        } catch (NumberFormatException n) {
                            JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                            jTFDinheiro.setText("");
                            jTFDinheiro.requestFocus();
                            testeRuim = true;
                        }
                    }
                    if (dinheiro >= Float.parseFloat(jTFValorCompra.getText().replace(",", "."))) {
                        resultado = dinheiro - Double.parseDouble(jTFValorCompra.getText().replace(",", "."));
                        jTFTroco.setText(df.format(resultado));
                        EnterPraImprimirEFechar();
                        jTFDinheiro.setFocusable(false);
                    } else {
                        if (testeRuim == false) {
                            JOptionPane.showMessageDialog(null, "Dinheiro menor que o total da compra", "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                            jTFDinheiro.setText("");
                            jTFDinheiro.requestFocus();
                        }
                    }
                }
            }
        });

    }

    private void ConfiguraF8() {
        InputMap inputMap10 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap10.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "forward10");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap10);
        this.getRootPane().getActionMap().put("forward10", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jTFDinheiro.requestFocus();
                jbDinheiroActionPerformed(arg0);
                jTFDinheiro.requestFocus();
            }
        });
    }

    private void ConfiguraF9() {
        InputMap inputMap9 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap9.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "forward9");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap9);
        this.getRootPane().getActionMap().put("forward9", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg1) {
                jCBCartao.requestFocus();
                jbCartaoActionPerformed(arg1);
                jCBCartao.requestFocus();
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

    private void EnterPraImprimirEFechar() {
        InputMap inputMap2 = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "forward2");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap2);
        this.getRootPane().getActionMap().put("forward2", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            private MouseEvent evt;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jBConfirmarActionPerformed(arg0);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTFValorCompra = new javax.swing.JTextField();
        jLTotalCompra = new javax.swing.JLabel();
        jBConfirmar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLCartao = new javax.swing.JLabel();
        jCBCartao = new javax.swing.JComboBox<>();
        jTFTroco = new javax.swing.JTextField();
        jLTroco = new javax.swing.JLabel();
        jTFDinheiro = new javax.swing.JTextField();
        jLDinheiro = new javax.swing.JLabel();
        jbCartao = new javax.swing.JButton();
        jbDinheiro = new javax.swing.JButton();
        jLVendedor = new javax.swing.JLabel();
        jCBVendedor = new javax.swing.JComboBox<>();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LIFES CREATIVE - Fechamento de Compra");
        setMinimumSize(new java.awt.Dimension(368, 364));
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jTFValorCompra.setFont(new java.awt.Font("Tahoma", 1, 25)); // NOI18N
        jTFValorCompra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFValorCompra.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTFValorCompra.setFocusable(false);

        jLTotalCompra.setText("Total Compra:");

        jBConfirmar.setText("Confirmar");
        jBConfirmar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfirmarActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLCartao.setText("Cartão");
        jPanel1.add(jLCartao, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 40, -1));

        jCBCartao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "Débito", "Crédito", "Banrisul" }));
        jCBCartao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jCBCartaoKeyTyped(evt);
            }
        });
        jPanel1.add(jCBCartao, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 285, 40));

        jTFTroco.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFTroco.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFTroco.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTFTroco.setFocusable(false);
        jPanel1.add(jTFTroco, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 285, 40));

        jLTroco.setText("Troco:");
        jPanel1.add(jLTroco, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 40, -1));

        jTFDinheiro.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTFDinheiro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTFDinheiro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTFDinheiroKeyTyped(evt);
            }
        });
        jPanel1.add(jTFDinheiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 285, 40));

        jLDinheiro.setText("Dinheiro: ");
        jPanel1.add(jLDinheiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 60, -1));

        jbCartao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cartao.png"))); // NOI18N
        jbCartao.setText("Cartão (F9)");
        jbCartao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbCartao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCartaoActionPerformed(evt);
            }
        });
        jPanel1.add(jbCartao, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 0, 140, -1));

        jbDinheiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/dinheiro.png"))); // NOI18N
        jbDinheiro.setText("Dinheiro (F8)");
        jbDinheiro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbDinheiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDinheiroActionPerformed(evt);
            }
        });
        jPanel1.add(jbDinheiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, -1));

        jLVendedor.setText("Vendedor:");

        jCBVendedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Selecione" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTFValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLTotalCompra)
                    .addComponent(jLVendedor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(jBConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jLTotalCompra)
                .addGap(6, 6, 6)
                .addComponent(jTFValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLVendedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCBVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    void setCartao() {
        jLDinheiro.setVisible(false);
        jLTroco.setVisible(false);
        jTFDinheiro.setVisible(false);
        jTFTroco.setVisible(false);
        jLCartao.setVisible(true);
        jCBCartao.setVisible(true);
        setado = 1;
    }

    private void setDinheiro() {
        jLDinheiro.setVisible(true);
        jLTroco.setVisible(true);
        jTFDinheiro.setVisible(true);
        jTFTroco.setVisible(true);
        jLCartao.setVisible(false);
        jCBCartao.setVisible(false);
        setado = 0;
    }
    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        // TODO add your handling code here:
        requestFocus();
        toFront();
    }//GEN-LAST:event_formWindowLostFocus

    private void jbDinheiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDinheiroActionPerformed
        jTFDinheiro.requestFocus();
        setDinheiro();
    }//GEN-LAST:event_jbDinheiroActionPerformed

    private void jTFDinheiroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTFDinheiroKeyTyped
        String caracteres = "0987654321,.";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }
    }//GEN-LAST:event_jTFDinheiroKeyTyped

    private void jbCartaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCartaoActionPerformed
        setCartao();
        jCBCartao.requestFocus();
    }//GEN-LAST:event_jbCartaoActionPerformed

    private void jBConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfirmarActionPerformed
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int vendedor = 0;
        // DINHEIRO
        boolean teste = true;
        if (setado == 0) {
            if (jTFTroco.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico", "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                jTFDinheiro.requestFocus();
            } else {
                long unixTime;
                DefaultTableModel val = (DefaultTableModel) tabela.getModel();
                try {
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                    unixTime = System.currentTimeMillis() / 1000L;
                    teste = true;
                    // insert movimento
                    if (TemVendedor) {

                        if (jCBVendedor.getSelectedItem().equals("-- Selecione")) {
                            teste = false;
                        } else {
                            for (int j = 0; j < listaComboVendedor.size(); j++) {
                                if (jCBVendedor.getSelectedItem().equals(listaComboVendedor.get(j).getNome())) {
                                    vendedor = listaComboVendedor.get(j).getId();
                                }
                            }
                            con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 1," + vendedor + "," + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 10, 'Venda Produtos', " + itens + ", " + ValorCompra + ", " + dinheiro + ", " + resultado + ", '" + unixTime + "', 0, 0)");
                        }
                    } else {
                        vendedor = 0;
                        con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, id_fc_forma_pgto, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", 1,0," + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 10, 'Venda Produtos', " + itens + ", " + ValorCompra + ", " + dinheiro + ", " + resultado + ", '" + unixTime + "', 0, 0)");
                    }
                    //
                    if (teste) {
                        rs = con.Select("SELECT id_fc_movimento FROM fc_movimento USE INDEX (id_fc_movimento) ORDER BY id_fc_movimento DESC LIMIT 1");
                        rs.first();

                        i.acionaGaveta();
                        
                        // impressao ticket
                        for (int x = 0; x < val.getRowCount(); x++) 
                        {
                            for (int y = 0; y < Integer.parseInt((String) val.getValueAt(x, 2)); y++) {
                                unixTime = System.currentTimeMillis() / 1000L;
                                String preco_produto = df.format(Double.parseDouble(((String) val.getValueAt(x, 3)).replace(",", ".")) / Double.parseDouble(((String) val.getValueAt(x, 2)))).replace(".", ",0");

                                rs2 = con.Select("SELECT impressao_produtos, ticket_produtos, imp_valor_produtos FROM fc_produtos USE INDEX (id_fc_produtos) WHERE id_fc_produtos = " + val.getValueAt(x, 0) + " LIMIT 1");
                                rs2.first();

                                int qtde_ticket = rs2.getInt("ticket_produtos");
                                String nome_impressao = rs2.getString("impressao_produtos");
                                preco_produto = preco_produto.replaceAll(",", ".");
                                double preco = Float.parseFloat(preco_produto) / qtde_ticket;
                                preco_produto = df.format(preco).replace(".", ",");
                                String imp_valor = rs2.getString("imp_valor_produtos");

                                for (int z = 0; z < qtde_ticket; z++) {
                                    String chave = geraChave() + unixTime;
                                    i.ImprimeTicket(TelaPrincipal.nome_eventoAtivo, nome_impressao, preco_produto, imp_valor, chave, TelaPrincipal.ImprimeTitulo, TelaPrincipal.Codabar, TelaPrincipal.pdvAtivo, vendedor);
                                    con.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos, id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, valor_movimento, imp_valor_movimento, chave_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.id_operadorAtivo + ", " + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + rs.getInt("id_fc_movimento") + ", " + val.getValueAt(x, 0) + "," + vendedor + ", '" + TelaPrincipal.pdvAtivo + "', '" + timeStamp + "', 10, '" + (String) val.getValueAt(x, 1) + "', '" + preco + "', '" + imp_valor + "', '" + chave + "', " + unixTime + ", 0, 0)");
                                }
                            }
                        }
                        // update caixa
                        con.Insert("UPDATE fc_movimento USE INDEX (id_fc_movimento) SET sts_movimento = 1 WHERE id_fc_movimento = " + rs.getInt("id_fc_movimento") + " LIMIT 1");
                        con.Insert("UPDATE fc_movimento_item USE INDEX (id_fc_movimento) SET sts_movimento = 1 WHERE id_fc_movimento = " + rs.getInt("id_fc_movimento"));
                        con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET saldo_caixa = (saldo_caixa + " + ValorCompra + ") WHERE id_fc_caixa=" + TelaPrincipal.Id_CaixaAberto + " LIMIT 1");

                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Não selecionou vendedor.", "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL " + ex, "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                    Logger
                        .getLogger(FechamentoCompra.class
                        .getName()).log(Level.SEVERE, null, ex);
                }
                if (teste) {
                    TelaPrincipal.FechouCompra = true;
                    TelaPrincipal.AparecerTela = true;
                    dispose();
                }
            }
            getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } 
        // CARTÃO
        else {
            //listaCombo.get(jCBCartao.getSelectedIndex()).getId()
            if (!jCBCartao.getSelectedItem().equals("SELECIONE")) {
                long unixTime;
                DefaultTableModel val = (DefaultTableModel) tabela.getModel();
                try {
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                    unixTime = System.currentTimeMillis() / 1000L;
                    teste = true;
                    // insert movimento
                    if (TemVendedor) {
                        if (jCBVendedor.getSelectedItem().equals("SELECIONE")) {
                            teste = false;
                        } else {
                            for (int j = 0; j < listaComboVendedor.size(); j++) {
                                if (jCBVendedor.getSelectedItem().equals(listaComboVendedor.get(j).getNome())) {
                                    vendedor = listaComboVendedor.get(j).getId();
                                }
                            }
                            con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento, id_fc_forma_pgto, id_fc_vendedor) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 10, 'Venda Produtos', " + itens + ", " + ValorCompra + ", " + dinheiro + ", " + resultado + ", '" + unixTime + "', 0, 0," + listaCombo.get(jCBCartao.getSelectedIndex()).getId() + ", " + vendedor + ")");
                        }
                    } else {
                        vendedor = 0;
                        con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, sts_movimento, integra_movimento, id_fc_forma_pgto, id_fc_vendedor) VALUES (" + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + TelaPrincipal.id_operadorAtivo + ", " + TelaPrincipal.pdvAtivo + ", '" + timeStamp + "', 10, 'Venda Produtos', " + itens + ", " + ValorCompra + ", " + dinheiro + ", " + resultado + ", '" + unixTime + "', 0, 0," + listaCombo.get(jCBCartao.getSelectedIndex()).getId() + ", 0)");
                    }
                    if (teste) {
                        rs = con.Select("SELECT id_fc_movimento FROM fc_movimento USE INDEX (id_fc_movimento) ORDER BY id_fc_movimento DESC LIMIT 1");
                        rs.first();

                        // impressao ticket
                        for (int x = 0; x < val.getRowCount(); x++) {
                            for (int y = 0; y < Integer.parseInt((String) val.getValueAt(x, 2)); y++) {
                                unixTime = System.currentTimeMillis() / 1000L;
                                String preco_produto = df.format(Double.parseDouble(((String) val.getValueAt(x, 3)).replace(",", ".")) / Double.parseDouble(((String) val.getValueAt(x, 2)))).replace(".", ",0");

                                rs2 = con.Select("SELECT impressao_produtos, ticket_produtos, imp_valor_produtos FROM fc_produtos USE INDEX (id_fc_produtos) WHERE id_fc_produtos = " + val.getValueAt(x, 0) + " LIMIT 1");
                                rs2.first();
                                int qtde_ticket = rs2.getInt("ticket_produtos");
                                String nome_impressao = rs2.getString("impressao_produtos");
                                preco_produto = preco_produto.replaceAll(",", ".");
                                double preco = Float.parseFloat(preco_produto) / qtde_ticket;
                                preco_produto = df.format(preco).replace(".", ",");
                                String imp_valor = rs2.getString("imp_valor_produtos");
                                for (int z = 0; z < qtde_ticket; z++) {
                                    String chave = geraChave() + unixTime;
                                    i.ImprimeTicket(TelaPrincipal.nome_eventoAtivo, nome_impressao, preco_produto, imp_valor, chave, TelaPrincipal.ImprimeTitulo, TelaPrincipal.Codabar, TelaPrincipal.pdvAtivo, vendedor);
                                    con.Insert("INSERT INTO fc_movimento_item (id_fc_operador, id_fc_caixa, id_fc_evento, id_fc_movimento, id_fc_produtos,id_fc_vendedor, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, valor_movimento, imp_valor_movimento, chave_movimento, mktime_movimento, sts_movimento, integra_movimento) VALUES (" + TelaPrincipal.id_operadorAtivo + ", " + TelaPrincipal.Id_CaixaAberto + ", " + TelaPrincipal.Id_eventoAtivo + ", " + rs.getInt("id_fc_movimento") + ", " + val.getValueAt(x, 0) + "," + vendedor + ", '" + TelaPrincipal.pdvAtivo + "', '" + timeStamp + "', 10, '" + (String) val.getValueAt(x, 1) + "', '" + preco + "', '" + imp_valor + "', '" + chave + "', " + unixTime + ", 0, 0)");
                                }
                            }
                        }
                        // update caixa
                        con.Insert("UPDATE fc_movimento USE INDEX (id_fc_movimento) SET sts_movimento = 1 WHERE id_fc_movimento = " + rs.getInt("id_fc_movimento") + " LIMIT 1");
                        con.Insert("UPDATE fc_movimento_item USE INDEX (id_fc_movimento) SET sts_movimento = 1 WHERE id_fc_movimento = " + rs.getInt("id_fc_movimento"));
                        con.Insert("UPDATE fc_caixa USE INDEX (id_fc_caixa) SET saldo_caixa = (saldo_caixa + " + ValorCompra + ") WHERE id_fc_caixa=" + TelaPrincipal.Id_CaixaAberto + " LIMIT 1");

                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Não selecionou vendedor.", "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro de MySQL " + ex, "LIFES CREATIVE - Fechamento de Compra", JOptionPane.ERROR_MESSAGE);
                    Logger
                            .getLogger(FechamentoCompra.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                if (teste) {
                    TelaPrincipal.FechouCompra = true;
                    TelaPrincipal.AparecerTela = true;
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Selecione algum cartão.", "Nenhum cartão selecionado", JOptionPane.ERROR_MESSAGE);
                jCBCartao.requestFocus();
            }
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jBConfirmarActionPerformed

    private void jCBCartaoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCBCartaoKeyTyped

    }//GEN-LAST:event_jCBCartaoKeyTyped
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBConfirmar;
    private javax.swing.JComboBox<String> jCBCartao;
    private javax.swing.JComboBox<String> jCBVendedor;
    private javax.swing.JLabel jLCartao;
    private javax.swing.JLabel jLDinheiro;
    private javax.swing.JLabel jLTotalCompra;
    private javax.swing.JLabel jLTroco;
    private javax.swing.JLabel jLVendedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTFDinheiro;
    private javax.swing.JTextField jTFTroco;
    private javax.swing.JTextField jTFValorCompra;
    private javax.swing.JButton jbCartao;
    private javax.swing.JButton jbDinheiro;
    // End of variables declaration//GEN-END:variables

    private void ListaVendedores() {
        try {
            rs = con.Select("SELECT vendedor FROM fc_config WHERE vendedor = 1 LIMIT 1");
            if (rs.first()) {
                TemVendedor = true;
                jLVendedor.setVisible(true);
                jCBVendedor.setVisible(true);
                rs2 = con.Select("SELECT id_fc_vendedor, nome_vendedor FROM fc_vendedor USE INDEX (id_fc_vendedor) WHERE status_vendedor = 1 ORDER BY nome_vendedor");
                while (rs2.next()) {
                    Vendedor v = new Vendedor(rs2.getInt("id_fc_vendedor"), rs2.getString("nome_vendedor"));
                    listaComboVendedor.add(v);
                    jCBVendedor.addItem(rs2.getString("nome_vendedor"));
                }
            } else {
                FechamentoCompra.this.setSize(305, 430);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FechamentoCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}