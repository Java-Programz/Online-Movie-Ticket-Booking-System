import services.BookingService;
import services.MovieService;
import services.SeatService;
import storage.FileManager;
import ui.ConsoleUI;

public class Main {

    private static final String MOVIE_CSV = "Movie Reservation Dataset.csv";

    public static void main(String[] args) {

        try {
            FileManager fileManager = new FileManager();

            // Load movies from CSV
            MovieService movieService = new MovieService(fileManager);
            movieService.loadMovies(MOVIE_CSV);

            // Create seat service
            SeatService seatService = new SeatService();

            // Create booking service
            BookingService bookingService = new BookingService(
                    movieService,
                    seatService,
                    fileManager
            );

            // Start console interface
            ConsoleUI consoleUI = new ConsoleUI(
                    movieService,
                    bookingService,
                    seatService
            );

            consoleUI.start();

        } catch (Exception e) {
            System.out.println(
                    "The program could not start: " + e.getMessage()
            );
        }
    }
}