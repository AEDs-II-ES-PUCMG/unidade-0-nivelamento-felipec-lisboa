import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Implementação concreta de um produto perecível.
 * Além dos dados de Produto, possui uma data de validade e
 * aplica desconto quando está próximo de vencer.
 */
public class ProdutoPerecivel extends Produto {

    /** Percentual de desconto aplicado quando o produto está perto de vencer (25%) */
    private static final double DESCONTO = 0.25;

    /** Quantidade de dias antes do vencimento em que o desconto passa a valer */
    private static final int PRAZO_DESCONTO = 7;

    /** Data de validade do produto */
    private LocalDate validade;

    /**
     * Construtor de produto perecível.
     *
     * @param desc        descrição do produto
     * @param precoCusto  preço de custo do produto
     * @param margemLucro margem de lucro aplicada
     * @param validade    data de validade (deve ser hoje ou uma data futura)
     */
    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        // Inicializa os atributos herdados de Produto
        super(desc, precoCusto, margemLucro);

        // Se a data de validade já passou, não faz sentido cadastrar o produto
        if (validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O produto venceu");
        }

        // Armazena a data de validade informada
        this.validade = validade;
    }

    /**
     * Calcula o valor de venda do produto perecível.
     * Se estiver a até PRAZO_DESCONTO dias da validade, aplica o desconto.
     */
    @Override
    public double valorDeVenda() {
        double desconto = 0d;

        // Calcula quantos dias faltam para o produto vencer
        int diasValidade = LocalDate.now().until(validade).getDays();

        // Se estiver dentro da janela de desconto, aplica o percentual definido
        if (diasValidade <= PRAZO_DESCONTO) {
            desconto = DESCONTO;
        }

        // Fórmula: (custo * (1 + margem)) * (1 - desconto)
        return (precoCusto * (1 + margemLucro)) * (1 - desconto);

    }

    /**
     * Representação em texto do produto, incluindo a data de validade.
     */
    @Override
    public String toString() {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Usa a string padrão de Produto e acrescenta a informação de validade
        String dados = super.toString();
        dados += "\nVálido até " + formato.format(validade);
        return dados;
    }

    /**
     * Gera a linha de texto que representa o produto perecível no arquivo CSV.
     * Formato: 2;descricao;precoDeCusto;margemDeLucro;dataDeValidade
     */
    @Override
    public String gerarDadosTexto() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Formata o preço de custo com 2 casas decimais e separador '.'
        String precoFormatado = String.format("%.2f", precoCusto).replace(",", ".");

        // Formata a margem de lucro com 2 casas decimais e separador '.'
        String margemFormatada = String.format("%.2f", margemLucro).replace(",", ".");

        // Formata a data de validade no padrão dd/MM/yyyy
        String dataFormatada = formato.format(validade);

        // Tipo 2 identifica produtos perecíveis
        return String.format("2;%s;%s;%s;%s", descricao, precoFormatado, margemFormatada, dataFormatada);
    }

}
