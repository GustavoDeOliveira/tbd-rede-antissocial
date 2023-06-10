package persistencia;

import negocio.Pessoa;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {
    
    private String uri = "bolt://localhost:7690";

    public Pessoa inserir(Pessoa p){
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        try (Session session = driver.session()) {
            Query query = new Query(
                "CREATE (p:Pessoa {cpf: $cpf, nome: $nome, email: $email, senha: $senha, dataNascimento: $dataNascimento}) return p",
                parameters("cpf", p.getCpf(), "nome", p.getNome(), "email", p.getEmail(), "senha", p.getSenha(), "dataNascimento", p.getDataNascimento()));
            Result result = session.run(query);
            if(result.hasNext()) {
                return extrairPessoa(result);
            }
        }
        return null;
    }
    
    public boolean atualizar(Pessoa p){
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        try (Session session = driver.session()) {
            Query query = new Query(
                "MATCH (p:Pessoa {cpf: $cpf})SET p.cpf = $cpf, p.nome = $nome, p.email = $email, p.senha = $senha, p.dataNascimento = $dataNascimento return p",
                parameters("cpf", p.getCpf(), "nome", p.getNome(), "email", p.getEmail(), "senha", p.getSenha(), "dataNascimento", p.getDataNascimento()));
            Result result = session.run(query);
            if(result.hasNext()) {
                return true;
            }
        }
        return false;
    }
    
    public void deletar(String cpf) {
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        try (Session session = driver.session()) {
            Query query = new Query("match(p:Pessoa {cpf: $cpf}) DETACH DELETE p;", parameters("cpf", cpf));
            session.run(query);
        }
    }

    public Pessoa obter(String cpf) {
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        Pessoa p = null;
        try (Session session = driver.session()) {
            Query query = new Query("MATCH(p:Pessoa {cpf: $cpf}) RETURN p", parameters("cpf", cpf));
            Result result = session.run(query);
            p = extrairPessoa(result);
        }
        return p;
    }

    public List<Pessoa> obterTodos() {
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        List<Pessoa> p = new ArrayList<>();
        try (Session session = driver.session()) {
            Query query = new Query("MATCH(p:Pessoa) RETURN p");
            Result result = session.run(query);
            while (result.hasNext()) {
                p.add(extrairPessoa(result));
            }
        }
        return p;
    }

    public boolean adicionarAmizade(Pessoa pessoa1, Pessoa pessoa2) {
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        try (Session session = driver.session()) {
            Query query = new Query(
                "MATCH(p1:Pessoa {cpf: $cpf1}), (p2:Pessoa {cpf: $cpf2}) MERGE (p1)-[a1:AMIGO]->(p2) MERGE (p2)-[a2:AMIGO]->(p1) RETURN a1,a2",
                parameters("cpf1", pessoa1.getCpf(), "cpf2", pessoa2.getCpf()));
            Result result = session.run(query);
            if (result.hasNext()) {
                return true;
            }
        }
        return false;
    }

    public List<Pessoa> obterAmizades(Pessoa p) {
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        List<Pessoa> amigos = new ArrayList<>();
        try (Session session = driver.session()) {
            Query query = new Query(
                "MATCH (:Pessoa {cpf: $cpf})-[:AMIGO]->(a:Pessoa) RETURN a",
                parameters("cpf", p.getCpf()));
            Result result = session.run(query);
            while (result.hasNext()) {
                amigos.add(extrairPessoa(result));
            }
        }
        return amigos;
    }

    public boolean desfazerAmizade(Pessoa pessoa1, Pessoa pessoa2) {
        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "password"));
        try (Session session = driver.session()) {
            Query query = new Query(
                "MATCH (p1:Pessoa {cpf: $cpf1})-[a1:AMIGO]->(p2:Pessoa {cpf: $cpf2}), (p2)-[a2:AMIGO]->(p1) DELETE a1,a2",
                parameters("cpf1", pessoa1.getCpf(), "cpf2", pessoa2.getCpf()));
            Result result = session.run(query);
            if (result.hasNext()) {
                return true;
            }
        }
        return false;
    }

    private Pessoa extrairPessoa(Result result) {
        if (result.hasNext()){
            Pessoa p = new Pessoa();
            Value v = result.next().get(0);
            p = new Pessoa();
            p.setCpf(v.get("cpf").asString());
            p.setNome(v.get("nome").asString());
            p.setEmail(v.get("email").asString());
            p.setSenha(v.get("senha").asString());
            p.setDataNascimento(v.get("dataNascimento").asLocalDate());
            return p;
        }
        return null;
    }
}
