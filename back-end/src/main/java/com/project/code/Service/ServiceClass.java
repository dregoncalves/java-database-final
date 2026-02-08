package com.project.code.Service;


import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public boolean validateInventory(Inventory inventory) {
        return inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(), inventory.getStore().getId()) == null;
    }

    public boolean validateProduct(Product product) {
        return productRepository.findByName(product.getName()) == null;
    }

    public boolean validateProductId(long id) {
        return !(productRepository.findById(id) == null);
    }

    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(), inventory.getStore().getId());
    }


    public ServiceClass(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }
}
