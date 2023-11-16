
            function openUserOptions() {
                var userOptions = document.getElementById("userOptions");
                userOptions.style.display = "flex";
        }
        window.onclick = function (event) {
            var userOptions = document.getElementById("userOptions");

            if (userOptions) {
                if (event.target !== userOptions && event.target !== document.getElementById("resultNomeLogin")) {
                    console.log("Closing user options");
                    userOptions.style.display = "none";
                    console.log('Window clicked');
                }
            }

            var cartSidebar = document.getElementById("cart-content");
            var cartSymbol = document.getElementById("cart-symbol");
            var cartWrapper = document.getElementById("cart-sidebar");
            var cartClose = document.getElementById("cartHeading");

            if (
                event.target !== cartSidebar &&
                event.target !== cartSymbol &&
                !cartSidebar.contains(event.target) &&
                !cartSymbol.contains(event.target)
            ) {
                console.log('Closing cart sidebar');
                cartWrapper.style.display = "none";
            }
            else if (
                event.target == cartClose &&
                cartClose.contains(event.target)
            ) {
                console.log('Closing cart sidebar');
                cartWrapper.style.display = "none";
            }
        };

        

        function toggleCartSidebar() {
            var cartSidebar = document.getElementById("cart-sidebar");
            console.log("funcao chamada")
            atualizarCarrinho();
            cartSidebar.style.display = "flex";
        }
        // var currentCartItems = [];
        // function atualizarCarrinho() {
        //     fetch('/carrinho') 
        //     .then(function (response) {
        //         return response.json();
        //     })
        //     .then(function (data) {
        //         var cartItems = data.itens;
        //         currentCartItems = cartItems.map(function(item) {
        //             return { id: item.id_produto, quantidade: item.quantidade, nome: item.nome, preco: item.preco };
        //         });
        //             if (currentCartItems.length === 0) {
        //                 // Show the empty cart section
        //                 emptyCartSection.style.display = 'block';
        //             } else {
        //                 // Hide the empty cart section
        //                 emptyCartSection.style.display = 'none';
        //             // 
        //             }
        //         }); 
        // }
        var currentCartItems = [];

