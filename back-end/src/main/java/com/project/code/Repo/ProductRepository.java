package com.project.code.Repo;

import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findByCategory(String category);

    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    public Product findBySku(String sku);

    public Product findByName(String name);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.name LIKE :pname")
    public List<Product> findByNameLike(Long storeId, String pname);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.product.category = :category")
    public List<Product> findByNameAndCategory(Long storeId, String pname, String category);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    public List<Product> findByCategoryAndStoreId(Long storeId, String category);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    public List<Product> findProductBySubName(String pname);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    public List<Product> findProductsByStoreId(Long storeId);

    @Query("SELECT i.product FROM Inventory i WHERE i.product.category = :category and i.store.id = :storeId")
    public List<Product> findProductByCategory(String category, Long storeId);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.category = :category")
    public List<Product> findProductBySubNameAndCategory(String pname, String category);
}
