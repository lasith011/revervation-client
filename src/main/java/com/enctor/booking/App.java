package com.enctor.booking;

import com.enctor.booking.response.ErrorResponse;
import com.enctor.booking.response.GenericResponse;
import com.enctor.booking.response.ReservationResponse;
import com.enctor.booking.util.BookingClient;
import com.enctor.booking.util.Task;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Log4j2
public class App {


    /**
     * The web service URL
     */
    private static final String apiUrl = "http://localhost:8080/api/booking";

    /**
     * The main method.
     */
    public static void main(String[] args) {
        // Get user inputs via scanner
        try (Scanner scan = new Scanner(System.in)) {
            int option = 0;
            while (option != 9) {
                printMainOptions();
                option = scan.nextInt();
                goToOption(scan, option);
            }
        } catch (Exception e) {
            log.log(Level.FATAL, "Error Occurred : ", e);
        }
    }

    /**
     * Print main options.
     */
    private static void printMainOptions() {
        System.out.println("Please select one option:");
        System.out.println();
        System.out.println("1. Check Seat Availability.");
        System.out.println("2. Reserve Tickets.");
        System.out.println("3. Bulk Tickets Reservation.");
        System.out.println("9. Exit.");
        System.out.println();
        System.out.print("Enter the option Num\t: ");
    }

    /**
     * Execute method by option number.
     */
    private static void goToOption(Scanner scan, int option) throws IOException {
        switch (option) {
            case 1:
                printAvailabilityCheckInputs(scan);
                break;
            case 2:
                printTicketReservationInputs(scan);
                break;
            case 3:
                printBulkTicketReservationInputs(scan);
                break;
            case 9:
                System.out.println("Bye");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option number");
                System.out.println();
        }
    }

    /**
     * Print availability check user inputs.
     */
    private static void printAvailabilityCheckInputs(Scanner scan) throws IOException {
        System.out.println();
        System.out.println("############### ENTER FOLLOWING DETAILS ###############");
        System.out.println();
        System.out.print("Enter origin\t\t: ");
        String origin = scan.next();
        System.out.print("Enter destination\t: ");
        String destination = scan.next();
        System.out.print("Enter date\t\t: ");
        String date = scan.next();
        System.out.print("Enter passenger count\t: ");
        String pCount = scan.next();
        checkAvailability("GET", origin, destination, date, pCount);
    }

    /**
     * Print ticket reservation user inputs.
     */
    private static void printTicketReservationInputs(Scanner scan) throws IOException {
        System.out.println();
        System.out.println("############### ENTER FOLLOWING DETAILS ###############");
        System.out.println();
        System.out.print("Enter origin\t\t: ");
        String origin = scan.next();
        System.out.print("Enter destination\t: ");
        String destination = scan.next();
        System.out.print("Enter date\t\t: ");
        String date = scan.next();
        System.out.print("Enter passenger count\t: ");
        String pCount = scan.next();
        checkAvailability("POST", origin, destination, date, pCount);
    }

    private static void printBulkTicketReservationInputs(Scanner scan) {
        System.out.println("############### ENTER FOLLOWING DETAILS ###############");
        System.out.println();
        System.out.print("Enter origin\t\t: ");
        String origin = scan.next();
        System.out.print("Enter destination\t: ");
        String destination = scan.next();
        System.out.print("Enter date\t\t: ");
        String date = scan.next();
        System.out.print("Enter passenger count\t: ");
        String pCount = scan.next();
        System.out.print("Enter request count\t: ");
        String tCount = scan.next();
        bulkTicketReservation(tCount, origin, destination, date, pCount);
    }

    private static void checkAvailability(String method, String origin, String destination, String date, String passengerCount) throws IOException {
        BookingClient bookingClient = new BookingClient(apiUrl);
        GenericResponse res = bookingClient.checkAvailability(method, origin, destination, date,
                passengerCount);
        printResponse(res);
    }

    /**
     * Bulk ticket reservation.
     */
    private static void bulkTicketReservation(String tCount, String origin, String destination,
                                              String date, String passengerCount) {
        int threadCount = Integer.parseInt(tCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        IntStream.range(0, threadCount).forEach(i -> {
            try {
                Future<GenericResponse> future = executorService.submit(new Task(apiUrl, "POST",
                        origin, destination, date, passengerCount));
                future.isDone();
                // Print the future result
                printResponse(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.log(Level.FATAL, "Error Occurred : ", e);
            }
        });
        executorService.shutdown();
    }

    /**
     * Print the response.
     */
    private static void printResponse(GenericResponse res) {
        if (res instanceof ReservationResponse) {
            ReservationResponse r = (ReservationResponse) res;
            log.log(Level.INFO, "Response Success");
            log.log(Level.INFO, r.toString());
        } else {
            ErrorResponse r = (ErrorResponse) res;
            log.log(Level.FATAL, "Response Error");
            log.log(Level.INFO, r.toString());

        }
    }

}
