package guckflix.backend.repository;

import guckflix.backend.entity.MovieGenre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class MovieGenreRepository implements CommonRepository<MovieGenre, Long>{

    @Autowired
    EntityManager em;

    @Override
    public Long save(MovieGenre entity) {
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public MovieGenre findById(Long id) {
        return null;
    }

    @Override
    public void remove(MovieGenre entity) {
        em.remove(entity);
    }

    public void flush(){
        em.flush();
    }
}
