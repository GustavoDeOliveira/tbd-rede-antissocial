package apresentacao;

import negocio.Pessoa;
import java.time.LocalDate;

import persistencia.PessoaDAO;

public class Main {

    public static void main(String[] args) {
        var dao = new PessoaDAO();

        dao.deletar("01234567890");
        dao.deletar("98765432109");

        dao.inserir(new Pessoa("01234567890", "Igor", "igor@ifrs.br", "senha123", LocalDate.of(1980, 6, 15)));
        var p = dao.obter("01234567890");
        p.setNome("Igor Ávila");
        dao.atualizar(p);

        dao.inserir(new Pessoa("98765432109", "Gustavo", "gustavo@ifrs.br", "senha321", LocalDate.of(1999, 5, 14)));

        var pessoas = dao.obterTodos();
        dao.adicionarAmizade(pessoas.get(0), pessoas.get(1));

        System.out.println(pessoas);
        System.out.println("Amigos de " + pessoas.get(0));
        System.out.println(dao.obterAmizades(pessoas.get(0)));
        System.out.println("Desfazendo amizade");
        dao.desfazerAmizade(pessoas.get(0), pessoas.get(1));
        System.out.println("Amigos de " + pessoas.get(0));
        System.out.println(dao.obterAmizades(pessoas.get(0)));
    }
}