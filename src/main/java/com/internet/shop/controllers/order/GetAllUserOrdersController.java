package com.internet.shop.controllers.order;

import com.internet.shop.controllers.user.LoginController;
import com.internet.shop.lib.Injector;
import com.internet.shop.model.Order;
import com.internet.shop.service.OrderService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetAllUserOrdersController extends HttpServlet {
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private OrderService orderService =
            (OrderService) injector.getInstance(OrderService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Order> orderList = orderService
                .getUsersOrders((Long) req.getSession().getAttribute(LoginController.USER_ID));
        req.setAttribute("orders", orderList);
        req.getRequestDispatcher("/WEB-INF/views/orders/allOrders.jsp").forward(req, resp);
    }
}
