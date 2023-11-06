package bdproject.bdproject;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Carrinho {
    private List<Map<String, Object>> itens = new ArrayList<>();

    public void adicionarItem(JdbcTemplate jdbcTemplate, Long produtoId) {
        String sql = "SELECT * FROM produto WHERE id_produto = ?";
        List<Map<String, Object>> produto = jdbcTemplate.queryForList(sql, produtoId);
        
        // System.out.println(produtoId);
        // if (!produto.isEmpty()) {
        //     Map<String, Object> produtoMap = produto.get(0);
        //     // if(produtoMap.containsKey("id_produto") ) {
        //     // }
        // }
        // itens.add(produto.get(0));
    }

    public void limparCarrinho() {
        itens.clear();
    }
    
    public void removerItem(int index) {
        if (index >= 0 && index < itens.size()) {
            itens.remove(index);
        }
    }

    public List<Map<String, Object>> getItens() {
        return itens;
    }
}
