package guckflix.backend.repository;

import guckflix.backend.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class MemberRepository implements CommonRepository<Member, Long>{

    @Autowired
    EntityManager em;

    @Override
    public Long save(Member entity){
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    @Override
    public void remove(Member entity){
        em.remove(entity);
    }

    public List<Member> findByUsername(String username) {
        return em.createQuery("select m from Member m where username = :username", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

}
