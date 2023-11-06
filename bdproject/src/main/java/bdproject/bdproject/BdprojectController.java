package bdproject.bdproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes(value = {"user", "resultNomeLogin"})
public class BdprojectController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ModelAttribute("user")
    public CustomUser getUser() {
        return new CustomUser();
    }

    @GetMapping("/")
    public String home(Model model, @ModelAttribute("user") CustomUser user) {
        List<Map<String, Object>> resultados = jdbcTemplate.queryForList("SELECT * FROM produto");
        model.addAttribute("resultados", resultados);
        model.addAttribute("user", user);
        return "components/home";
    }

    @GetMapping("/produto/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        String sql = "select p.nome as nomeProduto, p.preco as PrecoProduto, p.descricao as DescricaoProduto, c.nome as NomeCategoria\r\n" + //
                "from produto p \r\n" + //
                "join categoria c on p.fk_id_categoria = c.id_categoria\r\n" + //
                "where p.id_produto = ?";
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(sql, id);
        model.addAttribute("resultado", resultado);
        return "components/produto";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "components/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("senha") String senha, @ModelAttribute("user") CustomUser user, Model model) {
        String sql = "select p.nome from pessoa p where p.email = ? and p.senha = ?";
        List<Map<String, Object>> resultNomeLogin = jdbcTemplate.queryForList(sql, email, senha);
        model.addAttribute("resultNomeLogin", resultNomeLogin);
        
        if (resultNomeLogin != null && !resultNomeLogin.isEmpty()) {
            user.setLoggedIn(true);
            return "redirect:/";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@ModelAttribute("user") CustomUser user) {
        user.setLoggedIn(false);
        return "redirect:/";
    }
}
