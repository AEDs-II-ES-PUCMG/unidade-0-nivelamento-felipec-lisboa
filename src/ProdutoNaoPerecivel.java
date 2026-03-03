public class ProdutoNaoPerecivel extends Produto{

    public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro){
    super(desc, precoCusto,margemLucro);
    }

    public ProdutoNaoPerecivel(String desc, double precoCusto){
        super(desc, precoCusto);
    }

    @Override
            public String gerarDadosTexto(){
                String precoFormatado = String.format("%.2f",precoCusto).replace(",", ".");
                String margemFormatada = String.format("%2.f",margemLucro).replace(",",".");
                return String.format("1;%s;%s;%s", descricao,precoFormatado,margemFormatada);
            }
    

}
