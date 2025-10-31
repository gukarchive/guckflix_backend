package guckflix.backend.repository;

import guckflix.backend.dto.paging.PagingRequest;
import guckflix.backend.dto.paging.Slice;
import guckflix.backend.entity.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class ActorRepository implements CommonRepository <Actor, Long>{

    @Autowired
    EntityManager em;

    @Override
    public Long save(Actor entity) {
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public Actor findById(Long id) {
        return em.find(Actor.class, id);
    }

    @Override
    public void remove(Actor entity) {
        em.remove(entity);
    }

    public Actor findActorDetailById(Long id){
        return em.createQuery("select a from Actor a" +
                        " left join fetch a.credits c" +
                        " left join fetch c.movie m" +
                        " where a.id = :id", Actor.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Actor> findAllByIds(List<Long> ids) {
        return em.createQuery("select a from Actor a where id in :ids", Actor.class).setParameter("ids", ids).getResultList();
    }

    public Slice<Actor> findByKeyword(String keyword, PagingRequest paging) {
        List<Actor> list = em.createQuery("select a from Actor a where a.name like :keyword", Actor.class)
                .setParameter("keyword", "%"+keyword+"%")
                .setFirstResult(paging.getOffset())
                .setMaxResults(paging.getLimit()+1)
                .getResultList();

        /**
         * Slice는 limit보다 한 개 더 가져와서 다음 페이지가 있는지 확인함
         */
        boolean hasNext = list.size() > paging.getLimit() ? true : false;
        if (hasNext == true) list.remove(list.size()-1);
        return new Slice<>(hasNext, paging.getRequestPage(), list, paging.getLimit());
    }
}
