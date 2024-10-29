package com.xworkz.metro.repositry;

import com.xworkz.metro.entity.AddMetroEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@Repository
public class AddMetroRepoImpl implements AddMetroRepo{


    @Autowired
    EntityManagerFactory entityManagerFactory;


    @Override
    public boolean addMetro(AddMetroEntity addMetroEntity) {


        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(addMetroEntity);
            entityTransaction.commit();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            entityManager.close();
        }

    }
}
