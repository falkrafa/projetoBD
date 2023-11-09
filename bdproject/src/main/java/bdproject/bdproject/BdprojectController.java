package bdproject.bdproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes(value = {"user", "resultNomeLogin", "CartItem","carrinho", "resultNomeUpdate"})
public class BdprojectController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ModelAttribute("user")
    public CustomUser getUser() {
        return new CustomUser();
    }
    @ModelAttribute("carrinho")
    public Carrinho getCarrinho() {
        return new Carrinho();
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
        String sql = "select p.id_produto as idProduto, p.nome as nomeProduto, p.preco as PrecoProduto, p.descricao as DescricaoProduto, c.nome as NomeCategoria\r\n" + //
                "from produto p \r\n" + //
                "join categoria c on p.fk_id_categoria = c.id_categoria\r\n" + //
                "where p.id_produto = ?";
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(sql, id);
        model.addAttribute("resultado", resultado);
        return "components/produto";
    }

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("user") CustomUser user) {
        if (user.isLoggedIn()) {
            return "redirect:/";
        }
        return "components/login";
}

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestParam("email") String email, @RequestParam("senha") String senha, @ModelAttribute("user") CustomUser user, Model model) {
        Map<String, Object> response = new HashMap<>();
        
        String sql = "select p.nome, c.id_cliente from pessoa p join cliente c on c.cpf_pessoa_cliente = p.cpf where p.email = ? and p.senha = ?";
        List<Map<String, Object>> resultNomeLogin = jdbcTemplate.queryForList(sql, email, senha);
        model.addAttribute("resultNomeLogin", resultNomeLogin);
        
        if (resultNomeLogin != null && !resultNomeLogin.isEmpty()) {
            user.setLoggedIn(true);
            response.put("loggedIn", true);
        } else {
            response.put("loggedIn", false);
        }
        return response;
    }

    @GetMapping("/logout")
    public String logout(@ModelAttribute("user") CustomUser user, @ModelAttribute("carrinho") Carrinho carrinho) {
        user.setLoggedIn(false);
        carrinho.limparCarrinho();
        return "redirect:/";
    }

    @GetMapping("/carrinho")
    @ResponseBody
    public Map<String, Object> getCarrinho(@ModelAttribute("carrinho") Carrinho carrinho) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> itensCarrinho = carrinho.getItens();
        response.put("itens", itensCarrinho);
        return response;
    }

    @PostMapping("/carrinho/limpar")
    @ResponseBody
    public Map<String, Object> limparCarrinho(@ModelAttribute("carrinho") Carrinho carrinho) {
        Map<String, Object> response = new HashMap<>();
        carrinho.limparCarrinho();
        response.put("message", "Carrinho foi limpo com sucesso.");
        return response;
    }

    @PostMapping("/carrinho/remover/{id}")
    @ResponseBody
    public Map<String, Object> removerItem(@PathVariable Long id, @ModelAttribute("carrinho") Carrinho carrinho) {
        Map<String, Object> response = new HashMap<>();
        carrinho.removerItem(id);
        response.put("itens", carrinho.getItens()); // Send the updated items list back
        response.put("message", "Item removido com sucesso."); // Make sure this line is uncommented
        return response;
    }
    
    
    @PostMapping("/carrinho/adicionar/{id}")
    @ResponseBody
    public Map<String, Object> adicionarAoCarrinho(@PathVariable Long id, @ModelAttribute("carrinho") Carrinho carrinho, Model model) {
        Map<String, Object> response = new HashMap<>();

        carrinho.adicionarItem(jdbcTemplate, id);
        response.put("itens", carrinho.getItens());
        response.put("message", "Item adicionado com sucesso."); 
        return response;
    }

    @GetMapping("/registro")
    public String showRegisterPage(@ModelAttribute("user") CustomUser user) {
        if (user.isLoggedIn()) {
            return "redirect:/";
        }
        return "components/registro";
    }
    @PostMapping("/registro")
    @ResponseBody
    public Map<String, Object> register(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("cpf") String cpf,
            @RequestParam("user_type") String userType,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "phone2", required = false) String phone2,
            @RequestParam(value = "rua", required = false) String rua,
            @RequestParam(value = "numero", required = false) Integer numero,
            @RequestParam(value = "id-gerente", required = false) String idGerente,
            @RequestParam(value = "cargoG", required = false) String cargoG,
            @RequestParam(value = "cargo", required = false) String cargo) {
        
        Map<String, Object> response = new HashMap<>();

            String sql = "select p.nome from pessoa p where p.email = ?";
            List<Map<String, Object>> resultNomeregister1 = jdbcTemplate.queryForList(sql, email);
            if (resultNomeregister1 != null && !resultNomeregister1.isEmpty()) {
                response.put("error", "Já existe um usuário com este email.");
                return response;
            }

            String sql2 = "select p.nome from pessoa p where p.cpf = ?";
            List<Map<String, Object>> resultNomeRegister2 = jdbcTemplate.queryForList(sql2, cpf);
            if (resultNomeRegister2 != null && !resultNomeRegister2.isEmpty()) {
                response.put("error", "Já existe um usuário com este cpf.");
                return response;
            }

        String insertPessoaSql = "INSERT INTO pessoa (cpf, nome, email, senha) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertPessoaSql, cpf, name, email, password);

        // Verificar o tipo de usuário selecionado e inserir na tabela apropriada
        if ("cliente".equals(userType)) {
            System.out.println("entrei em cliente");
            String insertClienteSql = "INSERT INTO cliente (telefone, telefone2, rua, numero, cpf_pessoa_cliente) VALUES (?, ?, ?, ?, ?)";
            if(phone2.isEmpty()){
                phone2 = null;
            }
            jdbcTemplate.update(insertClienteSql, phone, phone2, rua, numero, cpf);
            System.out.println("depois do update");
        } 
        else if ("funcionario".equals(userType)) {
            String insertFuncionarioSql = "INSERT INTO funcionario (cargo, id_gerente, cpf_pessoa) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertFuncionarioSql, cargo, idGerente, cpf);
        } else if ("gerente".equals(userType)) {
            String insertGerenteSql = "INSERT INTO funcionario (cargo, id_gerente, cpf_pessoa) VALUES (?, null, ?)";           
            jdbcTemplate.update(insertGerenteSql, cargoG, cpf);
        }
        
        response.put("message", "Usuário registrado com sucesso.");
        return response;
    }

    @GetMapping("/updatePassword")
    public String showUpdatePasswordPage(@ModelAttribute("user") CustomUser user) {
        return "components/changePassword";
    }
    @PostMapping("/updatePassword")
    @ResponseBody
     public Map<String, Object> updatePassword (@RequestParam("email") String email, @RequestParam("password") String senha, @ModelAttribute("user") CustomUser user, Model model) {
        System.out.println(senha);
        System.out.println(email);
        Map<String, Object> response = new HashMap<>();
        if(!email.isEmpty() && senha.isEmpty() || senha == null){
            String sql = "select p.nome from pessoa p where p.email = ?";
            List<Map<String, Object>> resultNomeUpdate = jdbcTemplate.queryForList(sql, email);
            model.addAttribute("resultNomeUpdate", resultNomeUpdate);
            if(resultNomeUpdate.isEmpty()){
                response.put("existe", false);
            }
            response.put("existe", true);
        }
        else if(!senha.isEmpty()){
            String updatePasswordSql = "UPDATE pessoa SET senha = ? WHERE email = ?";
            jdbcTemplate.update(updatePasswordSql, senha, email);
            response.put("trocada", true);
            user.setLoggedIn(true);
        }
        return response;

    }
}