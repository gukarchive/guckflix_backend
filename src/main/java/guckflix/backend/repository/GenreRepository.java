package guckflix.backend.repository;

import guckflix.backend.entity.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Repository
public class GenreRepository implements CommonRepository<Genre, Long> {

    @Autowired
    EntityManager em;

    @Override
    public Long save(Genre entity){
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public Genre findById(Long id) {
        return em.find(Genre.class, id);
    }

    @Override
    public void remove(Genre entity){
        em.remove(entity);
    }

    public List<Genre> findAll(){
        return em.createQuery("select g from Genre g", Genre.class).getResultList();
    }
}
