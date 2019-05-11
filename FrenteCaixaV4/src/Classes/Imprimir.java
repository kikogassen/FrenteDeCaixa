package Classes;

import Epson.InterfaceEpsonNF.*;
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

    private static final long serialVersionUID = 1L;

    InterfaceEpsonNF interfaceEpsonNF = new InterfaceEpsonNF();
    Conexao con = new Conexao();
    ResultSet rs, rs2, rs3;

    public int iRetorno;
    DecimalFormat df = new DecimalFormat("0");
    DecimalFormat df2 = new DecimalFormat("0.00");

    public Imprimir() {
        vExecIniciaPorta();
        vExecConfiguraCodigoBarras();
    }

    public void ImprimeTicket(String nome_evento, String nome_produto, String preco_produto, String imp_valor, String chave_produto, int titulo, int barra, int pdv, int vendedor) throws SQLException {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp></bmp></ce><ce>");

        if (titulo == 1) {
            String[] evento = nome_evento.split("");
            if (evento.length > 20) {
                boolean achei = false;
                for (int x = 20; x > 0; x--) {
                    if (evento[x].equals(" ") && achei == false) {
                        achei = true;
                        String msg = "";
                        for (int y = 0; y < x; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                        msg = "";
                        for (int y = x; y < evento.length; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    }
                }
                if (achei == false) {
                    String msg = "";
                    int metade = Integer.parseInt(df.format(evento.length / 2));
                    for (int x = 0; x < metade; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    msg = "";
                    for (int x = metade; x < evento.length; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                }
            } else {
                iRetorno = interfaceEpsonNF.FormataTX(nome_evento + "\n", 2, 1, 0, 1, 1);
            }
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>");
        //iRetorno = interfaceEpsonNF.FormataTX(timeStamp+"\n", 2, 1, 0, 1, 0);
        iRetorno = interfaceEpsonNF.FormataTX(timeStamp + "\n", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
        String[] produto = nome_produto.split("");
        if (produto.length > 20) {
            boolean achei = false;
            for (int x = 20; x > 0; x--) {
                if (produto[x].equals(" ") && achei == false) {
                    achei = true;
                    String msg = "";
                    for (int y = 0; y < x; y++) {
                        msg = msg + produto[y];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    msg = "";
                    for (int y = x; y < produto.length; y++) {
                        msg = msg + produto[y];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                }
            }
            if (achei == false) {
                String msg = "";
                int metade = Integer.parseInt(df.format(produto.length / 2));
                for (int x = 0; x < metade; x++) {
                    msg = msg + produto[x];
                }
                iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                msg = "";
                for (int x = metade; x < produto.length; x++) {
                    msg = msg + produto[x];
                }
                iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
            }
        } else {
            iRetorno = interfaceEpsonNF.FormataTX(nome_produto + "\n", 2, 1, 0, 1, 1);
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");

        if (imp_valor.equals("0")) {
            iRetorno = interfaceEpsonNF.FormataTX("R$ " + preco_produto.replace(".", ",") + "\n", 2, 1, 0, 0, 0);
        } else {
            iRetorno = interfaceEpsonNF.FormataTX(imp_valor + "\n", 2, 1, 0, 0, 0);
        }
        
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");

        if (barra == 1) {
            vExecImprimeCodigoBarrasCODE128(chave_produto);
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>"+ pdv +"-"+ chave_produto +"-"+vendedor+"</ce>\n<ce>");
        } else {
            iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>#### "+ pdv +"-"+ chave_produto +"-"+vendedor+" ####</ce>\n<ce>");
        }
        iRetorno = interfaceEpsonNF.FormataTX("--- Lifes Creative Tecnologia (51) 3793-0623 ---\n", 1, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<gui></gui>");
    }
    
    public void acionaGaveta(){
        iRetorno = interfaceEpsonNF.AcionaGaveta();
    }

    public void ImprimeFuncao(int id_evento, String nome_evento, String nome_operacao, String valor_operacao, String nome_operador, String pdv, int titulo) throws SQLException {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp></bmp></ce><ce>");
        if (titulo == 1) {
            String[] evento = nome_evento.split("");
            if (evento.length > 20) {
                boolean achei = false;
                for (int x = 20; x > 0; x--) {
                    if (evento[x].equals(" ") && achei == false) {
                        achei = true;
                        String msg = "";
                        for (int y = 0; y < x; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                        msg = "";
                        for (int y = x; y < evento.length; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    }
                }
                if (achei == false) {
                    String msg = "";
                    int metade = Integer.parseInt(df.format(evento.length / 2));
                    for (int x = 0; x < metade; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    msg = "";
                    for (int x = metade; x < evento.length; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                }
            } else {
                iRetorno = interfaceEpsonNF.FormataTX(nome_evento + "\n", 2, 1, 0, 1, 1);
            }
        }

        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
        iRetorno = interfaceEpsonNF.FormataTX(nome_operacao + "\n", 2, 1, 0, 1, 1);
        iRetorno = interfaceEpsonNF.FormataTX(timeStamp + "\n", 2, 1, 0, 0, 0);
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
        if (titulo == 0) {
            iRetorno = interfaceEpsonNF.FormataTX("Evento: \t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(nome_evento + "\n", 2, 0, 0, 0, 1);
        }
        iRetorno = interfaceEpsonNF.FormataTX("Operador: \t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(nome_operador + "\n", 2, 0, 0, 0, 1);
        iRetorno = interfaceEpsonNF.FormataTX("PDV: \t\t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(pdv + "\n", 2, 0, 0, 0, 1);
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n");

        if (nome_operacao.equals("ABERTURA CAIXA")) {
            iRetorno = interfaceEpsonNF.FormataTX("Saldo Inicial:\t", 2, 0, 0, 0, 1);
        } else if (nome_operacao.equals("FECHAMENTO CAIXA")) {
            String dinheiro = valor_operacao.split("\\+")[0];
            String credito = valor_operacao.split("\\+")[1];
            String debito = valor_operacao.split("\\+")[2];
            String banri = valor_operacao.split("\\+")[3];
            iRetorno = interfaceEpsonNF.FormataTX("Dinheiro:\t", 2, 0, 0, 0, 1);
            iRetorno = interfaceEpsonNF.FormataTX(dinheiro + "\n", 2, 1, 0, 1, 1);
            iRetorno = interfaceEpsonNF.FormataTX("Crédito:\t", 2, 0, 0, 0, 1);
            iRetorno = interfaceEpsonNF.FormataTX(credito + "\n", 2, 1, 0, 1, 1);
            iRetorno = interfaceEpsonNF.FormataTX("Débito:\t\t", 2, 0, 0, 0, 1);
            iRetorno = interfaceEpsonNF.FormataTX(debito + "\n", 2, 1, 0, 1, 1);
            iRetorno = interfaceEpsonNF.FormataTX("Banri:\t\t", 2, 0, 0, 0, 1);
            iRetorno = interfaceEpsonNF.FormataTX(banri + "\n\n\n\n", 2, 1, 0, 1, 1);

            //Dinheiro:\t" + dinheiro + "\nCrédito:\t" + credito + "\nDébito:\t" + debito + "\nBanrisul:\t" + banri + "\nTotal:\t"
            //iRetorno = interfaceEpsonNF.FormataTX("Dinheiro:\t" + dinheiro + "\nCrédito:\t" + credito + "\nDébito:\t\t" + debito + "\nBanrisul:\t" + banri + "\n\n\n\n", 2, 1, 0, 1, 1);
        } else {
            iRetorno = interfaceEpsonNF.FormataTX("Valor:\t", 2, 0, 0, 0, 1);
        }
        if (!nome_operacao.equals("FECHAMENTO CAIXA")) {
            iRetorno = interfaceEpsonNF.FormataTX("R$ " + valor_operacao + "\n\n\n\n", 2, 1, 0, 1, 1);
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>______________________________________</ce>");
        iRetorno = interfaceEpsonNF.FormataTX("Assinatura\n\n\n", 1, 1, 0, 0, 0);
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>______________________________________</ce>");
        iRetorno = interfaceEpsonNF.FormataTX("Assinatura\n\n", 1, 1, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX("  --- Lifes Creative Tecnologia (51) 3793-0623 ---\n", 1, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<gui></gui>");

    }

    public void ImprimeRelatorioVendas(int id_evento, String nome_evento, String nome_operador, String id_fc_caixa, String abertura, String fechamento, String pdv, int titulo) throws SQLException {

        DecimalFormat df3 = new DecimalFormat("###,###,###,###,##0.00");
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp></bmp></ce><ce>");
        if (titulo == 1) {
            String[] evento = nome_evento.split("");
            if (evento.length > 20) {
                boolean achei = false;
                for (int x = 20; x > 0; x--) {
                    if (evento[x].equals(" ") && achei == false) {
                        achei = true;
                        String msg = "";
                        for (int y = 0; y < x; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                        msg = "";
                        for (int y = x; y < evento.length; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    }
                }
                if (achei == false) {
                    String msg = "";
                    int metade = Integer.parseInt(df.format(evento.length / 2));
                    for (int x = 0; x < metade; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    msg = "";
                    for (int x = metade; x < evento.length; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                }
            } else {
                iRetorno = interfaceEpsonNF.FormataTX(nome_evento + "\n", 2, 1, 0, 1, 1);
            }
        }

        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
        iRetorno = interfaceEpsonNF.FormataTX("Relatório de Vendas\n", 2, 1, 0, 1, 1);
        if (fechamento.equals("")) {
            iRetorno = interfaceEpsonNF.FormataTX("PARCIAL\n", 2, 1, 0, 1, 1);
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
        if (titulo == 0) {
            iRetorno = interfaceEpsonNF.FormataTX("Evento: \t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(nome_evento + "\n", 2, 0, 0, 0, 1);
        }
        iRetorno = interfaceEpsonNF.FormataTX("Operador: \t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(nome_operador + "\n", 2, 0, 0, 0, 1);
        iRetorno = interfaceEpsonNF.FormataTX("PDV: \t\t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(pdv + "\n", 2, 0, 0, 0, 1);
        iRetorno = interfaceEpsonNF.FormataTX("Abertura: \t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(abertura + "\n", 2, 0, 0, 0, 1);
        if (!fechamento.equals("")) {
            iRetorno = interfaceEpsonNF.FormataTX("Fechamento: \t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(fechamento + "\n", 2, 0, 0, 0, 1);
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n");

        double total = 0;
        rs = con.Select("SELECT descricao_movimento, COUNT(id_fc_movimento) AS quant, SUM(valor_movimento) AS valor FROM fc_movimento_item WHERE id_fc_caixa = "+ id_fc_caixa +" AND sts_movimento = 1 GROUP BY id_fc_produtos ORDER BY id_fc_produtos");
        while (rs.next()) {
            iRetorno = interfaceEpsonNF.FormataTX("Venda\t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(rs.getString("descricao_movimento") + "\n", 2, 0, 0, 0, 1);
            iRetorno = interfaceEpsonNF.FormataTX("Quant:\t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(String.format("%03d", rs.getInt("quant")) + "\t\t\t", 2, 0, 0, 0, 1);
            iRetorno = interfaceEpsonNF.FormataTX("R$ ", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(df3.format(rs.getDouble("valor")).replace(".", ",") + "\n\n", 2, 0, 0, 0, 1);
            total = total + rs.getDouble("valor");
        }
        //iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>");

        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n");
        
        rs = con.Select("SELECT nome_pgto, SUM(i.valor_movimento) AS valor FROM fc_movimento_item i USE INDEX (id_fc_caixa) INNER JOIN fc_movimento m ON i.id_fc_movimento = m.id_fc_movimento INNER JOIN fc_forma_pgto f ON f.id_fc_forma_pgto = m.id_fc_forma_pgto WHERE i.id_fc_caixa = "+ id_fc_caixa +" AND i.sts_movimento = 1 GROUP BY m.id_fc_forma_pgto");
        while (rs.next()) {
            iRetorno = interfaceEpsonNF.FormataTX("\tValor "+rs.getString("nome_pgto")+"\t\t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX("R$ "+df3.format(rs.getDouble("valor")) + "\n", 2, 0, 0, 0, 1);
            //iRetorno = interfaceEpsonNF.FormataTX(rs.getString("nome_pgto")+": R$ " + df3.format(rs.getDouble("valor")) + "\n", 2, 0, 0, 0, 1);
        }

        rs = con.Select("SELECT SUM(valor_movimento) AS valor FROM fc_movimento_item USE INDEX (id_fc_caixa) WHERE id_fc_caixa = "+ id_fc_caixa +" AND sts_movimento = 1");
        rs.next();
        
        iRetorno = interfaceEpsonNF.FormataTX("\tValor TOTAL:\t\t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX("R$ "+df3.format(rs.getDouble("valor")) + "\n", 2, 0, 0, 0, 1);

            //iRetorno = interfaceEpsonNF.FormataTX(" R$ " + df3.format(rs.getDouble("valor")) + "\n", 2, 0, 0, 0, 1);

        //iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>\n");
        iRetorno = interfaceEpsonNF.FormataTX("  --- Lifes Creative Tecnologia (51) 3793-0623 ---\n", 1, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX("\n\n", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<gui></gui>");
    }

    public void ImprimeRelatorioMovimentos(int id_evento, String nome_evento, String nome_operador, String id_fc_caixa, String abertura, String fechamento, String pdv, int titulo) throws SQLException {

        DecimalFormat df3 = new DecimalFormat("###,###,###,###,##0.00");
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce><bmp></bmp></ce><ce>");
        if (titulo == 1) {
            String[] evento = nome_evento.split("");
            if (evento.length > 20) {
                boolean achei = false;
                for (int x = 20; x > 0; x--) {
                    if (evento[x].equals(" ") && achei == false) {
                        achei = true;
                        String msg = "";
                        for (int y = 0; y < x; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                        msg = "";
                        for (int y = x; y < evento.length; y++) {
                            msg = msg + evento[y];
                        }
                        iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    }
                }
                if (achei == false) {
                    String msg = "";
                    int metade = Integer.parseInt(df.format(evento.length / 2));
                    for (int x = 0; x < metade; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                    msg = "";
                    for (int x = metade; x < evento.length; x++) {
                        msg = msg + evento[x];
                    }
                    iRetorno = interfaceEpsonNF.FormataTX(msg + "\n", 2, 1, 0, 1, 1);
                }
            } else {
                iRetorno = interfaceEpsonNF.FormataTX(nome_evento + "\n", 2, 1, 0, 1, 1);
            }
        }

        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n<ce>");
        iRetorno = interfaceEpsonNF.FormataTX("Resumo de Movimentos\n", 2, 1, 0, 1, 1);
        if (fechamento.equals("")) {
            iRetorno = interfaceEpsonNF.FormataTX("PARCIAL\n", 2, 1, 0, 1, 1);
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce>");
        if (titulo == 0) {
            iRetorno = interfaceEpsonNF.FormataTX("Evento: \t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(nome_evento + "\n", 2, 0, 0, 0, 1);
        }
        iRetorno = interfaceEpsonNF.FormataTX("Operador: \t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(nome_operador + "\n", 2, 0, 0, 0, 1);
        iRetorno = interfaceEpsonNF.FormataTX("PDV: \t\t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(pdv + "\n", 2, 0, 0, 0, 1);
        iRetorno = interfaceEpsonNF.FormataTX("Abertura: \t", 2, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX(abertura + "\n", 2, 0, 0, 0, 1);
        if (!fechamento.equals("")) {
            iRetorno = interfaceEpsonNF.FormataTX("Fechamento: \t", 2, 0, 0, 0, 0);
            iRetorno = interfaceEpsonNF.FormataTX(fechamento + "\n", 2, 0, 0, 0, 1);
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("</ce><ce>__________________________________________</ce>\n");

        rs = con.Select("SELECT tipo_movimento, datahora_movimento, COUNT(id_fc_movimento) AS qtde, SUM(valor_movimento) AS valor FROM fc_movimento USE INDEX (tipo_movimento) WHERE id_fc_caixa = '" + id_fc_caixa + "' AND sts_movimento = 1 GROUP BY tipo_movimento ORDER BY datahora_movimento");
        while (rs.next()) {

            //iRetorno = interfaceEpsonNF.FormataTX("\nOperações: "+rs.getInt("qtde")+"\t\tTotal: R$ "+df3.format(Double.parseDouble(rs.getString("valor").replace(",", ".")))+"\n", 2, 0, 0, 0, 0);
            String datahora = AjeitaDataHora(rs.getString("datahora_movimento"));

            switch (rs.getInt("tipo_movimento")) {
                case 1:
                    iRetorno = interfaceEpsonNF.FormataTX("Abertura Caixa\n", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX(datahora + "\t\t", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX("R$ ", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(df3.format(rs.getDouble("valor")).replace(".", ",") + "\n\n", 2, 0, 0, 0, 1);
                    break;
                case 10:
                    iRetorno = interfaceEpsonNF.FormataTX("Vendas\n", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("Quant:\t", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(String.format("%03d", rs.getInt("qtde")) + "\t\t\t", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("R$ ", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(df3.format(rs.getDouble("valor")).replace(".", ",") + "\n\n", 2, 0, 0, 0, 1);
                    break;
                case 11:
                    iRetorno = interfaceEpsonNF.FormataTX("Reimpressão de Venda\n", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("Quant:\t", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(String.format("%03d", rs.getInt("qtde")) + "\t\t\t", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("R$ ", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX("0,00\n\n", 2, 0, 0, 0, 1);
                    break;
                case 3:
                    iRetorno = interfaceEpsonNF.FormataTX("Sangria de Dinheiro\n", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("Quant:\t", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(String.format("%03d", rs.getInt("qtde")) + "\t\t\t", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("R$ ", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(df3.format(rs.getDouble("valor")).replace(".", ",") + "\n\n", 2, 0, 0, 0, 1);
                    break;
                case 5:
                    iRetorno = interfaceEpsonNF.FormataTX("Suprimento de Dinheiro\n", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("Quant:\t", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(String.format("%03d", rs.getInt("qtde")) + "\t\t\t", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX("R$ ", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(df3.format(rs.getDouble("valor")).replace(".", ",") + "\n\n", 2, 0, 0, 0, 1);
                    break;
                case 2:
                    iRetorno = interfaceEpsonNF.FormataTX("Fechamento Caixa\n", 2, 0, 0, 0, 1);
                    iRetorno = interfaceEpsonNF.FormataTX(datahora + "\t\t", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX("R$ ", 2, 0, 0, 0, 0);
                    iRetorno = interfaceEpsonNF.FormataTX(df3.format(rs.getDouble("valor")).replace(".", ",") + "\n\n", 2, 0, 0, 0, 1);
                    break;

            }
        }
        iRetorno = interfaceEpsonNF.ImprimeTextoTag("<ce>__________________________________________</ce>\n");
        iRetorno = interfaceEpsonNF.FormataTX("  --- Lifes Creative Tecnologia (51) 3793-0623 ---\n", 1, 0, 0, 0, 0);
        iRetorno = interfaceEpsonNF.FormataTX("\n\n", 2, 0, 0, 0, 0);
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

    private String AjeitaDataHora(String abertura) {
        String aberturaArray[] = abertura.split(" ");
        String dataAberturaArray[] = aberturaArray[0].split("-");
        String dataAberturaCerta = dataAberturaArray[2] + "/" + dataAberturaArray[1] + "/" + dataAberturaArray[0];
        String horaAberturaCerta = aberturaArray[1].replace(".0", "");
        String aberturaCompleta = dataAberturaCerta + " - " + horaAberturaCerta;
        return aberturaCompleta;
    }
}
