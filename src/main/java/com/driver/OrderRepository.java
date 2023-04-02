package com.driver;

import java.util.*;

public class OrderRepository {

    HashMap<String, Order> orderDb = new HashMap<>();  //orderId, order
    HashMap<String, DeliveryPartner> partnerDb = new HashMap<>();  //partnerId, partner

    HashMap<String, List<String>> partnerOrderPair = new HashMap<>();  //partnerId, List<orderId>
    HashSet<String> unassignedOrders = new HashSet<>();

    public void addOrder(Order order){
        String key = order.getId();
        orderDb.put(key, order);
        unassignedOrders.add(key);
    }

    public void addPartner(String partnerId){
        partnerDb.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId){

        List<String> orders = partnerOrderPair.get(partnerId);
        if(orders == null){
            orders = new ArrayList<>();
        }
        orders.add(orderId);
        partnerOrderPair.put(partnerId, orders);
        unassignedOrders.remove(orderId);
    }

    public Order getOrderById(String orderId){
        Order order = orderDb.get(orderId);
        return order;
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partnerDb.get(partnerId);
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
       return unassignedOrders.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int count = 0;
//        int numericalTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        String[] hourMin = time.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int convertedTime = hour * 60 + mins;
       for(String orderId : partnerOrderPair.get(partnerId)){
           if(orderDb.get(orderId).getDeliveryTime() > convertedTime){
               count++;
           }
       }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){

        int time = 0;
        for(String orderId : partnerOrderPair.get(partnerId)){
            if(orderDb.get(orderId).getDeliveryTime() > time){
                time = orderDb.get(orderId).getDeliveryTime();
            }
        }
        int hours = time / 60;
        int minutes = (time % 60);
        String convertedTime = String.format("%02d:%02d", hours, minutes);
        return convertedTime;
    }

    public void deletePartnerById(String partnerId){
        partnerDb.remove(partnerId);
        if(partnerOrderPair.containsKey(partnerId)) {
            for (String orderId : partnerOrderPair.get(partnerId)) {
                unassignedOrders.add(orderId);
            }
        }
        partnerOrderPair.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        orderDb.remove(orderId);
        if(unassignedOrders.contains(orderId)){
            unassignedOrders.remove(orderId);
        }
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
