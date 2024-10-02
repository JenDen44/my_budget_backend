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
        var date = LocalDate.now();
        var purchase = new Purchase(Category.CLOTHE, 679.0, 2, date);
        var savedPurchase = repo.save(purchase);

        assertThat(savedPurchase).isNotNull();
    }

    @Test
    public void updatePurchase() {
        var id = 6;
        var purchase = repo.findById(id)
            .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        var newCost = purchase.getCost() * 2;

        purchase.setCost(newCost);
        purchase.setTotalCost(purchase.getTotalCost());

        var updatedPurchase = repo.save(purchase);

        assertThat(updatedPurchase.getCost() == newCost);
    }

    @Test()
    public void findPurchase() {
        var id = 3;
        var exception = assertThrows(PurchaseNotFoundException.class, () -> {
            repo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        });
        var expectedMessage = "Purchase with id " +  id + " is not found in DB";
        var actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void findAllPurchases() {
        var allPurchases = (List<Purchase>) repo.findAll();

        assertThat(!allPurchases.isEmpty());
    }

    @Test
    public void deletePurchase() {
        var id = 6;

        repo.deleteById(id);

        var exception = assertThrows(PurchaseNotFoundException.class, () -> {
            repo.findById(id)
                .orElseThrow (() -> new PurchaseNotFoundException("Purchase with id " + id + " is not found in DB"));
        });
        var expectedMessage = "Purchase with id 6 is not found in DB";
        var actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}