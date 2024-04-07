package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.Contact;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ContactRepositoryWithBagRelationshipsImpl implements ContactRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String CONTACTS_PARAMETER = "contacts";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Contact> fetchBagRelationships(Optional<Contact> contact) {
        return contact.map(this::fetchCompanies);
    }

    @Override
    public Page<Contact> fetchBagRelationships(Page<Contact> contacts) {
        return new PageImpl<>(fetchBagRelationships(contacts.getContent()), contacts.getPageable(), contacts.getTotalElements());
    }

    @Override
    public List<Contact> fetchBagRelationships(List<Contact> contacts) {
        return Optional.of(contacts).map(this::fetchCompanies).orElse(Collections.emptyList());
    }

    Contact fetchCompanies(Contact result) {
        return entityManager
            .createQuery("select contact from Contact contact left join fetch contact.companies where contact.id = :id", Contact.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Contact> fetchCompanies(List<Contact> contacts) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, contacts.size()).forEach(index -> order.put(contacts.get(index).getId(), index));
        List<Contact> result = entityManager
            .createQuery("select contact from Contact contact left join fetch contact.companies where contact in :contacts", Contact.class)
            .setParameter(CONTACTS_PARAMETER, contacts)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
