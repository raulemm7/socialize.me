package org.example.labx.repository;

import org.example.labx.domain.Entity;
import org.example.labx.utils.Page;
import org.example.labx.utils.Pageable;

public interface FriendshipPagedRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    /**
     * retrieves all friendships for a user
     * @param id user's id
     * @param pageable object which descripts page number and size
     * @return page with friendships
     */
    Page<E> getFriendshipsOnPage(Long id, Pageable pageable);
}
