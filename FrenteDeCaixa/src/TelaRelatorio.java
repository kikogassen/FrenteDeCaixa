
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
import java.text.DateFormat;
import java.text.DecimalFormat;
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
import javax.swing.JTable;
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
 * @author User
 */
public class TelaRelatorio extends javax.swing.JFrame {

    /**
     * Creates new form TelaRelatorio
     */
    ResultSet rs, rs2;
    Conexao con = new Conexao();
    Imprimir i = new Imprimir();
    int linhas;
    DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat dateformatcerto = new SimpleDateFormat("dd/MM/yyyy");
    DecimalFormat df3 = new DecimalFormat("###,###,###,###,##0.00");
    JTable Ids = new JTable();
    
    public TelaRelatorio() {
        initComponents();
        URL url = this.getClass().getResource("/imagens/ícone.png");  
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(url);  
        this.setIconImage(iconeTitulo);
        ConfiguraCloseOperation();
        ConfiguraESC();
        jTabela.setShowGrid(false);
        Ids.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        AjeitaTabela();
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        String[] var1 = timeStamp.split("-");
        String mesAntigo, timeStampAntigo;
        if (var1[1].equals("01")){
            mesAntigo="12";
            timeStampAntigo = String.valueOf(Integer.parseInt(var1[2])-1)+"-"+mesAntigo+"-"+var1[0];
        } else {
            mesAntigo = String.valueOf(Integer.parseInt(var1[1])-1);
            timeStampAntigo = var1[2]+"-"+mesAntigo+"-"+var1[0];
        }
        if (Integer.parseInt(mesAntigo)>=1 && Integer.parseInt(mesAntigo)<=9){
            mesAntigo="0"+mesAntigo;
            timeStampAntigo = var1[2]+"-"+mesAntigo+"-"+var1[0];
        }
        String[] antigoVisual = timeStampAntigo.split("-");
        jFTFInicio.setText(antigoVisual[2]+antigoVisual[1]+antigoVisual[0]);
        jFTFFinal.setText(var1[0]+var1[1]+var1[2]);
        SetaTabela(var1[2]+"-"+var1[1]+"-"+var1[0], timeStampAntigo);
    }
    
