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

        if (!produto.isEmpty()) {
            Map<String, Object> produtoMap = produto.get(0);
            // Verifica se o carrinho já tem o produto
            boolean produtoExiste = false;
            for (Map<String, Object> item : itens) {
                if (item.get("id_produto").equals(produtoMap.get("id_produto"))) {
                    int quantidade = (int) item.getOrDefault("quantidade", 0);
                    item.put("quantidade", quantidade + 1);
                    produtoExiste = true;
                    break;
                }
            }
            if (!produtoExiste) {
                produtoMap.put("quantidade", 1); // Define a quantidade inicial como 1
                itens.add(produtoMap);
            }
        }
    }


    public void limparCarrinho() {
        itens.clear();
    }
    
    public void removerItem(Long produtoId) {
        // Assuming there's a map for item quantities where the key is the product ID
        for (Map<String, Object> item : itens) {
            Long itemId = ((Number) item.get("id_produto")).longValue();
            if (produtoId.equals(itemId)) {
                int quantity = (Integer) item.get("quantidade");
                if (quantity > 1) {
                    item.put("quantidade", quantity - 1);
                } else {
                    itens.remove(item);
                }
                break;
            }
        }
    }
    
    

    public List<Map<String, Object>> getItens() {
        return itens;
    }
}
