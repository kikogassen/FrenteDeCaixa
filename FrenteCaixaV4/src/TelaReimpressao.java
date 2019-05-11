
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
import java.text.DecimalFormat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class TelaReimpressao extends javax.swing.JFrame {

    DecimalFormat df = new DecimalFormat("0.00");
    Conexao con = new Conexao();
    ResultSet rs;
    Imprimir i = new Imprimir();
    static boolean PodeSair;

    public TelaReimpressao() {
        initComponents();
        AjeitaTabela();
        ConfiguraESC();
        jTabela.setShowGrid(false);
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        URL url = this.getClass().getResource("/imagens/icone.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);
        this.setIconImage(iconeTitulo);
        SetaTabela();
        ConfiguraCloseOperation();
        PodeSair = false;
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
            rs = con.Select("SELECT id_fc_movimento, datahora_movimento, itens_movimento, valor_movimento FROM fc_movimento USE INDEX (tipo_movimento) WHERE tipo_movimento = 10 AND id_fc_caixa = '" + TelaPrincipal.Id_CaixaAberto + "' ORDER BY id_fc_movimento DESC LIMIT 6");
            while (rs.next()) {
                String id_fc_movimento = rs.getString("id_fc_movimento");
                String quant = rs.getString("itens_movimento");
                String valor = rs.getString("valor_movimento").replace(".", ",");
                String datahora = rs.getString("datahora_movimento");
                String datahoraArray[] = datahora.split(" ");
                String dataArray[] = datahoraArray[0].split("-");
                String dataCerta = dataArray[2] + "/" + dataArray[1] + "/" + dataArray[0];
                String horaCerta = datahoraArray[1].replace(".0", "");
                DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
                val.addRow(new String[]{id_fc_movimento, dataCerta, horaCerta, quant, valor});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de MySQL", "LIFES CREATIVE - Reimpressão de Venda", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaReimpressao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void AjeitaTabela() {
        TableColumn col = jTabela.getColumnModel().getColumn(0);
        col.setPreferredWidth(50);
        col = jTabela.getColumnModel().getColumn(1);
        col.setPreferredWidth(50);
        col = jTabela.getColumnModel().getColumn(2);
        col.setPreferredWidth(50);
        col = jTabela.getColumnModel().getColumn(3);
        col.setPreferredWidth(50);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTabela = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LIFES CREATIVE - Reimpressão");
        setMinimumSize(new java.awt.Dimension(722, 290));
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jScrollPane1.setPreferredSize(new java.awt.Dimension(700, 202));

        jTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Movimento", "Data", "Hora", "Quant. Ticket", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        if (jTabela.getColumnModel().getColumnCount() > 0) {
            jTabela.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTabela.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTabela.getColumnModel().getColumn(2).setPreferredWidth(60);
            jTabela.getColumnModel().getColumn(3).setPreferredWidth(40);
            jTabela.getColumnModel().getColumn(4).setPreferredWidth(60);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Ultimos Movimentos:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(489, 489, 489))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        // TODO add your handling code here:
        if (PodeSair == false) {
            requestFocus();
            toFront();
        }
    }//GEN-LAST:event_formWindowLostFocus

    private void jTabelaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabelaMouseReleased
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        new TelaReimpressao2((String) val.getValueAt(jTabela.getSelectedRow(), 0), (String) val.getValueAt(jTabela.getSelectedRow(), 4)).setVisible(true);
        dispose();
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jTabelaMouseReleased

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabela;
    // End of variables declaration//GEN-END:variables
}
