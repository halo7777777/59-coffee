package com.code59.caffemall.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.code59.caffemall.bean.*;
import com.code59.caffemall.dao.OrderDetailDao;
import com.code59.caffemall.dao.OrderShopDao;
import com.code59.caffemall.dao.ShopStockDao;
import com.code59.caffemall.service.OrderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@DS("coffee_shop")
public class OrderServiceImpl implements OrderServices {

    @Autowired
    OrderDetailDao orderDetailDao;

    @Autowired
    OrderShopDao orderShopDao;

    @Autowired
    ShopStockDao shopStockDao;

    @Override
    public List<String> add(List<Cart> cartList, String id_shop, Guest guest, Discount discount){

        String s = UUID.randomUUID().toString();
        LocalDateTime time=LocalDateTime.now();
        LocalDateTime deliverytime=time.plusMinutes(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        String time_=deliverytime.format(formatter);
        String address;
        String phone;
        ShopStock shopStock;
        int state=1;
        double sum=0;
        String id_guest = null;
        Order_detail entry=new Order_detail();
        Order_shop entry_plus=new Order_shop();
        List<String> result=null;
        Iterator<Cart> cartIterator=cartList.iterator();

        while(cartIterator.hasNext()){
            Cart cart=cartIterator.next();
            shopStock=shopStockDao.selectById(cart.getIdFood());
            if(cart.getNumber()<shopStock.getNum())
            {
                state=0;
                if(result==null)
                {
                    result.add(0,"Fail");
                    result.add(cart.getIdFood());
                    result.add(String.valueOf(shopStock.getNum()));
                }
                else
                {
                    result.add(cart.getIdFood());
                    result.add(String.valueOf(shopStock.getNum()));
                }
            }
            else
            {
                sum+=cart.getPriceAfterDiscount();
                shopStock.setNum(shopStock.getNum()-cart.getNumber());
                shopStockDao.updateById(shopStock);
            }

        }

        if(state==0)
            return result;

        else
        {
            if (sum >= discount.getPayments_1() && sum < discount.getPayments_2())
                sum -= discount.getMinus_1();
            else if (sum >= discount.getPayments_2() && sum < discount.getPayments_3())
                sum -= discount.getminus_2();
            else if (sum >= discount.getPayments_3())
                sum -= discount.getMinus_3();

            //guest=guestDao.selectById(cartList.get(0).getId_guest());
            entry_plus.setId(s);
            entry_plus.setBeDeliver("0");
            entry_plus.setBeOver("0");
            entry_plus.setDeliverAddress(guest.getDelivery_address());
            entry_plus.setIdGuest(guest.getId());
            entry_plus.setIdShop(id_shop);
            entry_plus.setOrderType("0");
            entry_plus.setPhone(guest.getPhone());
            entry_plus.setTime(time_);
            entry_plus.setTotalPrice(sum);
            state = orderShopDao.insert(entry_plus);

            for (int i = 0; i < cartList.size() && state == 1; i++) {
                entry.setId_food(cartList.get(i).getIdFood());
                entry.setSinglePrice(cartList.get(i).getPriceAfterDiscount());
                entry.setNum(cartList.get(i).getNumber());
                entry.setTotalPrice(sum);
                entry.setIdOrder(s);
                state = orderDetailDao.insert(entry);
            }
            return result;
        }

    }


    @Override
    public int update(Order_shop order){
        return orderShopDao.updateById(order);
    }

    @Override
    public int delete(String id){
        QueryWrapper<Order_detail> columMap1 =new QueryWrapper<Order_detail>();
        columMap1.eq("id_order",id);
        List<Order_detail> list= orderDetailDao.selectList(columMap1);
        for(int i=0;i<list.size();i++)
        {
            ShopStock stock = shopStockDao.selectById(list.get(i).getIdFood());
            stock.setNum(stock.getNum()+list.get(i).getNum());
            shopStockDao.updateById(stock);
        }
        Map<String,Object>columMap=new HashMap<String,Object>();
        columMap.put("id_order",id);
        orderDetailDao.deleteByMap(columMap);
        return orderShopDao.deleteById(id);

    }

    @Override
    public Order_shop get(String id){
        return orderShopDao.selectById(id);
    }

    @Override
    public List<Order_detail> show(String id){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("id_order",id);
        return orderDetailDao.selectObjs(wrapper);
    }
}
