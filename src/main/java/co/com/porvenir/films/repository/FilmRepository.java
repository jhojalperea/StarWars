package co.com.porvenir.films.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.porvenir.films.entity.FilmEntity;

public interface FilmRepository extends JpaRepository<FilmEntity, Integer> {
	
}
