package org.example;

import java.util.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, SeatInfo> seats = new ConcurrentHashMap<>();
        seats.put("1A", new SeatInfo(true, 250.0));
        seats.put("1B", new SeatInfo(false, 200.0));
        seats.put("1C", new SeatInfo(true, 180.0));
        seats.put("2A", new SeatInfo(true, 300.0));
        seats.put("2B", new SeatInfo(false, 150.0));
        seats.put("2C", new SeatInfo(true, 100.0));

        CompletableFuture<CopyOnWriteArrayList<String>> availableSeatsTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("Отримуємо вільні місця...");
            CopyOnWriteArrayList<String> result = new CopyOnWriteArrayList<String>();
            seats.forEach((key, value) -> {
                if(value.isAvailable)
                    result.add(key);
            });
            return result;
        });

        CompletableFuture<List<Map.Entry<String, SeatInfo>>> priceSeatsTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("Отримуємо місця з мінімальною ціною...");
            List<Map.Entry<String, SeatInfo>> sortedSeats = new CopyOnWriteArrayList<>(seats.entrySet());
            sortedSeats.sort(Comparator.comparingDouble(entry -> entry.getValue().price));

            return sortedSeats;
        });
        CompletableFuture<String> optimalSeatTask = availableSeatsTask.thenCombine(priceSeatsTask, (availableSeats, minPriceSeats) -> {
            System.out.println("Шукаємо оптимальне місце...");
            if(!availableSeats.isEmpty())
            {
                for (Map.Entry<String, SeatInfo> seat : minPriceSeats) {
                    for (String availableSeat : availableSeats) {
                        if (seat.getKey().equals(availableSeat)) {
                            return availableSeat;
                        }
                    }
                }
            }
            return "";
        });
        if ((Objects.equals(optimalSeatTask.get(), ""))) {
            System.out.println("There are not available seats!");
        } else {
            System.out.println("Your seat: " + optimalSeatTask.get());
        }


    }
}
class SeatInfo {
    boolean isAvailable;
    double price;

    SeatInfo(boolean isAvailable, double price) {
        this.isAvailable = isAvailable;
        this.price = price;
    }
}