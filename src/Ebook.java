/**
 * Ebook: não tem data de devolução, mas registra o tamanho em MB.
 */
public class Ebook extends Livro {
    private double tamanhoMB;

    public Ebook(String titulo, String autor, int anoPublicacao, double tamanhoMB) {
        super(titulo, autor, anoPublicacao);
        this.tamanhoMB = tamanhoMB;
    }

    @Override
    public String toString() {
        String base = super.toString();
        base += String.format("\nTamanho do arquivo: %.2f MB", tamanhoMB);
        return base;
    }

    @Override
    public String gerarDadosTexto() {
        // tipo 2 indica ebook
        return String.format("2;%s;%s;%d;%.2f", titulo, autor, anoPublicacao, tamanhoMB);
    }
}

