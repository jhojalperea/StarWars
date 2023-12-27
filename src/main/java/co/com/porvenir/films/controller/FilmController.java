package co.com.porvenir.films.controller;

import javax.validation.Valid;
import javax.validation.constraints.Max;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import co.com.porvenir.films.RestBusinessException;
import co.com.porvenir.films.dto.FilmDto;
import co.com.porvenir.films.model.ErrorMessage;
import co.com.porvenir.films.service.FilmServiceImpl;

@Validated
@RestController
@RequestMapping("/star-wars/film")
public class FilmController extends HandlerValidationController {

	private FilmServiceImpl filmService;
	
	@Autowired
	public FilmController(FilmServiceImpl filmService) {
		super();
		this.filmService = filmService;
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<?> findFilm(
			@Max(value = 999, message = "No debe ser mayor a 999")
			@PathVariable(name = "id") Integer id){
		try{
			return ResponseEntity
					.ok()
					.body(filmService.searchFilmById(id));
		}catch( RestBusinessException e ) {
			switch(e.getStatusCode()) {
				case NOT_FOUND:
					return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
				default:
					return ResponseEntity.status(e.getStatusCode())
							.body(e.getErrorMsg());
			}
		}catch( DataAccessException | ResourceAccessException e ) {
			return ResponseEntity.internalServerError()
					.body(new ErrorMessage(e.getMessage()));
		}
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<ErrorMessage> updateFilm(
			@Max(value = 999, message = "No debe ser mayor a 999")
			@PathVariable(name = "id") Integer id,
			@Valid @RequestBody FilmDto film){
		try{
			if(filmService.updateFilm(film, id)) {
				return ResponseEntity.ok()
					.body( new ErrorMessage("Registro actualizado en base de datos de manera satisfactoria"));
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body( new ErrorMessage("Registro NO encontrado en base de datos para ser actualizado"));

		}catch( DataAccessException e ) {
			return ResponseEntity.internalServerError()
					.body(new ErrorMessage(e.getMessage()));	
		}
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<ErrorMessage> deleteFilm(
			@Max(value = 999, message = "No debe ser mayor a 999")
			@PathVariable(name = "id") Integer id){
		try{
			if(filmService.deleteFilmById(id)) {
				return ResponseEntity.ok()
						.body(new ErrorMessage("Registro eliminado de base de datos de manera satisfactoria"));
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorMessage("Registro NO encontrado en base de datos para ser eliminado"));

		}catch( DataAccessException e ) {
			return ResponseEntity.internalServerError()
					.body(new ErrorMessage(e.getMessage()));	
		}
	}
	
}