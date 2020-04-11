package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Renvoie le Compte  en fonction de son numéro
 * s'il est présent dans la liste
 */
class CompteComptableTest {

    private static CompteComptable vCompte;
    private static CompteComptable compte2;
    private static List<CompteComptable> vList;

    @BeforeEach
    void init() {
        vCompte = new CompteComptable();
        vCompte.setNumero(401);
        vCompte.setLibelle("Fournisseurs");
        vList = new ArrayList<>(0);
        vList.add(vCompte);
        vList.add(new CompteComptable(411, "Clients"));

        compte2 = new CompteComptable();
        compte2.setNumero(401);
        compte2.setLibelle("Fournisseurs");
    }

    @AfterAll
    static void tearDownAll() {
        vCompte = null;
        vList.clear();
    }

    @Test
    void getByNumero() {
        assertEquals(CompteComptable.getByNumero(vList, 401).getLibelle(),
                "Fournisseurs");
    }

    @Test
    void getByNumeroTest() {
        assertEquals(CompteComptable.getByNumero(vList, 401),
                compte2);
    }
}