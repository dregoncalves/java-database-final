package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            Customer newCustomer = new Customer();
            newCustomer.setName(placeOrderRequest.getCustomerName());
            newCustomer.setEmail(placeOrderRequest.getCustomerEmail());
            newCustomer.setPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(newCustomer);
        }

        Store store = storeRepository.findById(placeOrderRequest.getStoreId());
        if (store == null) {
            throw new RuntimeException("Loja não encontrada");
        }

        OrderDetails orderDetails = new OrderDetails(customer, store, placeOrderRequest.getTotalPrice(), LocalDateTime.now());
        orderDetailsRepository.save(orderDetails);

        for (PurchaseProductDTO item : placeOrderRequest.getPurchaseProduct()) {
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(item.getId(), placeOrderRequest.getStoreId());
            if (inventory == null) {
                throw new RuntimeException("Inventário inválido");
            } else if (inventory.getStockLevel() < item.getQuantity()){
                throw new RuntimeException("Estoque insuficiente para o produto: " + item.getName());
            } else {
                inventory.setStockLevel(inventory.getStockLevel() - item.getQuantity());
                inventoryRepository.save(inventory);

                OrderItem orderItem = new OrderItem(orderDetails,
                        productRepository.findById(item.getId())
                                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + item.getId())),
                        item.getQuantity(),
                        item.getPrice());
                orderItemRepository.save(orderItem);
            }
        }
    }
}
