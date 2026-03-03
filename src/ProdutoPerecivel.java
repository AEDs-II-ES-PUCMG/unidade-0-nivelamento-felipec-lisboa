import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProdutoPerecivel extends Produto {

    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;
    private LocalDate validade;

    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, double DESCONTO, int PRAZO_DESCONTO,
            LocalDate validade) {
        super(desc, precoCusto, margemLucro);

        if (validade.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("O produto venceu");
        }

    }

    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        super(desc, precoCusto, margemLucro);
    }

    @Override
    public double valorDeVenda() {
        double desconto = 0d;
        int diasValidade = LocalDate.now().until(validade).getDays();
        if (diasValidade <= 7) {
            desconto = DESCONTO;
        }
        return (precoCusto * (1 + margemLucro)) * (1 - desconto);

    }

    @Override
    public String toString() {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String dados = super.toString();
        dados += "\nValido até" + formato.format(validade);
        return dados;
    }

    @Override
    public String gerarDadosTexto() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String precoFormatado = String.format("%.2f", precoCusto).replace(",", ".");
        String margemFormatada = String.format("%2.f", margemLucro).replace(",", ".");
        String dataFormatada = formato.format(validade);
        return String.format("1;%s;%s;%s", descricao, precoFormatado, margemFormatada,dataFormatada);
    }

}
