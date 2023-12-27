package co.com.porvenir.films.service;

import java.util.Optional;

import co.com.porvenir.films.RestBusinessException;
import co.com.porvenir.films.dto.FilmDto;
import co.com.porvenir.films.entity.FilmEntity;

public interface FilmService {

	FilmDto searchFilmById(int id) throws RestBusinessException;

	Optional<FilmEntity> saveFilm(FilmDto film, int id);

	boolean updateFilm(FilmDto film, int id);

	boolean deleteFilmById(int id);
	
}
