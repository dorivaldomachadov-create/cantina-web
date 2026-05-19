package com.cantina.servico;

import com.cantina.modelo.Produto;
import com.cantina.repositorio.ProdutoRepositorio;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueServico{
    private final ProdutoRepositorio produtoRepositorio;
    public EstoqueServico(ProdutoRepositorio produtoRepositorio){
        this.produtoRepositorio = produtoRepositorio;
    }
    public List<Produto> Listarprodutos(){
        return produtoRepositorio.findAll();
    }
    public Optional<Produto> BuscarPorId(Integer id){
        return produtoRepositorio.findById(id);
    }
    public Produto SalvarProduto(Produto produto){
        return produtoRepositorio.save(produto);
    }
    public void DeletarProduto(Integer id){
        produtoRepositorio.deleteById(id);
    }
}