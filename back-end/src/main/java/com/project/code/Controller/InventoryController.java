package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ServiceClass serviceClass;

    @Transactional
    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        if (!serviceClass.validateProduct(combinedRequest.getProduct())){
            throw new RuntimeException("Produto inválido");
        }

        Inventory inventory = inventoryRepository.findByProductIdandStoreId(combinedRequest.getProduct().getId(), combinedRequest.getInventory().getStore().getId());

        if (inventory == null) {
            throw new RuntimeException("Não há dados disponíveis");
        }
        inventory.setStore(combinedRequest.getInventory().getStore());
        inventory.setStockLevel(combinedRequest.getInventory().getStockLevel());
        inventory.setProduct(combinedRequest.getProduct());
        inventoryRepository.save(inventory);
        return Map.of("message", "Inventário atualizado com sucesso!");
    }

    @Transactional
    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        if (!(serviceClass.validateInventory(inventory))) {
            return Map.of("message", "Os dados já existem");
        }

        inventoryRepository.save(inventory);
        return Map.of("message", "Dados salvos com sucesso!");
    }

    @GetMapping("/{storeId}")
    public Map<String, List<Product>> getAllProducts(@PathVariable Long storeId) {
        List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);
        List<Product> productList = new ArrayList<>();

        for (Inventory inventory : inventories) {
            productList.add(inventory.getProduct());
        }

        return Map.of("products", productList);
    }

    @GetMapping("filter/{category}/{name}/{storeId}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name, @PathVariable Long storeId) {
        List<Product> productList = new ArrayList<>();

        if (category.equals("null") && name.equals("null")) {
            List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);
            for (Inventory inventory : inventories) {
                productList.add(inventory.getProduct());
            }
        } else if (category.equals("null")) {
            List<Product> products = productRepository.findByNameLike(storeId, name);
            productList.addAll(products);
        } else if (name.equals("null")) {
            List<Product> products = productRepository.findProductByCategory(category, storeId);
            productList.addAll(products);
        } else {
            List<Product> products = productRepository.findByNameAndCategory(storeId, name, category);
            productList.addAll(products);
        }
        return Map.of("product", productList);
    }

    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        List<Product> productList = productRepository.findByNameLike(storeId, name);
        return Map.of("product", productList);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public Map<String, Object> removeProduct(@PathVariable Long id) {
        if (!serviceClass.validateProductId(id)) {
            return Map.of("message", "O produto não existe no banco ");
        }

        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        return Map.of("message", "O produto foi deletado!");
    }

    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Integer quantity, @PathVariable Long storeId, @PathVariable Long productId) {
        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (inventory == null) {
            return false;
        }

        return inventory.getStockLevel() >= quantity;
    }
}
