package com.xworkz.metro.repositry;

import com.xworkz.metro.entity.AddLocationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;


@Repository
public class AddLocationRepoImpl implements AddLocationRepo{


    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Override
    public boolean saveLocation(AddLocationEntity addLocationEntity) {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(addLocationEntity);
            entityTransaction.commit();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            entityManager.close();
        }

    }

    @Override
    public List<AddLocationEntity> getAllLocationDetails() {
       EntityManager entityManager = entityManagerFactory.createEntityManager();
       try {
           EntityTransaction entityTransaction = entityManager.getTransaction();
           entityTransaction.begin();
           Query query=entityManager.createNamedQuery("ReadAllLocation");
           List<AddLocationEntity> addLocationEntities =  query.getResultList();
           entityTransaction.commit();
           return addLocationEntities;
       }catch (Exception e){
           return Collections.emptyList();
       }finally {
           entityManager.close();
       }
    }
}
