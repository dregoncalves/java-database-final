package com.project.code.Controller;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        storeRepository.save(store);
        return Map.of("message", "Loja criada com sucesso!");
    }

    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        return storeRepository.findById(storeId) != null;
    }

    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        try {
            orderService.saveOrder(placeOrderRequest);
            return Map.of("message", "Pedido realizado com sucesso!");
        } catch (Exception e) {
            return Map.of("Error", e.getMessage());
        }
    }
}
