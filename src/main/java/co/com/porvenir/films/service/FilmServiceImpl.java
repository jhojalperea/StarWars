package co.com.porvenir.films.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import co.com.porvenir.films.RestBusinessException;
import co.com.porvenir.films.dto.FilmDto;
import co.com.porvenir.films.entity.FilmEntity;
import co.com.porvenir.films.model.ErrorMessage;
import co.com.porvenir.films.repository.FilmRepository;

@Service
public class FilmServiceImpl implements FilmService{

	private FilmRepository filmRepository;
	private RestTemplate restTemplate;
	
	
	@Autowired
	public FilmServiceImpl(FilmRepository filmRepository, RestTemplate restTemplate) {
		super();
		this.filmRepository = filmRepository;
		this.restTemplate = restTemplate;
	}	
	
	@Override
	public FilmDto searchFilmById(int id) throws RestBusinessException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", "application/json");
			headers.set("Content-Type", "application/json");

			ResponseEntity<FilmDto> response = 
					restTemplate.exchange( String.format("https://swapi.dev/api/films/%s", id),
							HttpMethod.GET,
							new HttpEntity<>(headers),
							FilmDto.class);
			
			saveFilm(response.getBody(), id);
			return response.getBody();

		}catch(HttpStatusCodeException e) {
			//HttpStatus 4xx, 5xx
			throw new RestBusinessException(
				new Gson().fromJson( e.getResponseBodyAsString(), ErrorMessage.class ), 
				e.getStatusCode(),
				"Not Found"
			);
		}
	}

	@Override
	public Optional<FilmEntity> saveFilm(FilmDto film, int id) {	
		Optional<FilmEntity> filmExists = filmRepository.findById(id);
		if( !filmExists.isPresent() ) {
			FilmEntity f = filmRepository.save( new FilmEntity(id, film.getEpisodeId(), film.getTitle(), film.getReleaseDate()) );
			return Optional.of(f);
		}
		return Optional.empty();
	}

	@Override
	public boolean updateFilm(FilmDto film, int id) {
		Optional<FilmEntity> filmExists = filmRepository.findById(id);
		if( filmExists.isPresent() ) {
			filmRepository.save( new FilmEntity(id, film.getEpisodeId(), film.getTitle(), film.getReleaseDate()) );
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteFilmById(int id) {
		Optional<FilmEntity> filmExists = filmRepository.findById(id);
		if( filmExists.isPresent() ) {
			filmRepository.deleteById(id);
			return true;
		}
		return false;
	}
}