package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Movie {

    private final String code;
    private final String name;
    private final String language;
    private final String genre;

    private final List<Show> shows;

    public Movie(
            String code,
            String name,
            String language,
            String genre
    ) {

        this.code = code;
        this.name = name;
        this.language = language;
        this.genre = genre;

        this.shows = new ArrayList<>();
    }

    public void addShow(Show show) {
        shows.add(show);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public String getGenre() {
        return genre;
    }

    public List<Show> getShows() {
        return Collections.unmodifiableList(shows);
    }
}