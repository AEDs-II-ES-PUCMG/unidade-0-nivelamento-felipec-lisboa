/**
 * Implementação concreta de um produto não perecível.
 * Não possui data de validade, apenas herda o comportamento padrão de Produto.
 */
public class ProdutoNaoPerecivel extends Produto{

    /**
     * Construtor completo.
     * Recebe descrição, preço de custo e margem de lucro.
     */
    public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro){
        // Repassa os parâmetros para o construtor da classe base Produto
        super(desc, precoCusto,margemLucro);
    }

    /**
     * Construtor que utiliza a margem de lucro padrão da classe Produto.
     */
    public ProdutoNaoPerecivel(String desc, double precoCusto){
        super(desc, precoCusto);
    }

    @Override
    public String gerarDadosTexto(){
        // Formata o preço de custo com 2 casas decimais e ponto como separador decimal
        String precoFormatado = String.format("%.2f",precoCusto).replace(",", ".");
        // Formata a margem de lucro com 2 casas decimais e ponto como separador decimal
        String margemFormatada = String.format("%.2f",margemLucro).replace(",",".");
        // Monta a linha no formato:
        // tipo;descricao;precoDeCusto;margemDeLucro
        // Para não perecível, o tipo é 1
        return String.format("1;%s;%s;%s", descricao,precoFormatado,margemFormatada);
    }
    

}
