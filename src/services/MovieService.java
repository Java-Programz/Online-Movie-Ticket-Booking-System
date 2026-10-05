package services;

import models.Movie;
import models.Show;
import storage.FileManager;

import java.util.Collections;
import java.util.List;

public class MovieService {

    private final FileManager fileManager;

    private List<Movie> movies;

    public MovieService(
            FileManager fileManager
    ) {

        this.fileManager = fileManager;
    }

    public void loadMovies(
            String csvPath
    ) {

        movies =
                fileManager.loadMoviesFromCsv(
                        csvPath
                );
    }

    public List<Movie> getMovies() {

        if (movies == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(
                movies
        );
    }

    public Movie getMovieByNumber(
            int number
    ) {

        if (
                movies == null
                        || number < 1
                        || number > movies.size()
        ) {

            return null;
        }

        return movies.get(number - 1);
    }

    public Show getShowByNumber(
            Movie movie,
            int number
    ) {

        if (
                movie == null
                        || number < 1
                        || number > movie.getShows().size()
        ) {

            return null;
        }

        return movie
                .getShows()
                .get(number - 1);
    }

    public Show findShow(
            String movieCode,
            String date,
            String time
    ) {

        if (movies == null) {
            return null;
        }

        for (Movie movie : movies) {

            if (
                    !movie
                            .getCode()
                            .equalsIgnoreCase(movieCode)
            ) {

                continue;
            }

            for (
                    Show show :
                    movie.getShows()
            ) {

                if (
                        show.getDate()
                                .equalsIgnoreCase(date)

                                &&

                        show.getTime()
                                .equalsIgnoreCase(time)
                ) {

                    return show;
                }
            }
        }

        return null;
    }
}