function atualizarCarrinho() {
    fetch('/carrinho')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            var cartItems = data.itens;
            currentCartItems = cartItems.map(function (item) {
                return { id: item.id_produto, quantidade: item.quantidade, nome: item.nome, preco: item.preco };
            });

            var cartContent = document.getElementById('cart-content');
            var emptyCartSection = document.querySelector('.empty-cart');
            var productContainer = document.querySelector('.product-container');
            var cartBottom = document.querySelector('.cart-bottom');

            // Clear existing items in the cart
            productContainer.innerHTML = '';

            if (currentCartItems.length === 0) {
                cartBottom.style.display = 'none';
                emptyCartSection.style.display = 'block';
            } else {
                emptyCartSection.style.display = 'none';

                
                currentCartItems.forEach(function (item) {
                    var productDiv = document.createElement('div');
                    productDiv.classList.add('product');

                    var itemDescDiv = document.createElement('div');
                    itemDescDiv.classList.add('item-desc');

                    var flexDiv = document.createElement('div');
                    flexDiv.classList.add('flex', 'top');

                    var flexBot = document.createElement('div');
                    flexBot.classList.add('flex', 'bottom');  // Aqui corrigido para 'flexBot'

                    var nomeH5 = document.createElement('h5');
                    nomeH5.textContent = item.nome;

                    var precoH4 = document.createElement('h4');
                    precoH4.textContent = "R$" + item.preco;

                    var deleteButton = document.createElement('button');
                    deleteButton.textContent = 'Delete';
                    deleteButton.onclick = function () { deleteCartItem(item.id); };
                     // Assuming item.id is the unique identifier
                    var quantidade = document.createElement('h4');
                    quantidade.textContent = 'Quantidade: ' + item.quantidade;

                    flexDiv.appendChild(nomeH5);
                    flexDiv.appendChild(precoH4);

                    flexBot.appendChild(deleteButton);  // Adicionado o botão de delete ao flexBot
                    flexBot.appendChild(quantidade);  // Adicionado o botão de delete ao flexBot

                    itemDescDiv.appendChild(flexDiv);
                    itemDescDiv.appendChild(flexBot);  // Adicionado flexBot ao itemDescDiv
                    productDiv.appendChild(itemDescDiv);
                    productContainer.appendChild(productDiv);
                    cartBottom.style.display = 'block';

                    // Calculate and display subtotal
                    var subtotal = currentCartItems.reduce(function (acc, item) {
                        return acc + item.preco * item.quantidade;
                    }, 0);
                    var subtotalElement = document.querySelector('.cart-bottom .total h3:last-child');
                    subtotalElement.textContent = "R$" + subtotal;
                    });
            }
        });
}



        // Function to delete an item from the cart
        function deleteCartItem(productId) {
            fetch('/carrinho/remover/' + productId, {
                method: 'POST',
                headers: {
                    // CSRF header if needed; it depends on your Spring Security configuration
                    // 'X-CSRF-TOKEN': token
                }
            }).then(response => {
                return response.json(); // Assuming your endpoint returns a JSON response
            }).then(data => {
                alert(data.message); // Alert the message from the server
                var itemElement = document.getElementById('cart-item-' + productId);
                if (itemElement) {
                    itemElement.remove(); // This will remove the item from the DOM
                }
                atualizarCarrinho(); // Update the cart
            }).catch(error => {
                console.error('Error:', error);
            });
        }
        function compra(id_cliente) {
            var selectedShipping = document.getElementById("shipping").value;
            var itemsArray = [];
        
            if (selectedShipping) {
                // Array para armazenar os itens
        
                // Iterar através de currentCartItems e adicionar informações necessárias ao array de itens
                for (var i = 0; i < currentCartItems.length; i++) {
                    var currentItem = currentCartItems[i];
        
                    // Adicionar informações relevantes ao array de itens
                    var itemData = {
                        "id": currentItem.id,
                        "quantidade": currentItem.quantidade,
                    };
        
                    // Adicionar o objeto itemData ao array de itens
                    itemsArray.push(itemData);
                }
        
                console.log(itemsArray);
        
                // Criar objeto pedidoData com todas as informações necessárias
                var pedidoData = {
                    "id_cliente": id_cliente,
                    "id_transportadora": selectedShipping,
                    "itens": itemsArray, // Adicionar o array de itens ao objeto pedidoData
                };
        
                console.log(pedidoData);
        
                // Enviar dados para o servidor usando fetch
                fetch('/insertPedido', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(pedidoData),
                })
                .then(response => response.json())
                .then(data => {
                    if(data.sucesso) {
                        currentCartItems = [];
                        itemsArray = [];
                        showGifInCart();
                        document.getElementById("shipping").value = "";
                        console.log('Success:', data);
                    }
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        
            } else {
                console.error("Please choose a shipping company before proceeding to checkout.");
            }
        }
        
        function showGifInCart() {
            var gifContainer = document.getElementById("gif-container");
            var bottom = document.getElementById("cart-bottom");
            var cartContainer = document.getElementById("cart-content");
            var estiloOriginal = cartContainer.style.cssText;
        
            bottom.style.display = "none";
            cartContainer.style.padding = "0";
            gifContainer.style.display = "flex";
            gifContainer.innerHTML = "<img src='/images/ezgif.com-gif-maker.gif' alt='gif'>";
            // Salvar o estilo atual para que ele possa ser restaurado posteriormente
        
            // Remover o padding do cart-container
        
        
            setTimeout(function() {
                cartContainer.style.cssText = estiloOriginal;
                gifContainer.style.display = "none";
                atualizarCarrinho(); 
            }, 5000); 
        }
        
        document.addEventListener('DOMContentLoaded', function () {
            

            var limparCarrinhoButton = document.getElementById('limpar-carrinho');
            limparCarrinhoButton.addEventListener('click', function () {
                fetch('/carrinho/limpar', { method: 'POST' })
                    .then(function (response) {
                        return response.json();
                    })
                    .then(function (data) {
                        alert(data.message);
                        atualizarCarrinho();
                    });
            });

            // Initial cart update
            atualizarCarrinho();
        });