    public boolean ComparaDatas (String vencimento, String atual){
        try {
            boolean valida=true;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date data1 = new Date(format.parse(vencimento).getTime());
            Date data2 = new Date(format.parse(atual).getTime());
            if (data1.after(data2)){
                valida=false;
                //System.out.println("Data: " + vencimento + " eh posterior à " + atual+" valida:"+valida);
            } else if (data1.before(data2)){
                valida=true;
                //System.out.println("Data: " + vencimento + " eh inferior à " + atual+" valida:"+valida);
            } else {
                valida=false;
                //System.out.println("Data: " + vencimento + " eh igual à " + atual+" valida:"+valida);
            }
            return valida;
        } catch (ParseException ex) {
            Logger.getLogger(TelaRelatorio.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Erro na validação da data", "LC - Relatórios", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
    }
    
    private void AjeitaTabela(){
        TableColumn col = jTabela.getColumnModel().getColumn(0);
        col.setPreferredWidth(100);
        col = jTabela.getColumnModel().getColumn(1);
        col.setPreferredWidth(100);
        col = jTabela.getColumnModel().getColumn(2);
        col.setPreferredWidth(80);
        col = jTabela.getColumnModel().getColumn(3);
        col.setPreferredWidth(80);
        col = jTabela.getColumnModel().getColumn(4);
        col.setPreferredWidth(40);
    }
    
    private void SetaTabela(String fim, String inicio){
        try {
            DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
            val.setRowCount(0);
            DecimalFormat df =  new DecimalFormat("0.00");
            rs=con.Select("SELECT caixa.id_fc_caixa AS id_fc_caixa, operador.nome_operador AS nome_operador, evento.nome_evento AS nome_evento, caixa.id_fc_evento, caixa.id_fc_operador, caixa.abertura_caixa AS abertura_caixa, caixa.fechamento_caixa AS fechamento_caixa, caixa.saldo_caixa AS saldo_caixa FROM fc_caixa AS caixa USE INDEX (id_fc_caixa) INNER JOIN fc_evento AS evento ON caixa.id_fc_evento=evento.id_fc_evento INNER JOIN fc_operador AS operador ON caixa.id_fc_operador=operador.id_fc_operador ORDER BY id_fc_caixa DESC");
            while(rs.next()){
                String aberturaVetor[] = rs.getString("abertura_caixa").split(" ");
                boolean valida = ComparaDatas(fim, aberturaVetor[0]);
                boolean valida2 = ComparaDatas(aberturaVetor[0], inicio);
                if (valida==false && valida2==false){
                    rs2=con.Select("SELECT SUM(valor_movimento) AS saldo FROM fc_movimento_item USE INDEX (id_fc_caixa) WHERE id_fc_caixa='"+rs.getString("id_fc_caixa")+"' AND tipo_movimento=10");
                    String saldo="";
                    while (rs2.next()){
                        saldo = df.format(rs2.getDouble("saldo")).replace(".",",");
                        saldo = df3.format(Double.parseDouble(saldo.replace(",",".")));
                    }
                    String nome_evento = rs.getString("nome_evento");
                    String id_fc_caixa = rs.getString("id_fc_caixa");
                    String nome_operador = rs.getString("nome_operador");
                    String abertura = rs.getString("abertura_caixa");
                    String fechamento = rs.getString("fechamento_caixa");
                    String aberturaArray[] = abertura.split(" ");
                    String dataAberturaArray[] = aberturaArray[0].split("-");
                    String dataAberturaCerta = dataAberturaArray[2]+"/"+dataAberturaArray[1]+"/"+dataAberturaArray[0];
                    String horaAberturaCerta = aberturaArray[1].replace(".0", "");
                    String aberturaCompleta=dataAberturaCerta+" - "+horaAberturaCerta;
                    String fechamentoArray[] = fechamento.split(" ");
                    String dataFechamentoArray[] = fechamentoArray[0].split("-");
                    String dataFechamentoCerta = dataFechamentoArray[2]+"/"+dataFechamentoArray[1]+"/"+dataFechamentoArray[0];
                    String horaFechamentoCerta = fechamentoArray[1].replace(".0", "");
                    String fechamentoCompleto=dataFechamentoCerta+" - "+horaFechamentoCerta;
                    if (fechamentoCompleto.equals("01/01/0001 - 00:00:00")){
                        fechamentoCompleto="";
                    }
                    DefaultTableModel val2 = (DefaultTableModel) Ids.getModel();
                    val2.addRow(new String[]{id_fc_caixa});
                    val.addRow(new String[]{nome_evento, nome_operador, aberturaCompleta, fechamentoCompleto, saldo});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Relatórios", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(TelaReimpressao.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    private void ImprimeRelatorioMovimentos() throws SQLException{
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        DefaultTableModel val2 = (DefaultTableModel) Ids.getModel();
        String evento = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 0));
        String operador = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 1));
        String abertura = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 2));
        String fechamento = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 3));
        String id = String.valueOf(val2.getValueAt(jTabela.getSelectedRow(), 0));
        //i.ImprimeRelatorioMovimentos(TelaPrincipal.img_eventoAtivo, TelaPrincipal.Id_eventoAtivo, evento, operador, id, abertura, fechamento);
    }
    
    private void ImprimeRelatorioVendas() throws SQLException{
        DefaultTableModel val = (DefaultTableModel) jTabela.getModel();
        DefaultTableModel val2 = (DefaultTableModel) Ids.getModel();
        String evento = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 0));
        String operador = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 1));
        String abertura = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 2));
        String fechamento = String.valueOf(val.getValueAt(jTabela.getSelectedRow(), 3));
        String id = String.valueOf(val2.getValueAt(jTabela.getSelectedRow(), 0));
        //i.ImprimeRelatorioVendas(TelaPrincipal.img_eventoAtivo, TelaPrincipal.Id_eventoAtivo, evento, operador, id, abertura, fechamento);
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
        jPanel1 = new javax.swing.JPanel();
        jFTFInicio = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jFTFFinal = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jBAtualizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("LC - Relatórios");
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
                "Evento", "Operador", "Abertura", "Fechamento", "Vendas"
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
        jTabela.setRowHeight(25);
        jTabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTabelaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTabela);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        try {
            jFTFInicio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel1.setText("Data Inicial:");

        try {
            jFTFFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel2.setText("Data Final:");

        jBAtualizar.setText("Atualizar");
        jBAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAtualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFTFInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFTFFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                .addComponent(jBAtualizar)
                .addGap(35, 35, 35))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jFTFInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFTFFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBAtualizar))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTabelaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabelaMouseReleased
        // TODO add your handling code here:
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        switch (jTabela.getSelectedRow()){
            case -1:
                break;
            default:
                //int resposta = JOptionPane.showConfirmDialog(null, "Deseja imprimir o relatório deste venda?", "LC - Relatórios", JOptionPane.YES_NO_OPTION);
                int resposta = JOptionPane.showOptionDialog(null, "Qual relatório você deseja imprimir?", "LC - Relatórios", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Vendas","Movimentos"}, null);
                if (resposta==0){
                    try {
                        ImprimeRelatorioVendas();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Relatórios", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(TelaReimpressao.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (resposta==1){
                    try {
                        ImprimeRelatorioMovimentos();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados", "LC - Relatórios", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(TelaReimpressao.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
        }
        getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jTabelaMouseReleased

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        // TODO add your handling code here:
        requestFocus();
        toFront();
    }//GEN-LAST:event_formWindowLostFocus

    private void jBAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAtualizarActionPerformed
        try {
            // TODO add your handling code here:
            Date DataInicio = dateformatcerto.parse(jFTFInicio.getText());
            Date DataFinal = dateformatcerto.parse(jFTFFinal.getText());
            int x=DataInicio.compareTo(DataFinal);
            String[] inicio = jFTFInicio.getText().split("/");
            String[] fim = jFTFFinal.getText().split("/");
            SetaTabela(fim[2]+"-"+fim[1]+"-"+fim[0], inicio[2]+"-"+inicio[1]+"-"+inicio[0]);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Informe datas válidas", "LC - Relatórios", JOptionPane.ERROR_MESSAGE);
            jFTFInicio.setText("");
            jFTFFinal.setText("");
        }
    }//GEN-LAST:event_jBAtualizarActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBAtualizar;
    private javax.swing.JFormattedTextField jFTFFinal;
    private javax.swing.JFormattedTextField jFTFInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabela;
    // End of variables declaration//GEN-END:variables
}
