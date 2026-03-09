import java.nio.charset.Charset;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe principal de console (CLI) da aplicação.
 * Responsável por interagir com o usuário e orquestrar as operações
 * de leitura, cadastro, listagem, busca e gravação de produtos.
 */
public class App {

	/** Quantidade máxima de novos produtos que podem ser inseridos em uma execução */
	static final int MAX_NOVOS_PRODUTOS = 10;

	/**
	 * Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto
	 */
	static String nomeArquivoDados;

	/** Scanner para leitura do teclado */
	static Scanner teclado;

	/**
	 * Vetor de produtos cadastrados. Sempre terá espaço para 10 novos produtos a
	 * cada execução
	 */
	static Produto[] produtosCadastrados;

	/** Quantidade produtos cadastrados atualmente no vetor */
	static int quantosProdutos;

	/** Gera um efeito de pausa na CLI. Espera o usuário pressionar ENTER para continuar */
	static void pausa() {
		System.out.println("Digite enter para continuar...");
		teclado.nextLine();
	}

	/** Cabeçalho principal da CLI do sistema */
	static void cabecalho() {
		System.out.println("AEDII COMÉRCIO DE COISINHAS");
		System.out.println("===========================");
	}

	/**
	 * Imprime o menu principal, lê a opção do usuário e a retorna (int).
	 * Perceba que poderia haver uma melhor modularização com a criação de uma
	 * classe Menu dedicada apenas a essa responsabilidade.
	 * 
	 * @return Um inteiro com a opção do usuário.
	 */
	static int menu() {
		cabecalho();
		System.out.println("1 - Listar todos os produtos");
		System.out.println("2 - Procurar e listar um produto");
		System.out.println("3 - Cadastrar novo produto");
		System.out.println("0 - Sair");
		System.out.print("Digite sua opção: ");
		return Integer.parseInt(teclado.nextLine());
	}

	/**
	 * Lê os dados de um arquivo texto e retorna um vetor de produtos. Arquivo no
	 * formato
	 * N (quantiade de produtos) <br/>
	 * tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
	 * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em
	 * caso de problemas com o arquivo.
	 * 
	 * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
	 * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de
	 *         leitura.
	 */
	static Produto[] lerProdutos(String nomeArquivoDados) {
		// Scanner responsável por ler o arquivo de texto
		Scanner arquivo = null;
		// Variáveis auxiliares para controle do laço
		int i, numProdutos;
		String linha;
		Produto produto;
		// Vetor temporário que armazena apenas os produtos lidos do arquivo
		Produto[] produtoCadastrados = new Produto[MAX_NOVOS_PRODUTOS];

		try{
			// Abre o arquivo para leitura usando o charset UTF-8
			arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

			// Primeira linha contém a quantidade de produtos gravados
			numProdutos = Integer.parseInt(arquivo.nextLine());

			// Lê, no máximo, numProdutos linhas ou até atingir o limite de MAX_NOVOS_PRODUTOS
			for(i=0;(i<numProdutos && i<MAX_NOVOS_PRODUTOS); i++){
				linha = arquivo.nextLine();               // lê uma linha do CSV
				produto = Produto.criarDoTexto(linha);    // converte a linha em um objeto Produto
				produtoCadastrados[i] = produto;          // armazena no vetor temporário
			}

			// Atualiza o número de produtos efetivamente carregados
			quantosProdutos =i;

		}catch (IOException excecaoArquivo){
			// Em caso de erro de IO, considera que nenhum produto foi carregado
			produtoCadastrados = null;
			quantosProdutos = 0;
		}finally{
			// Garante o fechamento do arquivo, se ele tiver sido aberto
			if (arquivo != null) {
				arquivo.close();
			}
		}

		// Monta o vetor final que será usado pela aplicação
		Produto[] vetorProdutos;
		if (produtoCadastrados == null || quantosProdutos == 0) {
			// Caso nenhum produto tenha sido lido, retorna vetor vazio
			vetorProdutos = new Produto[0];
		} else {
			// Cria um vetor com espaço para os produtos carregados + novos produtos
			vetorProdutos = new Produto[quantosProdutos + MAX_NOVOS_PRODUTOS];
			for (i = 0; i < quantosProdutos; i++) {
				vetorProdutos[i] = produtoCadastrados[i];
			}
		}
		return vetorProdutos;
	}

	/** Lista todos os produtos cadastrados, numerados, um por linha */
	static void listarTodosOsProdutos() {
		cabecalho();
		System.out.println("\nPRODUTOS CADASTRADOS:");
		for (int i = 0; i < produtosCadastrados.length; i++) {
			if (produtosCadastrados[i] != null)
				System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
		}
	}

