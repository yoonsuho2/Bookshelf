package com.cerdure.bookshelf.service.interfaces;

import com.cerdure.bookshelf.domain.order.Cart;
import com.cerdure.bookshelf.domain.order.Orders;
import com.cerdure.bookshelf.dto.order.CartDto;
import com.cerdure.bookshelf.dto.order.OrderDto;
import com.cerdure.bookshelf.dto.order.OrderItemDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {
    public List<Cart> findAllCart(Authentication authentication);
    public Cart findCart(Long bookId, Authentication authentication);
    public Object modifyCart(Long bookId, Integer amount, Authentication authentication);
    public void removeCart(CartDto cartDto, Authentication authentication);
    public void clearCart(Authentication authentication);
    public Orders findLastOrder(Authentication authentication);
    public List<Cart> newOrder(OrderDto orderDto, Authentication authentication);
    public String createCode(Authentication authentication);
    public OrderDto memberAndCode(Authentication authentication);
    public void createOrder(OrderDto orderDto, Authentication authentication) throws Exception;
    public Boolean saveOrderItems(OrderItemDto orderItemDto, Authentication authentication);
    public Integer restPoint(Integer point, Authentication authentication);
}
