package guckflix.backend.repository;

import guckflix.backend.entity.AdminMemo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminMemoRepository extends JpaRepository<AdminMemo, Long> {

}
