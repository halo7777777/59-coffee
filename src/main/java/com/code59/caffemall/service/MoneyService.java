package com.code59.caffemall.service;

import com.code59.caffemall.bean.Discount;
import com.code59.caffemall.bean.Food;
import com.code59.caffemall.bean.Order;
import com.code59.caffemall.controller.Order.tempVar.FoodlistForRank;

import java.util.List;

public interface MoneyService {
    public int set_single_discount(double discount, String id_food);
    public Discount set_total_discount1(Discount discount,
                                        int payments1, int minus1,
                                        int payments2, int minus2,
                                        int payments3, int minus3);
    public List<Order> moneyflow(String start_time, String end_time);
    public List<FoodlistForRank> ranking();
    public void quicksort(List<String> listOfID,List<Integer> listOfNum,int low,int high);
}