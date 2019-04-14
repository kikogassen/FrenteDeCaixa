
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USER
 */
public class TelaReimpressao extends javax.swing.JFrame {

    /**
     * Creates new form TelaReimpressao
     */
    
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
        URL url = this.getClass().getResource("/imagens/ícone.png");  
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);  
        this.setIconImage(iconeTitulo);
        SetaTabela();
        ConfiguraCloseOperation();
        PodeSair=false;
    }
    
    private void ConfiguraCloseOperation(){
        addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    TelaPrincipal.AparecerTela=true;
                    dispose();
                }
            }
	);
    }
    
    private void SetaTabela(){
        try {
            rs=con.Select("SELECT datahora_movimento, itens_movimento, valor_movimento FROM fc_movimento USE INDEX (tipo_movimento) WHERE tipo_movimento=10 AND id_fc_caixa='"+TelaPrincipal.Id_CaixaAberto+"' ORDER BY id_fc_movimento DESC LIMIT 5");
            while(rs.next()){
                String datahora = rs.getString("datahora_movimento");
                String quant = rs.getString("itens_movimento");
                String valor = rs.getString("valor_movimento").replace(".", ",");
                String datahoraArray[] = datahora.split(" ");
                String dataArray[] = datahoraArray[0].split("-");
                String dataCerta = dataArray[2]+"/"+dataArray[1]+"/"+dataArray[0];
                String horaCerta = datahoraArray[1].replace(".0", "");
                DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
                val.addRow(new String[]{dataCerta, horaCerta, quant, valor});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Reimpressão de Venda", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaReimpressao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void AjeitaTabela(){
        TableColumn col = jTabela.getColumnModel().getColumn(0);
        col.setPreferredWidth(50);
        col = jTabela.getColumnModel().getColumn(1);
        col.setPreferredWidth(50);
        col = jTabela.getColumnModel().getColumn(2);
        col.setPreferredWidth(50);
        col = jTabela.getColumnModel().getColumn(3);
        col.setPreferredWidth(50);
    }
    
    private void ConfiguraESC(){
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"forward");
        this.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        this.getRootPane().getActionMap().put("forward", new AbstractAction(){
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent arg0) {
                TelaPrincipal.AparecerTela=true;
                dispose();
            }
        });
    }
    
    private static String geraStringAleatoria() {
        int qtdeMaximaCaracteres = 9;
        String[] caracteres = { "0", "1", "b", "2", "4", "5", "6", "7", "8",
                "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
                "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z" };
       
        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < qtdeMaximaCaracteres; i++) {
            int posicao = (int) (Math.random() * caracteres.length);
            senha.append(caracteres[posicao]);
        }
        return senha.toString();
    }
    
    private void Reimprimir() throws SQLException{
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        long unixTime = System.currentTimeMillis() / 1000L;
        con.Insert("INSERT INTO fc_movimento (id_fc_caixa, id_fc_evento, id_fc_operador, pdv_movimento, datahora_movimento, tipo_movimento, descricao_movimento, itens_movimento, valor_movimento, dinheiro_movimento, troco_movimento, mktime_movimento, integra_movimento) VALUES ("+TelaPrincipal.Id_CaixaAberto+", "+TelaPrincipal.Id_eventoAtivo+", "+TelaPrincipal.id_operadorAtivo+", "+TelaPrincipal.pdvAtivo+", '"+timeStamp+"', 11, 'Reimpressão Venda', "+val.getValueAt(jTabela.getSelectedRow(), 2)+", "+((String)val.getValueAt(jTabela.getSelectedRow(), 3)).replace(",", ".")+", 0, 0, '"+unixTime+"', 0)");
        int id_fc_movimento = 0;
        String data_bruta[] = ((String) val.getValueAt(jTabela.getSelectedRow(), 0)).split("/");
        int id_fc_evento = 0;
        String datahora_movimento = data_bruta[2]+"-"+data_bruta[1]+"-"+data_bruta[0]+" "+((String) val.getValueAt(jTabela.getSelectedRow(), 1));
        rs=con.Select("SELECT id_fc_movimento, id_fc_evento FROM fc_movimento USE INDEX (tipo_movimento) WHERE descricao_movimento='Venda Produtos' AND tipo_movimento=10 AND datahora_movimento='"+datahora_movimento+"'");
        while (rs.next()){
            id_fc_movimento=rs.getInt("id_fc_movimento");
            id_fc_evento=rs.getInt("id_fc_evento");
        }
        String nome_evento="";
        rs=con.Select("SELECT nome_evento FROM fc_evento USE INDEX (id_fc_evento) WHERE id_fc_evento="+id_fc_evento);
        while (rs.next()){
            nome_evento = rs.getString("nome_evento");
        }
        rs=con.Select("SELECT descricao_movimento, valor_movimento FROM fc_movimento_item USE INDEX (id_fc_movimento) WHERE id_fc_movimento="+id_fc_movimento);
        while (rs.next()){
            unixTime = System.currentTimeMillis() / 1000L;
            String chave = geraStringAleatoria();
            String nome_produto = rs.getString("descricao_movimento");
            String preco_produto = rs.getString("valor_movimento");
            //i.ImprimeTicket(TelaPrincipal.img_eventoAtivo, nome_evento, nome_produto, preco_produto, chave+unixTime, TelaPrincipal.Id_eventoAtivo);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTabela = new javax.swing.JTable();
        jLTitulo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LC - Reimpressão");
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "Hora", "Quant. Itens", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTabela.setRowHeight(25);
        jTabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTabelaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTabela);

        jLTitulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLTitulo.setText("Últimas 5 vendas:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                    .addComponent(jLTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        // TODO add your handling code here:
        if (PodeSair==false){
            requestFocus();
            toFront();
        }
    }//GEN-LAST:event_formWindowLostFocus

    private void jTabelaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabelaMouseReleased
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        switch (jTabela.getSelectedRow()){
            case -1:
                break;
            default:
                int resposta = JOptionPane.showConfirmDialog(null, "Deseja reimprimir a venda?", "LC - Reimpressão de Venda", JOptionPane.YES_NO_OPTION);
                if (resposta==JOptionPane.YES_OPTION){
                    try {
                        Reimprimir();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Reimpressão de Venda", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(TelaReimpressao.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jTabelaMouseReleased

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLTitulo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabela;
    // End of variables declaration//GEN-END:variables
}
