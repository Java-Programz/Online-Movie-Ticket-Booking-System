package storage;

import models.Booking;
import models.Movie;
import models.Show;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    private static final String DATA_DIRECTORY =
            "data";

    private static final String BOOKINGS_FILE =
            DATA_DIRECTORY
                    + File.separator
                    + "bookings.dat";

    public List<Movie> loadMoviesFromCsv(
            String filePath
    ) {

        Map<String, Movie> movieMap =
                new LinkedHashMap<>();

        File sourceFile =
                resolveFile(filePath);

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(sourceFile)
                        )
        ) {

            // Ignore CSV header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                String[] values =
                        line.split(",", -1);

                if (values.length != 9) {

                    System.out.println(
                            "Skipping invalid CSV row: "
                                    + line
                    );

                    continue;
                }

                String movieCode =
                        values[0].trim();

                String movieName =
                        values[1].trim();

                String date =
                        values[2].trim();

                String showTime =
                        values[3].trim();

                int totalSeats =
                        Integer.parseInt(
                                values[4].trim()
                        );

                int availableSeats =
                        Integer.parseInt(
                                values[5].trim()
                        );

                double ticketPrice =
                        Double.parseDouble(
                                values[6].trim()
                        );

                String language =
                        values[7].trim();

                String genre =
                        values[8].trim();

                Movie movie =
                        movieMap.get(movieCode);

                if (movie == null) {

                    movie = new Movie(
                            movieCode,
                            movieName,
                            language,
                            genre
                    );

                    movieMap.put(
                            movieCode,
                            movie
                    );
                }

                movie.addShow(
                        new Show(
                                date,
                                showTime,
                                totalSeats,
                                availableSeats,
                                ticketPrice
                        )
                );
            }

        } catch (
                IOException |
                NumberFormatException e
        ) {

            throw new IllegalStateException(
                    "Unable to load movie CSV: "
                            + e.getMessage(),
                    e
            );
        }

        return new ArrayList<>(
                movieMap.values()
        );
    }

    @SuppressWarnings("unchecked")
    public List<Booking> loadBookings() {

        File file =
                resolveDataFile(
                        BOOKINGS_FILE
                );

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (
                ObjectInputStream input =
                        new ObjectInputStream(
                                new FileInputStream(file)
                        )
        ) {

            Object data =
                    input.readObject();

            if (data instanceof List<?>) {

                return (List<Booking>) data;
            }

        } catch (
                IOException |
                ClassNotFoundException e
        ) {

            System.out.println(
                    "Warning: saved bookings could not be loaded: "
                            + e.getMessage()
            );
        }

        return new ArrayList<>();
    }

    public boolean saveBookings(
            List<Booking> bookings
    ) {

        File file =
                resolveDataFile(
                        BOOKINGS_FILE
                );

        File parent =
                file.getParentFile();

        if (
                parent != null
                        && !parent.exists()
                        && !parent.mkdirs()
        ) {

            System.out.println(
                    "Could not create data directory: "
                            + parent.getAbsolutePath()
            );

            return false;
        }

        try (
                ObjectOutputStream output =
                        new ObjectOutputStream(
                                new FileOutputStream(file)
                        )
        ) {

            output.writeObject(
                    new ArrayList<>(bookings)
            );

            return true;

        } catch (IOException e) {

            System.out.println(
                    "Could not save bookings: "
                            + e.getMessage()
            );

            return false;
        }
    }

    private File resolveFile(
            String filePath
    ) {

        File file =
                new File(filePath);

        if (file.exists()) {
            return file;
        }

        File parentFile =
                new File(
                        "..",
                        filePath
                );

        if (parentFile.exists()) {
            return parentFile;
        }

        return file;
    }

    private File resolveDataFile(
            String path
    ) {

        File file =
                new File(path);

        if (
                file.exists()
                        || new File(DATA_DIRECTORY).exists()
        ) {

            return file;
        }

        File parentFile =
                new File(
                        "..",
                        path
                );

        if (
                parentFile.exists()
                        || new File(
                        "..",
                        DATA_DIRECTORY
                ).exists()
        ) {

            return parentFile;
        }

        return file;
    }
}