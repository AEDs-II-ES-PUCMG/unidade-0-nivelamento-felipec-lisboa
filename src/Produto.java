import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe base abstrata que representa um produto genérico.
 * Contém os atributos e comportamentos comuns a qualquer produto da aplicação.
 */
public abstract class  Produto {

	/** Margem de lucro padrão utilizada quando não é informada explicitamente */
	protected static final double MARGEM_PADRAO = 0.2;

	/** Texto que descreve o produto (nome) */
	protected String descricao;

	/** Preço de custo do produto (quanto a loja paga para adquirir) */
	protected double precoCusto;

	/** Margem de lucro aplicada sobre o preço de custo (por exemplo, 0.2 = 20%) */
	protected double margemLucro;

	/**
	 * Inicializador privado utilizado pelos construtores.
	 * Centraliza a validação dos dados de entrada.
	 * 
	 * @param desc        Descrição do produto (mínimo de 3 caracteres)
	 * @param precoCusto  Preço do produto (mínimo 0.01)
	 * @param margemLucro Margem de lucro (mínimo 0.01)
	 */
	private void init(String desc, double precoCusto, double margemLucro) {

		// Garante que todos os valores são válidos antes de atribuir
		if ((desc.length() >= 3) && (precoCusto > 0.0) && (margemLucro > 0.0)) {
			descricao = desc;
			this.precoCusto = precoCusto;
			this.margemLucro = margemLucro;
		} else {
			throw new IllegalArgumentException("Valores inválidos para os dados do produto.");
		}
	}

	/**
	 * Construtor completo.
	 * Recebe descrição, preço de custo e margem de lucro desejada.
	 * 
	 * @param desc        Descrição do produto (mínimo de 3 caracteres)
	 * @param precoCusto  Preço do produto (mínimo 0.01)
	 * @param margemLucro Margem de lucro (mínimo 0.01)
	 */
	public Produto(String desc, double precoCusto, double margemLucro) {
		init(desc, precoCusto, margemLucro);
	}

	/**
	 * Construtor sem margem de lucro.
	 * Utiliza a margem padrão {@link #MARGEM_PADRAO} caso a margem não seja informada.
	 * 
	 * @param desc       Descrição do produto (mínimo de 3 caracteres)
	 * @param precoCusto Preço do produto (mínimo 0.01)
	 */
	public Produto(String desc, double precoCusto) {
		init(desc, precoCusto, MARGEM_PADRAO);
	}

	/**
	 * Retorna o valor de venda do produto, considerando seu preço de custo e
	 * a margem de lucro configurada.
	 * 
	 * @return Valor de venda do produto (double, positivo)
	 */
	public double valorDeVenda() {
		return (precoCusto * (1.0 + margemLucro));
	}

	/**
	 * Descrição, em string, do produto, contendo sua descrição e o valor de venda.
	 * 
	 * @return String com o formato:
	 *         [NOME]: R$ [VALOR DE VENDA]
	 */
	@Override
	public String toString() {

		// Usa o formato de moeda da localidade atual (por exemplo, R$ no Brasil)
		NumberFormat moeda = NumberFormat.getCurrencyInstance();

		// Monta a string com o nome e o valor de venda formatado
		return String.format("NOME: " + descricao + ": " + moeda.format(valorDeVenda()));
	}

	/**
	 * Igualdade de produtos: caso possuam o mesmo nome/descrição.
	 * 
	 * @param obj Outro produto a ser comparado
	 * @return booleano true/false conforme o parâmetro possua a descrição igual ou
	 *         não a este produto.
	 */
	@Override
	public boolean equals(Object obj) {
		// Converte o Object recebido para Produto (assumindo que a chamada está correta)
		Produto outro = (Produto) obj;
		// Compara apenas a descrição, ignorando maiúsculas/minúsculas
		return this.descricao.toLowerCase().equals(outro.descricao.toLowerCase());
	}

	/**
	 * Gera uma linha de texto a partir dos dados do produto,
	 * usada para gravação em arquivo CSV.
	 * 
	 * @return Uma string no formato "tipo;
	 *         descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
	 */
	public abstract String gerarDadosTexto();



	/**
	 * Cria um produto a partir de uma linha de dados em formato texto.
	 * A linha de dados deve estar de acordo com a formatação:
	 * "tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
	 * ou o funcionamento não será garantido. Os tipos são 1 para produto não
	 * perecível e 2 para perecível.
	 * 
	 * @param linha Linha com os dados do produto a ser criado.
	 * @return Um produto com os dados recebidos
	 */
	static Produto criarDoTexto(String linha) {
		// Variável que irá receber a instância do produto concreto (perecível ou não)
		Produto novoProduto = null;

		// Quebra a linha de texto usando ';' como separador de campos
		String[] parte = linha.split(";");

		// Formato de data esperado no arquivo (dd/MM/yyyy)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Converte os campos básicos
		int tipo = Integer.parseInt(parte[0]);   // 1 = não perecível, 2 = perecível
		String nome = parte[1];
		Double preco = Double.parseDouble(parte[2]);
		Double margem = Double.parseDouble(parte[3]);

		// De acordo com o tipo, cria a subclasse correspondente
		if(tipo == 1){
			// Produto sem data de validade
			novoProduto = new ProdutoNaoPerecivel(nome, preco,margem);
		}else{
			// Produto perecível: lê também a data de validade
			LocalDate datavalidade = LocalDate.parse(parte[4],formatter);
			novoProduto = new ProdutoPerecivel(nome, preco,margem,datavalidade);
		}
		return novoProduto;
	}

}