package co.com.porvenir.films;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import co.com.porvenir.films.controller.FilmController;
import co.com.porvenir.films.dto.FilmDto;
import co.com.porvenir.films.model.ErrorMessage;
import co.com.porvenir.films.service.FilmServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class FilmControllerTest {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT);
	
	@Mock
	private FilmServiceImpl filmService;
	
	@InjectMocks
	private FilmController filmController;
	
	private static ResponseEntity buildResponseEntity( HttpStatus statusCode, String message ){
		return ResponseEntity.status(statusCode)
			.body( new ErrorMessage(message));
	}
	
	@BeforeEach
	public void setup() throws RestBusinessException, ParseException{
		
		when(filmService.updateFilm(any(FilmDto.class), eq(1))).thenReturn(true);
		when(filmService.updateFilm(any(FilmDto.class), eq(3))).thenReturn(false);
		
		when(filmService.deleteFilmById(eq(1))).thenReturn(true);
		when(filmService.deleteFilmById(eq(3))).thenReturn(false);
		
		when(filmService.searchFilmById(eq(1))).thenReturn(new FilmDto(4, "A New Hope", FORMAT.parse("1977-05-25")));
		when(filmService.searchFilmById(eq(3)))
			.thenThrow(new RestBusinessException(new ErrorMessage("Not found"), HttpStatus.NOT_FOUND, "Not found"));
		when(filmService.searchFilmById(eq(99)))
			.thenThrow(new RestBusinessException(new ErrorMessage("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR, "Error"));
		
		when(filmService.updateFilm(any(FilmDto.class), eq(0))).thenThrow( new TestDataAccessException("Problemas con la base de datos") );
		when(filmService.deleteFilmById(eq(0))).thenThrow( new TestDataAccessException("Problemas con la base de datos") );
		when(filmService.searchFilmById(eq(0))).thenThrow( new TestDataAccessException("Problemas con la base de datos") );
	
	}
	
	@Test
	public void whenUpdateDone_thenSuccess() throws ParseException{
		assertEquals(filmController.updateFilm(1, new FilmDto()), 
			buildResponseEntity( HttpStatus.OK, "Registro actualizado en base de datos de manera satisfactoria" ));
	}
	
	@Test
	public void whenUpdateNotDone_thenFail() throws ParseException{
		assertEquals(filmController.updateFilm(3, new FilmDto()), 
			buildResponseEntity( HttpStatus.NOT_FOUND, "Registro NO encontrado en base de datos para ser actualizado" ));
	}
	
	@Test
	public void whenDeleteDone_thenSuccess() throws ParseException{
		assertEquals(filmController.deleteFilm(1), 
			buildResponseEntity( HttpStatus.OK, "Registro eliminado de base de datos de manera satisfactoria" ));
	}
	
	@Test
	public void whenDeleteNotDone_thenFail() throws ParseException{
		assertEquals(filmController.deleteFilm(3), 
			buildResponseEntity( HttpStatus.NOT_FOUND, "Registro NO encontrado en base de datos para ser eliminado" ));
	}
	
	@Test
	public void whenSearchRecordFound_thenSuccess() throws ParseException{
		
		ResponseEntity<FilmDto> responseEntity = (ResponseEntity<FilmDto>) filmController.findFilm(1);
		FilmDto filmControllerResponse = responseEntity.getBody();
		FilmDto filmDto = new FilmDto(4, "A New Hope", FORMAT.parse("1977-05-25"));
		
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		assertEquals(filmControllerResponse.getEpisodeId(), filmDto.getEpisodeId());
		assertEquals(filmControllerResponse.getReleaseDate(), filmDto.getReleaseDate());
		assertEquals(filmControllerResponse.getTitle(), filmDto.getTitle());
	}
	
	@Test
	public void whenSearchRecordFound_thenNoContent() throws ParseException{
		
		ResponseEntity<FilmDto> responseEntity = (ResponseEntity<FilmDto>) filmController.findFilm(3);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
	}
	
	@Test
	public void whenSearchError_thenInternalServerError() throws ParseException{
		
		ResponseEntity<FilmDto> responseEntity = (ResponseEntity<FilmDto>) filmController.findFilm(99);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void whenOperationError_thenDataAccessException() throws ParseException{
		
		ResponseEntity<FilmDto> responseEntity1 = (ResponseEntity<FilmDto>) filmController.findFilm(0);
		ResponseEntity<ErrorMessage> responseEntity2 = (ResponseEntity<ErrorMessage>) filmController.updateFilm(0, new FilmDto());
		ResponseEntity<ErrorMessage> responseEntity3 = (ResponseEntity<ErrorMessage>) filmController.deleteFilm(0);
		
		assertEquals(responseEntity1.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		assertEquals(responseEntity2.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		assertEquals(responseEntity3.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
class TestDataAccessException extends DataAccessException{

	public TestDataAccessException(String msg) {
		super(msg);
	}
}