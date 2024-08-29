package com.melnikov.bulish.my.budget.my_budget_backend.purchase;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@Rollback(value = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PurchaseRepositoryTest {

    @Autowired
    PurchaseRepository repo;

    @Test
    public void createPurchase() {
        LocalDate date = LocalDate.now();
        Purchase purchase = new Purchase(Category.CLOTHE, 679.0, 2, date);

        Purchase savedPurchase = repo.save(purchase);
        assertThat(savedPurchase).isNotNull();


    }

    @Test
    public void updatePurchase() {
        Integer id = 6;
        Purchase purchase = repo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        Double newCost = purchase.getCost() * 2;
        purchase.setCost(newCost);
        purchase.setTotalCost(purchase.getTotalCost());
        Purchase updatedPurchase = repo.save(purchase);

        assertThat(updatedPurchase.getCost() == newCost);

    }

    @Test()
    public void findPurchase() {
        Integer id = 3;
        PurchaseNotFoundException exception = assertThrows(PurchaseNotFoundException.class, () -> {
            Purchase purchase = repo.findById(id)
                    .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        });

        String expectedMessage = "Purchase with id " +  id + " is not found in DB";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void findAllPurchases() {
        List<Purchase> allPurchases = repo.findAll();

        assertThat(!allPurchases.isEmpty());


    }

    @Test
    public void deletePurchase() {
        Integer id = 6;
        repo.deleteById(id);
        PurchaseNotFoundException exception = assertThrows(PurchaseNotFoundException.class, () -> {
            Purchase purchase = repo.findById(id)
                    .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        });

        String expectedMessage = "Purchase with id 6 is not found in DB";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}