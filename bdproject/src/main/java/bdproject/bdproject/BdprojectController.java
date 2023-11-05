package bdproject.bdproject;

import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class BdprojectController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String mostrarAssociados(Model model) {
        List<Map<String, Object>> resultados = jdbcTemplate.queryForList("SELECT * FROM produto");
        model.addAttribute("resultados", resultados);
        return "components/home";
    }
    @GetMapping("/produto/{id}")
    public String mostrarAssociadoPorId(@PathVariable Long id, Model model) {
        String sql = "select p.nome as nomeProduto, p.preco as PrecoProduto, p.descricao as DescricaoProduto, c.nome as NomeCategoria\r\n" + //
                "from produto p \r\n" + //
                "join categoria c on p.fk_id_categoria = c.id_categoria\r\n" + //
                "where p.id_produto = ?";
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(sql, id);
        model.addAttribute("resultado", resultado);
        return "components/produto";
    }
}



