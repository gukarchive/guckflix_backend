package guckflix.backend.repository;

public interface CommonRepository<T, K> {

    public K save(T entity);

    public T findById(Long id);

    public void remove(T entity);

}
