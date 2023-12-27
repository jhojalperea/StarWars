package co.com.porvenir.films.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Integer id;
	
	private Integer episodeId;
	
	private String title;

	private Date releaseDate;
}