	/**
	 * Localiza um produto no vetor de cadastrados, a partir do nome, e imprime seus
	 * dados.
	 * A busca não é sensível ao caso. Em caso de não encontrar o produto, imprime
	 * mensagem padrão
	 */
	static void localizarProdutos() {
		String descricao;
		ProdutoNaoPerecivel produtoALocalizar;
		Produto produto = null;
		Boolean localizado = false;

		cabecalho();
		System.out.println("Informe a descrição do produto desejado");

		// Lê do teclado a descrição que o usuário deseja pesquisar
		descricao = teclado.nextLine();

		// Cria um "produto temporário" apenas com a descrição,
		// para poder usar o equals (que compara pela descrição)
		produtoALocalizar = new ProdutoNaoPerecivel(descricao, 0.01);

		// Percorre o vetor de produtos até encontrar a descrição ou chegar ao fim
		for(int i = 0;(i<quantosProdutos && !localizado); i++){
			if(produtosCadastrados[i].equals(produtoALocalizar)){
				localizado = true;
				produto = produtosCadastrados[i];
			}
		}

		// Exibe o resultado da busca
		if(!localizado){
			System.out.println("Produto não localizado!");
		}else{
			System.out.println(produto.toString());
		}
	}

	/**
	 * Rotina de cadastro de um novo produto: pergunta ao usuário o tipo do produto,
	 * lê os dados correspondentes,
	 * cria o objeto adequado de acordo com o tipo, inclui no vetor. Este método
	 * pode ser feito com um nível muito
	 * melhor de modularização. As diversas fases da lógica poderiam ser
	 * encapsuladas em outros métodos.
	 * Uma sugestão de melhoria mais significativa poderia ser o uso de padrão
	 * Factory Method para criação dos objetos.
	 */
	static void cadastrarProduto() {
		cabecalho();
		System.out.println("Cadastro de novo produto");
		System.out.println("1 - Produto não perecível");
		System.out.println("2 - Produto perecível");
		System.out.print("Digite o tipo de produto: ");

		// Lê o tipo escolhido (1 ou 2)
		int tipo = Integer.parseInt(teclado.nextLine());

		// Coleta os dados básicos comuns a qualquer produto
		System.out.print("Descrição: ");
		String descricao = teclado.nextLine();

		System.out.print("Preço de custo: ");
		double precoCusto = Double.parseDouble(teclado.nextLine().replace(",", "."));

		System.out.print("Margem de lucro (ex: 0.2 para 20%): ");
		double margemLucro = Double.parseDouble(teclado.nextLine().replace(",", "."));

		Produto novoProduto = null;

		// Cria o objeto adequado de acordo com o tipo informado
		if (tipo == 1) {
			// Produto sem data de validade
			novoProduto = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
		} else if (tipo == 2) {
			// Produto perecível: precisa da data de validade
			System.out.print("Data de validade (dd/MM/yyyy): ");
			String dataStr = teclado.nextLine();
			java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
			java.time.LocalDate validade = java.time.LocalDate.parse(dataStr, formatter);
			novoProduto = new ProdutoPerecivel(descricao, precoCusto, margemLucro, validade);
		} else {
			System.out.println("Tipo de produto inválido. Cadastro cancelado.");
			return;
		}

		// Tenta incluir o produto no vetor de cadastrados, se houver espaço
		if (quantosProdutos < produtosCadastrados.length) {
			produtosCadastrados[quantosProdutos] = novoProduto;
			quantosProdutos++;
			System.out.println("Produto cadastrado com sucesso!");
		} else {
			System.out.println("Não há espaço para novos produtos.");
		}
	}

	/**
	 * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve
	 * todo o conteúdo do arquivo.
	 * 
	 * @param nomeArquivo Nome do arquivo a ser gravado.
	 */
	public static void salvarProdutos(String nomeArquivo) {
		FileWriter escritor = null;
		try{
			// Abre (ou cria) o arquivo, sobrescrevendo o conteúdo anterior
			escritor = new FileWriter(nomeArquivo);

			// Primeira linha: quantidade de produtos cadastrados
			escritor.write(Integer.toString(quantosProdutos));
			escritor.write(System.lineSeparator());

			// Escreve uma linha para cada produto, usando o formato CSV definido em gerarDadosTexto()
			for (int i = 0; i < quantosProdutos; i++) {
				if (produtosCadastrados[i] != null) {
					escritor.write(produtosCadastrados[i].gerarDadosTexto());
					escritor.write(System.lineSeparator());
				}
			}
		}catch(IOException e){
			// Em caso de falha, exibe uma mensagem amigável na CLI
			System.out.println("Erro ao salvar produtos: " + e.getMessage());
		}finally{
			// Garante o fechamento do arquivo, evitando vazamento de recursos
			if (escritor != null) {
				try {
					escritor.close();
				} catch (IOException e) {
					// erro ao fechar pode ser ignorado aqui
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// Cria o scanner para ler a entrada do usuário no console
		teclado = new Scanner(System.in, Charset.forName("ISO-8859-2"));

		// Define o nome do arquivo CSV a ser utilizado
		nomeArquivoDados = "dadosProdutos.csv";

		// Carrega os produtos existentes do arquivo
		produtosCadastrados = lerProdutos(nomeArquivoDados);

		int opcao = -1;

		// Loop principal do menu até o usuário escolher sair (opção 0)
		do {
			opcao = menu();
			switch (opcao) {
				case 1 -> listarTodosOsProdutos();
				case 2 -> localizarProdutos();
				case 3 -> cadastrarProduto();
			}
			// Dá uma pausa antes de voltar ao menu
			pausa();
		} while (opcao != 0);

		// Ao finalizar, salva os dados atualizados de volta no arquivo
		salvarProdutos(nomeArquivoDados);

		// Encerra o scanner associado ao teclado
		teclado.close();
	}
}
