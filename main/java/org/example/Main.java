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
            System.out.println("We get free places...");
            CopyOnWriteArrayList<String> result = new CopyOnWriteArrayList<String>();
            seats.forEach((key, value) -> {
                if(value.isAvailable)
                    result.add(key);
            });
            return result;
        });

        CompletableFuture<String> optimalSeatTask = availableSeatsTask.thenCompose(availableSeats -> {
            System.out.println("We get seats with a minimum price...");
            if(!availableSeats.isEmpty())
            {
                String seat = "";
                SeatInfo result = null;
                for (String availableSeat : availableSeats) {
                    SeatInfo tmp = seats.get(availableSeat);
                    if(result != null)
                    {
                        if(tmp.price < result.price)
                        {
                            result = tmp;
                            seat = availableSeat;
                        }
                    }
                    else {
                        result = tmp;
                        seat = availableSeat;
                    }
                }
                return CompletableFuture.completedFuture(seat);
            }
            return CompletableFuture.completedFuture("");
        });



        String optimalSeat = optimalSeatTask.join();
        if (optimalSeat.isEmpty()) {
            System.out.println("There are no available seats!");
        } else {
            seats.get(optimalSeat).isAvailable = false;
            System.out.println("Your seat: " + optimalSeat);
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