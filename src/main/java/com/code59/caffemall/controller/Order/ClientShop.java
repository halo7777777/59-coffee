package com.code59.caffemall.controller.Order;

import com.alibaba.fastjson.JSON;
import com.code59.caffemall.bean.Order_detail;
import com.code59.caffemall.bean.Order_shop;
import com.code59.caffemall.controller.Order.ClientAndShop.OrderTemp;
import com.code59.caffemall.service.OrderServices;
import com.code59.caffemall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClientShop {
    @Autowired
    OrderServices orderServices;
    @Autowired
    UserService userService;
//    @RequestMapping("/SearchShopOrder1111111")
//    public String showorders(@RequestBody String guestId)
//    {
//        List<Order_detail>orders=orderServices.show(guestId);
//        return JSON.toJSONString(orders);
//    }

    @RequestMapping("/SearchShopOrder")
    public String SearchShoporder(@RequestBody String shopid) {
        System.out.println(shopid + "shopid");
        shopid = shopid.substring(0, shopid.length() - 1);
        List<Order_shop> ordershoplist = orderServices.showOrderShops(shopid);
        List<OrderTemp> list = convertToOrderTemp(ordershoplist);
        list.forEach(t -> {
            System.out.println(t);
        });
        return JSON.toJSONString(list);
//        List<Order_shop>list=orderServices.showOrderShops(shopid);
//        return JSON.toJSONString(list);
    }

    @RequestMapping("/Deliver")
    public String delever(@RequestBody String orderId) {
        orderId = orderId.substring(0, orderId.length() - 1);
        System.out.println(orderId + "\n\n\nordersid");
        Order_shop orderShopTemp = orderServices.get(orderId);
        if (orderShopTemp != null) {
            orderShopTemp.setBeDeliver("y");
        }
        if (orderServices.update(orderShopTemp) == 1)
            return "ok";
        else
            return "fail";

    }

    @RequestMapping("/OrderDetail")
    public String orderdetail(@RequestBody String orderId) {
        orderId=orderId.substring(0,orderId.length()-1);
        List<Order_detail>order_details=orderServices.show(orderId);
//        Order_detail order_detail=order_details.get(0);
//        System.out.println("1");
//        //order_details=null;
//        for(int i=0;i<order_details.size();i++)
//        {
//            System.out.println(order_details.get(i).getIdFood()+"food");
//            System.out.println(order_details.get(i).getIdOrder()+"order");
//        }
        System.out.println("1");
        order_details.forEach(order_detail -> {
            System.out.println(order_detail);
        });
        System.out.println("2");
        return JSON.toJSONString(order_details);
    }


    public List<OrderTemp> convertToOrderTemp(List<Order_shop> orderdetails) {
        List<OrderTemp> orderTemps = new ArrayList<>();
        orderdetails.forEach(order_shop -> {
            String guestName = userService.get(order_shop.getIdGuest()).getName();
            OrderTemp ot = new OrderTemp(order_shop.getId(), order_shop.getTime(), guestName, order_shop.getDeliverAddress(), order_shop.getPhone(),
                    order_shop.getTotalPrice(), order_shop.getBeDeliver(), order_shop.getBeOver());
            orderTemps.add(ot);
        });
        return orderTemps;
    }
}

