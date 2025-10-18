package Analizador;

public class Token {
	public TokenType tipo;
    public String valor;

    public Token(TokenType tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
    
    public TokenType getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return "<" + tipo + ", \"" + valor + "\">";
    }
}
