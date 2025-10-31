package guckflix.backend.repository;

import guckflix.backend.entity.Video;
import guckflix.backend.entity.enums.ISO639;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class VideoRepository implements CommonRepository<Video, String> {

    @Autowired EntityManager em;

    @Override
    public String save(Video entity){
        if(entity.getId() == null){
            em.persist(entity);
        } else {
            em.merge(entity);
        }
        return entity.getId();
    }

    @Override
    public Video findById(Long id) {
        return em.find(Video.class, id);
    }

    @Override
    public void remove(Video entity){
        em.remove(entity);
    }

    public List<Video> findAllByMovieId(Long movieId, String locale){
        return em.createQuery("select v from Video v where v.movie.id = :id and v.iso639 = :locale", Video.class)
                .setParameter("id", movieId)
                .setParameter("locale", ISO639.valueOf(locale))
                .getResultList();
    }
}
