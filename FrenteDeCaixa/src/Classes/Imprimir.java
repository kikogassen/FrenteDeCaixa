package Classes;

//import Epson.InterfaceEpsonNF.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;


public class Imprimir {
        /*
	private static final long serialVersionUID = 1L;
	
	InterfaceEpsonNF interfaceEpsonNF = new InterfaceEpsonNF();
        Conexao con = new Conexao();
        ResultSet rs, rs2, rs3;
	
	public int iRetorno;
        DecimalFormat df =  new DecimalFormat("0");
        DecimalFormat df2 =  new DecimalFormat("0.00");
	
	
	public Imprimir(){
            vExecIniciaPorta();
            vExecConfiguraCodigoBarras();
	}
	
        public void ImprimeTicket(String img_evento, String nome_evento, String nome_produto, String preco_produto, String chave_produto, int id_evento) throws SQLException {
            
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
            
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp>"+img_evento+"</bmp></ce><ce>");
            
            rs=con.Select("SELECT imprime_titulo FROM fc_config");
            while (rs.next()){
                if (rs.getInt("imprime_titulo")==1){
                    String[] evento = nome_evento.split("");
                    if (evento.length>20){
                        boolean achei=false;
                        for (int x=20;x>0;x--){
                            if (evento[x].equals(" ") && achei==false){
                                achei=true;
                                String msg="";
                                for (int y=0;y<x;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                                msg="";
                                for (int y=x;y<evento.length;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            }
                        }
                        if (achei==false){
                            String msg="";
                            int metade = Integer.parseInt(df.format(evento.length/2));
                            for (int x=0;x<metade;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            msg="";
                            for (int x=metade;x<evento.length;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                        }
                    } else {
                        iRetorno = interfaceEpsonNF.FormataTX(nome_evento+"\n", 2, 1, 0, 1, 1);
                    }
                }
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>");
            iRetorno = interfaceEpsonNF.FormataTX(timeStamp+"\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
            String[] produto = nome_produto.split("");
            if (produto.length>20){
                boolean achei=false;
                for (int x=20;x>0;x--){
                    if (produto[x].equals(" ") && achei==false){
                        achei=true;
                        String msg="";
                        for (int y=0;y<x;y++){
                            msg=msg+produto[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                        msg="";
                        for (int y=x;y<produto.length;y++){
                            msg=msg+produto[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                    }
                }
                if (achei==false){
                    String msg="";
                    int metade = Integer.parseInt(df.format(produto.length/2));
                    for (int x=0;x<metade;x++){
                        msg=msg+produto[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                    msg="";
                    for (int x=metade;x<produto.length;x++){
                        msg=msg+produto[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                }
            } else {
                iRetorno = interfaceEpsonNF.FormataTX(nome_produto+"\n", 2, 1, 0, 1, 1);
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
            iRetorno = interfaceEpsonNF.FormataTX("R$ "+preco_produto.replace(".", ",")+"\n", 2, 1, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
            rs=con.Select("SELECT imprime_codebar FROM fc_config");
            while (rs.next()){
                if (rs.getInt("imprime_codebar")==1){
                    vExecImprimeCodigoBarrasCODE128(chave_produto);
                } else if (rs.getInt("imprime_codebar")==0){
                    iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>");
                    iRetorno = interfaceEpsonNF.FormataTX("###"+chave_produto+"###\n", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
                }
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<gui></gui>");
	
        }
        
        public void ImprimeFuncao(String img_evento, int id_evento, String nome_evento, String nome_operacao, String valor_operacao, String nome_operador, String pdv, String img_eventoAtivo) throws SQLException{
            
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp>"+img_evento+"</bmp></ce><ce>");

            rs=con.Select("SELECT imprime_titulo FROM fc_config");
            while (rs.next()){
                if (rs.getInt("imprime_titulo")==1){
                    String[] evento = nome_evento.split("");
                    if (evento.length>20){
                        boolean achei=false;
                        for (int x=20;x>0;x--){
                            if (evento[x].equals(" ") && achei==false){
                                achei=true;
                                String msg="";
                                for (int y=0;y<x;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                                msg="";
                                for (int y=x;y<evento.length;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            }
                        }
                        if (achei==false){
                            String msg="";
                            int metade = Integer.parseInt(df.format(evento.length/2));
                            for (int x=0;x<metade;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            msg="";
                            for (int x=metade;x<evento.length;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                        }
                    } else {
                        iRetorno = interfaceEpsonNF.FormataTX(nome_evento+"\n", 2, 1, 0, 1, 1);
                    }
                }
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>");
            iRetorno = interfaceEpsonNF.FormataTX(timeStamp+"\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
            iRetorno = interfaceEpsonNF.FormataTX(nome_operacao+"\n", 2, 1, 0, 1, 1);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Operador:\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(nome_operador+"\n\n", 2, 0, 0, 0, 1);
            iRetorno = interfaceEpsonNF.FormataTX("PDV:\t", 2, 1, 0, 1, 1);
            if (nome_operacao.equals("Fechamento Caixa") || nome_operacao.equals("Abertura Caixa")){
                iRetorno = interfaceEpsonNF.FormataTX("Saldo:\n", 2, 1, 0, 1, 1);
            } else {
                iRetorno = interfaceEpsonNF.FormataTX("Valor:\n", 2, 1, 0, 1, 1);
            }
            iRetorno = interfaceEpsonNF.FormataTX(pdv+"\t\t"+"R$ "+valor_operacao.replace(".", ",")+"\n\n\n", 2, 1, 0, 1, 1);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Assinatura\n\n\n", 1, 1, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Assinatura\n", 1, 1, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<gui></gui>");
        }
        
        public void ImprimeRelatorioVendas(String img_evento, int id_evento, String nome_evento, String nome_operador, String id_fc_caixa, String abertura, String fechamento) throws SQLException{
            DecimalFormat df3 = new DecimalFormat("###,###,###,###,##0.00");
            
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp>"+img_evento+"</bmp></ce><ce>");
            
            rs=con.Select("SELECT imprime_titulo FROM fc_config");
            while (rs.next()){
                if (rs.getInt("imprime_titulo")==1){
                    String[] evento = nome_evento.split("");
                    if (evento.length>20){
                        boolean achei=false;
                        for (int x=20;x>0;x--){
                            if (evento[x].equals(" ") && achei==false){
                                achei=true;
                                String msg="";
                                for (int y=0;y<x;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                                msg="";
                                for (int y=x;y<evento.length;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            }
                        }
                        if (achei==false){
                            String msg="";
                            int metade = Integer.parseInt(df.format(evento.length/2));
                            for (int x=0;x<metade;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            msg="";
                            for (int x=metade;x<evento.length;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                        }
                    } else {
                        iRetorno = interfaceEpsonNF.FormataTX(nome_evento+"\n", 2, 1, 0, 1, 1);
                    }
                }
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>");
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Relatório de Vendas\n", 2, 1, 0, 1, 1);
            if (fechamento.equals("")){
                iRetorno = interfaceEpsonNF.FormataTX("\nPARCIAL\n", 2, 1, 0, 1, 1);
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Operador: "+nome_operador+"\n", 2, 0, 0, 0, 0);
            rs=con.Select("SELECT pdv_caixa FROM fc_caixa USE INDEX (id_fc_caixa) WHERE id_fc_caixa='"+id_fc_caixa+"' LIMIT 1");
            while (rs.next()){
                iRetorno = interfaceEpsonNF.FormataTX("PDV: "+rs.getString("pdv_caixa")+"\n\n", 2, 0, 0, 0, 0);
            }
            iRetorno = interfaceEpsonNF.FormataTX("Abertura: "+abertura+"\n", 2, 0, 0, 0, 0);
            if (!fechamento.equals("")){
                iRetorno = interfaceEpsonNF.FormataTX("Fechamento: "+fechamento+"\n", 2, 0, 0, 0, 0);
            }
            iRetorno = interfaceEpsonNF.FormataTX("\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>\n");
            double total=0;
            boolean teste;
            rs3=con.Select("SELECT nome_produtos FROM fc_produtos USE INDEX (id_fc_produtos) WHERE sts_produtos=1 ORDER BY id_fc_produtos");
            while (rs3.next()){
                rs=con.Select("SELECT count(id_fc_movimento) AS quant, valor_movimento FROM fc_movimento_item USE INDEX (id_fc_movimento) WHERE id_fc_caixa="+id_fc_caixa+" AND descricao_movimento='"+rs3.getString("nome_produtos")+"' GROUP BY descricao_movimento ORDER BY id_fc_produtos");
                teste=false;
                while (rs.next()){
                    teste=true;
                    iRetorno = interfaceEpsonNF.FormataTX("Venda - "+rs3.getString("nome_produtos")+"\n", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX("Quant: "+rs.getInt("quant")+"\t\tValor: R$ "+df3.format(rs.getDouble("quant")*Double.parseDouble(rs.getString("valor_movimento").replace(",", ".")))+"\n\n", 2, 0, 0, 0, 0);
                    total=total+rs.getDouble("quant")*Double.parseDouble(rs.getString("valor_movimento").replace(",", "."));
                }
                if (teste==false){
                    iRetorno = interfaceEpsonNF.FormataTX("Venda - "+rs3.getString("nome_produtos")+"\n", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX("Quant: 0\t\tValor: R$ 0,00\n\n", 2, 0, 0, 0, 0);
                }
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Valor total: R$ "+ df3.format(total)+"\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>\n");
            iRetorno = interfaceEpsonNF.FormataTX("\n\n\n\n\n\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<gui></gui>");
        }
        
        public void ImprimeRelatorioMovimentos(String img_evento, int id_evento, String nome_evento, String nome_operador, String id_fc_caixa, String abertura, String fechamento) throws SQLException{
            DecimalFormat df3 = new DecimalFormat("###,###,###,###,##0.00");
            
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp>"+img_evento+"</bmp></ce><ce>");
            
            rs=con.Select("SELECT imprime_titulo FROM fc_config");
            while (rs.next()){
                if (rs.getInt("imprime_titulo")==1){
                    String[] evento = nome_evento.split("");
                    if (evento.length>20){
                        boolean achei=false;
                        for (int x=20;x>0;x--){
                            if (evento[x].equals(" ") && achei==false){
                                achei=true;
                                String msg="";
                                for (int y=0;y<x;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                                msg="";
                                for (int y=x;y<evento.length;y++){
                                    msg=msg+evento[y];
                                }
                                iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            }
                        }
                        if (achei==false){
                            String msg="";
                            int metade = Integer.parseInt(df.format(evento.length/2));
                            for (int x=0;x<metade;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                            msg="";
                            for (int x=metade;x<evento.length;x++){
                                msg=msg+evento[x];
                            }
                            iRetorno = interfaceEpsonNF.FormataTX(msg+"\n", 2, 1, 0, 1, 1);
                        }
                    } else {
                        iRetorno = interfaceEpsonNF.FormataTX(nome_evento+"\n", 2, 1, 0, 1, 1);
                    }
                }
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>");
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Resumo de Movimentos\n", 2, 1, 0, 1, 1);
            if (fechamento.equals("")){
                iRetorno = interfaceEpsonNF.FormataTX("\nPARCIAL\n", 2, 1, 0, 1, 1);
            }
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
            iRetorno = interfaceEpsonNF.FormataTX("Operador: "+nome_operador+"\n", 2, 0, 0, 0, 0);
            rs=con.Select("SELECT pdv_caixa FROM fc_caixa USE INDEX (id_fc_caixa) WHERE id_fc_caixa='"+id_fc_caixa+"' LIMIT 1");
            while (rs.next()){
                iRetorno = interfaceEpsonNF.FormataTX("PDV: "+rs.getString("pdv_caixa")+"\n\n", 2, 0, 0, 0, 0);
            }
            iRetorno = interfaceEpsonNF.FormataTX("Abertura: "+abertura, 2, 0, 0, 0, 0);
            if (!fechamento.equals("")){
                iRetorno = interfaceEpsonNF.FormataTX("\nFechamento: "+fechamento, 2, 0, 0, 0, 0);
            }
            iRetorno = interfaceEpsonNF.FormataTX("\n\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>\n");
            rs=con.Select("SELECT tipo_movimento, datahora_movimento, descricao_movimento, COUNT(id_fc_movimento) AS qtde, SUM(valor_movimento) AS valor FROM fc_movimento USE INDEX (tipo_movimento) WHERE id_fc_caixa = "+id_fc_caixa+" GROUP BY tipo_movimento ORDER BY datahora_movimento");
            while (rs.next()){
                iRetorno = interfaceEpsonNF.FormataTX(rs.getString("descricao_movimento"), 2, 0, 0, 0, 0);
                String datahora = AjeitaDataHora(rs.getString("datahora_movimento"));
                switch (rs.getInt("tipo_movimento")){
                    case 10:
                        iRetorno = interfaceEpsonNF.FormataTX("\nOperações: "+rs.getInt("qtde")+"\t\tTotal: R$ "+df3.format(Double.parseDouble(rs.getString("valor").replace(",", ".")))+"\n", 2, 0, 0, 0, 0);
                        break;
                    case 11:
                        iRetorno = interfaceEpsonNF.FormataTX("\nOperações: "+rs.getInt("qtde")+"\t\tTotal: R$ "+df3.format(Double.parseDouble(rs.getString("valor").replace(",", ".")))+"\n", 2, 0, 0, 0, 0);
                        break;
                    case 1:
                        iRetorno = interfaceEpsonNF.FormataTX("\t"+datahora+"\n", 2, 0, 0, 0, 0);
                        iRetorno = interfaceEpsonNF.FormataTX("Operações: "+rs.getInt("qtde")+"\t\tTotal: R$ "+df3.format(Double.parseDouble(rs.getString("valor").replace(",", ".")))+"\n", 2, 0, 0, 0, 0);
                        break;
                    case 2:
                        iRetorno = interfaceEpsonNF.FormataTX("\t"+datahora+"\n", 2, 0, 0, 0, 0);
                        iRetorno = interfaceEpsonNF.FormataTX("Operações: "+rs.getInt("qtde")+"\n", 2, 0, 0, 0, 0);
                        break;
                    case 3: case 5:
                        iRetorno = interfaceEpsonNF.FormataTX("\nOperações: "+rs.getInt("qtde")+"\t\tTotal: R$ "+df3.format(Double.parseDouble(rs.getString("valor").replace(",", ".")))+"\n", 2, 0, 0, 0, 0);
                        break;
                }
                iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>\n");    
            }
            iRetorno = interfaceEpsonNF.FormataTX("\n\n\n\n\n\n", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<gui></gui>");
        }

	
	public void vExecIniciaPorta() {
		iRetorno = interfaceEpsonNF.IniciaPorta("USB");
	}
	
	public void vExecConfiguraCodigoBarras() {
            iRetorno = interfaceEpsonNF.ConfiguraCodigoBarras(70, 1, 0, 0, 5);
	}

	public void vExecImprimeCodigoBarrasCODE128(String chave_produto) {
            iRetorno = interfaceEpsonNF.ImprimeCodigoBarrasCODE128(chave_produto);
	}
        
        private String AjeitaDataHora(String abertura){
            String aberturaArray[] = abertura.split(" ");
            String dataAberturaArray[] = aberturaArray[0].split("-");
            String dataAberturaCerta = dataAberturaArray[2]+"/"+dataAberturaArray[1]+"/"+dataAberturaArray[0];
            String horaAberturaCerta = aberturaArray[1].replace(".0", "");
            String aberturaCompleta=dataAberturaCerta+" - "+horaAberturaCerta;
            return aberturaCompleta;
        }
    */
}
