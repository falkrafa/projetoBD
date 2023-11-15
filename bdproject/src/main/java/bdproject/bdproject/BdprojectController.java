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
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@SessionAttributes(value = {"user", "resultNomeLogin", "CartItem","carrinho", "resultNomeUpdate","resultadoReview, ResultNomeGerente","resultProfileUpdate","nomeProfile","resultFunc","resultadoTransportadora"})
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

        List<Map<String, Object>> resultadoTransportadora = jdbcTemplate.queryForList("SELECT * FROM transportadora");
        model.addAttribute("resultadoTransportadora", resultadoTransportadora );
        model.addAttribute("user", user);
        return "components/home";
    }

    @GetMapping("/produto/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        String sql = "select p.id_produto as idProduto, p.nome as nomeProduto, p.preco as PrecoProduto, p.descricao as DescricaoProduto, c.nome as NomeCategoria\r\n" + //
                "from produto p \r\n" + //
                "join categoria c on p.fk_id_categoria = c.id_categoria\r\n" + //
                "where p.id_produto = ?";
        String sql2 = "select p.nome as nomeReview, r.descricao as descricaoReview, r.nota as nota, r.id_review as idReview,r2.resposta as Resposta,\r\n" + //
                "p2.nome as nomeFuncionario from cliente_faz_review_produto cfrp\r\n" + //
                "join cliente c on c.id_cliente = cfrp.fk_id_cliente  \r\n" + //
                "join pessoa p on p.cpf = c.cpf_pessoa_cliente \r\n" + //
                "join review r on cfrp.fk_id_review = r.id_review\r\n" + //
                "left join responde r2 on r2.fk_id_review = r.id_review \r\n" + //
                "left join funcionario f on r2.fk_id_funcionario = f.id_funcionario\r\n" + //
                "left join pessoa p2 on f.cpf_pessoa  = p2.cpf\r\n" + //
                "where cfrp.fk_id_produto = ?";
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(sql, id);
        List<Map<String, Object>> resultadoReview = jdbcTemplate.queryForList(sql2, id);
        model.addAttribute("resultado", resultado);
        model.addAttribute("resultadoReview", resultadoReview);
        return "components/produto";
    }

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("user") CustomUser user) {
        if (user.isLoggedIn() || user.isFuncLogged()) {
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
        
        String sql2 = "select p.nome, f.id_funcionario from pessoa p join funcionario f on f.cpf_pessoa = p.cpf where p.email = ? and p.senha = ?";
        List<Map<String, Object>> resultFunc = jdbcTemplate.queryForList(sql2, email, senha);
        model.addAttribute("resultFunc", resultFunc);
        
        if (resultNomeLogin != null && !resultNomeLogin.isEmpty() && resultFunc.isEmpty()) {
            user.setLoggedIn(true);
            user.setFuncLogged(false);
            response.put("loggedIn", true);
        } else if(resultFunc != null && !resultFunc.isEmpty() && resultNomeLogin.isEmpty()){
            user.setLoggedIn(false);
            user.setFuncLogged(true);
            response.put("loggedIn", true);
        } 
        else{
            response.put("loggedIn", false);
        }
        return response;
    }

    @GetMapping("/logout")
    public String logout(@ModelAttribute("user") CustomUser user, @ModelAttribute("carrinho") Carrinho carrinho) {
        user.setLoggedIn(false);
        user.setFuncLogged(false);
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
    public String showRegisterPage(@ModelAttribute("user") CustomUser user, Model model) {

        String sql = "select p. nome, f.id_funcionario from funcionario f \r\n" + //
                "join pessoa p on p.cpf = f.cpf_pessoa \r\n" + //
                "where cargo = 'Gerente'";
        List<Map<String, Object>> resultNomeGerente = jdbcTemplate.queryForList(sql);
        model.addAttribute("resultNomeGerente", resultNomeGerente);
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
        }
        return response;

    }

    @PostMapping("/makeReview")
    @ResponseBody
    public Map<String, Object> makeReview(@RequestBody Map<String, Object> reviewData) {
        Map<String, Object> response = new HashMap<>();

        String id_produto_reviewString = (String) reviewData.get("id_produto_review");
        String descricao = (String) reviewData.get("descricao");
        String notaString = (String) reviewData.get("nota");
        String id_clienteString = (String) reviewData.get("id_cliente");

        Long id_produto_review = Long.parseLong(id_produto_reviewString);
        Long nota = Long.parseLong(notaString);
        Long id_cliente = Long.parseLong(id_clienteString);

        System.out.println(id_produto_review);
        System.out.println(descricao);
        System.out.println(nota);

        String sql = "INSERT INTO review (descricao, nota) VALUES (?, ?);";
        jdbcTemplate.update(sql, descricao, nota);

        Long reviewId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Long.class);
        System.out.println(reviewId);

        String sql2 = "INSERT INTO cliente_faz_review_produto (fk_id_cliente, fk_id_produto, fk_id_review) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql2, id_cliente, id_produto_review, reviewId);

        response.put("sucesso", "Review feita com sucesso.");
        
        return response;
    }
    @PostMapping("/makeResponse")
    @ResponseBody
    public Map<String, Object> makeResponse(@RequestBody Map<String, Object> reviewData) {
        Map<String, Object> response = new HashMap<>();

        String id_review_string = (String) reviewData.get("id_review");
        String descricao = (String) reviewData.get("descricao");
        String id_func_string = (String) reviewData.get("id_func");

        Long id_review = Long.parseLong(id_review_string);
        Long id_func = Long.parseLong(id_func_string);

        System.out.println(id_review);
        System.out.println(descricao);

        String sql = "INSERT INTO Responde (fk_id_funcionario, fk_id_review, resposta) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql, id_func, id_review, descricao);

        response.put("sucesso", "Resposta feita com sucesso.");
        

        return response;
    }
        @GetMapping("/profile/{id}")
        public String showUpdateProfile(@PathVariable("id") Long id_cliente, @ModelAttribute("user")  CustomUser user, Model model) {

            // quero que o usuario veja os dados dele e tenha a opcao de modificar
            if( user.isLoggedIn() == false){
                return "redirect:/";
            }
            String sql = "select p.nome, p.email, c.telefone, c.telefone2, c.rua, c.numero, c.id_cliente from pessoa p join cliente c on c.cpf_pessoa_cliente = p.cpf where c.id_cliente = ?";
            
            List<Map<String, Object>> resultProfileUpdate = jdbcTemplate.queryForList(sql, id_cliente);
            model.addAttribute("resultProfileUpdate", resultProfileUpdate);
            return "components/profile";
        }
    @PostMapping("/atualizarInformacoes")
    @ResponseBody
    public Map<String, Object> atualizarInformacoes(@RequestBody Map<String, Object> updateData , Model model) {
        Map<String, Object> response = new HashMap<>();

        String id_clienteString = (String) updateData.get("id_cliente");
        String nome = (String) updateData.get("nome");
        String email = (String) updateData.get("email");
        String telefone = (String) updateData.get("telefone");
        String telefone2 = (String) updateData.get("telefone2");
        String rua = (String) updateData.get("rua");
        String numeroString = (String) updateData.get("numero");

        Long id_cliente = Long.parseLong(id_clienteString);
        Long numero = Long.parseLong(numeroString);

        String sql = "UPDATE pessoa p \r\n" + //
                "JOIN cliente c ON c.cpf_pessoa_cliente = p.cpf \r\n" + //
                "SET p.nome = ?, p.email = ? \r\n" + //
                "WHERE c.id_cliente = ?;\r\n" + //
                "";
        jdbcTemplate.update(sql, nome, email, id_cliente);

        String sql2 = "UPDATE cliente SET telefone = ?, telefone2 = ?, rua = ?, numero = ? WHERE id_cliente = ?";
        jdbcTemplate.update(sql2, telefone, telefone2, rua, numero, id_cliente);

        String sql3 = "select p.nome from pessoa p join cliente c on c.cpf_pessoa_cliente = p.cpf where c.id_cliente = ?";

        List<Map<String, Object>> nomeProfile = jdbcTemplate.queryForList(sql3, id_cliente);
        model.addAttribute("nomeProfile", nomeProfile);
        response.put("sucesso", "Informações atualizadas com sucesso.");
        
        return response;
    }

    @PostMapping("/insertPedido")
    @ResponseBody
    public Map<String, Object> insertPedido(@RequestBody Map<String, Object> pedidoData, Carrinho carrinho) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("entrei");
        String id_clienteString = (String) pedidoData.get("id_cliente");
        String id_transportadoraString = (String) pedidoData.get("id_transportadora");

        Long id_cliente = Long.parseLong(id_clienteString);
        Long id_transportadora = Long.parseLong(id_transportadoraString);
        System.out.println(id_cliente);
        System.out.println(id_transportadora);
        String sql = "INSERT INTO pedido (status, data_compra, data_prevista, fk_cliente_id, fk_id_transportadora)\r\n" + //
        "VALUES (\r\n" + //
        "    'Em processamento',\r\n" + //
        "    CURRENT_DATE,\r\n" + //
        "    DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY),\r\n" + //
        "    ?,\r\n" + //
        "    ?\r\n" + //
        ");";

        jdbcTemplate.update(sql, id_cliente, id_transportadora);
        Long pedidoId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Long.class);
        
        List<Map<String, Object>> items = castToItemsList(pedidoData.get("itens")); 
        for (Map<String, Object> item : items) {
            String id_produtoItemString = String.valueOf(item.get("id")); 
            String quantidade_String = String.valueOf(item.get("quantidade"));

            Long id_produtoItem = Long.parseLong(id_produtoItemString);
            Long quantidade = Long.parseLong(quantidade_String);

            String sql2 = "INSERT INTO contem (fk_id_produto, fk_id_pedido, quantidade) VALUES (?, ?, ?);";
            jdbcTemplate.update(sql2, id_produtoItem, pedidoId, quantidade);
        }
        carrinho.limparCarrinho();
        response.put("sucesso", "Pedido feito com sucesso.");
        
        return response;
    }
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> castToItemsList(Object items) {
        if (items instanceof List<?>) {
            return (List<Map<String, Object>>) items;
        } else {
            // Lida com o caso em que o objeto não é uma lista
            throw new IllegalArgumentException("O objeto não é uma lista de itens");
        }
    }
}