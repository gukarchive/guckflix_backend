package guckflix.backend.repository;

import guckflix.backend.dto.CreditDto;
import guckflix.backend.entity.Credit;
import guckflix.backend.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CreditRepository implements CommonRepository<Credit, Long> {

    @Autowired
    EntityManager em;

    @Override
    public Long save(Credit entity){
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public Credit findById(Long id) {
        return em.find(Credit.class, id);
    }

    @Override
    public void remove(Credit entity){
        em.remove(entity);
    }

    public List<Credit> findByMovieId(Long movieId) {
        return em.createQuery("select c from Credit c join fetch c.actor where c.movie.id = :id", Credit.class)
                .setParameter("id", movieId)
                .getResultList();
    }

    public List<Credit> findByActorId(Long actorId) {
        return em.createQuery("select c from Credit c join fetch c.actor where c.actor.id = :actorId", Credit.class)
                .setParameter("actorId", actorId)
                .getResultList();
    }
}
