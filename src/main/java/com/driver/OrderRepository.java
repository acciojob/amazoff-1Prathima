package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {

    HashMap<String, Order> orderDb = new HashMap<>();
    HashMap<String, DeliveryPartner> partnerDb = new HashMap<>();

    HashMap<String, List<String>> partnerOrderPair = new HashMap<>();
    HashMap<String, String> orderPartnerPair = new HashMap<>();

    public void addOrder(Order order){
        String key = order.getId();
        orderDb.put(key, order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        partnerDb.put(deliveryPartner.getId(),deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){

        List<String> orders = partnerOrderPair.get(partnerId);
        if(orders == null){
            orders = new ArrayList<>();
        }
        orders.add(orderId);
        partnerOrderPair.put(partnerId, orders);
        orderPartnerPair.put(orderId, partnerId);
    }

    public Order getOrderById(String orderId){
        Order order = orderDb.get(orderId);
        return order;
    }

    public DeliveryPartner getPartnerById(String partnerId){
        DeliveryPartner deliveryPartner = partnerDb.get(partnerId);
        return deliveryPartner;
    }

    public int getOrderCountByPartnerId(String partnerId){
        List<String> orders = partnerOrderPair.get(partnerId);
        return orders.size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orders = partnerOrderPair.get(partnerId);
        return orders;
    }

    public List<String> getAllOrders(){
        List<String> allOrders = new ArrayList<>();
        for(String orderId : orderDb.keySet()){
            allOrders.add(orderId);
        }
        return allOrders;
    }

    public int getCountOfUnassignedOrders(){
        int count = 0;
        for(String orderId : orderDb.keySet()){
            if(orderPartnerPair.get(orderId) == null){
                count++;
            }
        }
        return count;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int count = 0;
        String[] hourMin = time.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int convertedTime = hour * 60 + mins;
        for(Map.Entry<String, String> entry : orderPartnerPair.entrySet()){
            String partner = entry.getValue();
            if(partner.equals(partnerId)){
                String orderId = entry.getKey();
                if(orderDb.get(orderId).getDeliveryTime() > convertedTime){
                    count++;
                }

            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){

        int time = 0;
        for(Map.Entry<String, String> entry : orderPartnerPair.entrySet()){
            if(entry.getValue().equals(partnerId)){
                String orderId = entry.getKey();
                if(orderDb.get(orderId).getDeliveryTime() > time){
                    time = orderDb.get(orderId).getDeliveryTime();
                }
            }
        }
//        int hours = time / 10000;
//        int minutes = (time % 10000) / 100;
//        String convertedTime = String.format("%02d:%02d", hours, minutes);
        int hour = time/60;
        int min = time%60;
        String convertedTime = hour + ":" + min;
        return convertedTime;
    }

    public void deletePartnerById(String partnerId){
        partnerDb.remove(partnerId);
        for(String orderId : partnerOrderPair.get(partnerId)){
            orderPartnerPair.put(orderId, null);
        }
        partnerOrderPair.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        orderDb.remove(orderId);
        orderPartnerPair.remove(orderId);
        for(Map.Entry<String, List<String>> entry : partnerOrderPair.entrySet()){
            String partner = entry.getKey();
            for(String order : partnerOrderPair.get(partner)){
                if(order.equals(orderId)){
                    partnerOrderPair.get(partner).remove(orderId);
                }
            }
        }
    }
}
