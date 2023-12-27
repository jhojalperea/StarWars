package co.com.porvenir.films.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FilmDto {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_ZONE = "America/Bogota";
	
	@Positive( message = "debe ser > 0" )
	@JsonProperty("episode_id")
	private Integer episodeId;
	
	@NotBlank(message = "No puede ser vacio o nulo")
	private String title;
	
	@JsonProperty("release_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT, timezone = TIME_ZONE)
	private Date releaseDate;

}