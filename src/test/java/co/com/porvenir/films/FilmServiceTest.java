package co.com.porvenir.films;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import co.com.porvenir.films.dto.FilmDto;
import co.com.porvenir.films.entity.FilmEntity;
import co.com.porvenir.films.repository.FilmRepository;
import co.com.porvenir.films.service.FilmServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class FilmServiceTest {

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT);
	private static final String FILM_URL = "https://swapi.dev/api/films/%s";
	
	@Mock
	private FilmRepository filmRepository;
	
	@Mock
    private RestTemplate restTemplate;

	@InjectMocks
	private FilmServiceImpl filmService;
	
	@BeforeEach
	public void setup() throws ParseException{
		when(filmRepository.findById(1))
			.thenReturn( Optional.of(new FilmEntity(1, 4, "A New Hope", FORMAT.parse("1977-05-25"))) );
		
		when(filmRepository.findById(3)).thenReturn( Optional.empty());
		when(filmRepository.findById(99)).thenReturn( Optional.empty());
		
		when(filmRepository.save(any(FilmEntity.class))).thenReturn(new FilmEntity(10, null, null, null));
		
		ResponseEntity<FilmDto> responseOk = ResponseEntity.ok().body(new FilmDto(4, "A New Hope", FORMAT.parse("1977-05-25")));
		when(restTemplate.exchange( eq(String.format(FILM_URL, 1)),
				any(HttpMethod.class),
				any(HttpEntity.class),
				eq(FilmDto.class)
			)).thenReturn(responseOk);
		
		//ResponseEntity<FilmDto> responseNotFound = ResponseEntity.status(HttpStatus.NOT_FOUND).body(new FilmDto(4, "A New Hope", FORMAT.parse("1977-05-25")));
		when(restTemplate.exchange( eq(String.format(FILM_URL, 99)),
			any(HttpMethod.class),
			any(HttpEntity.class),
			eq(FilmDto.class))
		).thenThrow( new TestHttpStatusCodeException(HttpStatus.NOT_FOUND, "Not found") );
	}
	
	@Test
	public void whenFilmByIdFoundToDelete_thenRecordDeleted(){
		assertTrue(filmService.deleteFilmById(1));
	}
	
	@Test
	public void whenFilmByIdNotFoundToDelete_thenNotRecordDeleted(){
		assertFalse(filmService.deleteFilmById(99));
	}
	
	@Test
	public void whenFilmByIdFoundToUpdate_thenRecordUpdated(){
		assertTrue(filmService.updateFilm(new FilmDto(), 1));
	}
	
	@Test
	public void whenFilmByIdNotFoundToUpdate_thenNotRecordUpdated(){
		assertFalse(filmService.updateFilm(any(FilmDto.class), 99));
	}
	
	@Test
	public void whenFilmByIdFound_thenRecordSaved() throws ParseException{
		
		int filmIdSearched = 3;
		FilmDto filmDto = new FilmDto(6, "Return of the Jedi", FORMAT.parse("1983-05-25"));
		FilmEntity filmEntity = new FilmEntity(filmIdSearched, filmDto.getEpisodeId(), filmDto.getTitle(), filmDto.getReleaseDate());
		when(filmRepository.save(any(FilmEntity.class))).thenReturn(filmEntity);
		
		Optional<FilmEntity> filmSaved = filmService.saveFilm(filmDto, filmIdSearched);
		assertTrue(filmSaved.isPresent());
		assertEquals(filmSaved, Optional.of(filmEntity));
	}
	
	@Test
	public void whenFilmByIdNotFound_thenNotRecordSaved() throws ParseException{
		
		int filmIdSearched = 1;
		FilmDto filmDto = new FilmDto(4, "A New Hope", FORMAT.parse("1977-05-25"));
		assertFalse(filmService.saveFilm(filmDto, filmIdSearched).isPresent());
	}
	
	@Test
	public void whenFilmByIdFoundInService_thenOkResponse() throws ParseException{
		
		int filmIdSearched = 1;
		FilmDto filmDto = new FilmDto(4, "A New Hope", FORMAT.parse("1977-05-25"));
		FilmDto filmServiceResponse = filmService.searchFilmById(filmIdSearched);
		assertEquals(filmServiceResponse.getEpisodeId(), filmDto.getEpisodeId());
		assertEquals(filmServiceResponse.getReleaseDate(), filmDto.getReleaseDate());
		assertEquals(filmServiceResponse.getTitle(), filmDto.getTitle());
	}
	
	@Test
	public void whenFilmByIdNotFoundInService_thenNoContentResponse() throws ParseException{
		int filmIdSearched = 99;
		assertThrows(RestBusinessException.class, () -> filmService.searchFilmById(filmIdSearched));
	}
}

class TestHttpStatusCodeException extends HttpStatusCodeException{

	public TestHttpStatusCodeException(HttpStatus statusCode, String statusText) {
		super(statusCode, statusText);
	}
}