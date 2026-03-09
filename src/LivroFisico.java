import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Livro físico: possui uma data de devolução prevista.
 * Segue a mesma ideia de uma subclasse concreta de Produto.
 */
public class LivroFisico extends Livro {
    private LocalDate dataDevolucaoPrevista;

    public LivroFisico(String titulo, String autor, int anoPublicacao, LocalDate dataDevolucaoPrevista) {
        super(titulo, autor, anoPublicacao);
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String base = super.toString();
        base += "\nDevolução prevista: " + formatter.format(dataDevolucaoPrevista);
        return base;
    }

    @Override
    public String gerarDadosTexto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String data = formatter.format(dataDevolucaoPrevista);
        // tipo 1 indica livro físico
        return String.format("1;%s;%s;%d;%s", titulo, autor, anoPublicacao, data);
    }
}

