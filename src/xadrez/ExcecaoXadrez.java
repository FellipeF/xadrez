package xadrez;

import jogo.ExcecaoTabuleiro;

public class ExcecaoXadrez extends ExcecaoTabuleiro {
    private static final long serialVersionUID = 1L;
    
    public ExcecaoXadrez(String msg)
    {
        super(msg);
    }
}
