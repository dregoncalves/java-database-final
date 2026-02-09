package com.project.code.Controller;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

// 3. Define the `addProduct` Method:
//    - Annotate with `@PostMapping` to handle POST requests for adding a new product.
//    - Accept `Product` object in the request body.
//    - Validate product existence using `validateProduct()` in `ServiceClass`.
//    - Save the valid product using `save()` method of `ProductRepository`.
//    - Catch exceptions (e.g., `DataIntegrityViolationException`) and return appropriate error message.
    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        if (!serviceClass.validateProduct(product)) {
            return Map.of("message", "Produto inválido");
        }

        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
           return Map.of("message", "Erro de integridade de dados");
        }
        return Map.of("message", "Produto salvo com sucesso!");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        if (!serviceClass.validateProductId(id)) {
            return Map.of("message", "Produto inválido");
        }
        return Map.of("product", productRepository.findById(id));
    }

 // 5. Define the `updateProduct` Method:
//    - Annotate with `@PutMapping` to handle PUT requests for updating an existing product.
//    - Accept updated `Product` object in the request body.
//    - Use `save()` method from `ProductRepository` to update the product.
//    - Return a success message with key `message` after updating the product.

    @Transactional
    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        if (!serviceClass.validateProductId(product.getId())) {
            return Map.of("message", "Produto inválido");
        }

        productRepository.save(product);
        return Map.of("message", "Produto atualizado com sucesso!");
    }


// 6. Define the `filterbyCategoryProduct` Method:
//    - Annotate with `@GetMapping("/category/{name}/{category}")` to handle GET requests for filtering products by `name` and `category`.
//    - Use conditional filtering logic if `name` or `category` is `"null"`.
//    - Fetch products based on category using methods like `findByCategory()` or `findProductBySubNameAndCategory()`.
//    - Return filtered products in a `Map<String, Object>` with key `products`.

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String category, @PathVariable String name) {
        List<Product> products;
        if (category.equals("null")) {
            products = productRepository.findProductBySubName(name);
        } else if (name.equals("null")) {
            products = productRepository.findByCategory(category);
        } else {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }
        return Map.of("products", products);
    }

    @GetMapping
    public Map<String, Object> listProduct() {
        return Map.of("products", productRepository.findAll());
    }

    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category, @PathVariable Long storeid) {
        return Map.of("product", productRepository.findProductByCategory(category, storeid));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        if (!serviceClass.validateProductId(id)) {
            return Map.of("message", "Produto inválido");
        }

        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);
        return Map.of("message", "Produto deletado com sucesso!");
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        return Map.of("products", productRepository.findProductBySubName(name));
    }
}
