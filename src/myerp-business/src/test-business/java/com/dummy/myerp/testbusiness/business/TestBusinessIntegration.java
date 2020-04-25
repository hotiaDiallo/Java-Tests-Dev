package com.dummy.myerp.testbusiness.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ContextConfiguration(locations = "classpath:bootstrapContext.xml")
class TestBusinessIntegration extends BusinessTestCase {

    private static ComptabiliteDaoImpl dao;
    private static ComptabiliteManagerImpl manager;
    private static ComptabiliteManager managerIntegration;
    private static EcritureComptable vEcritureComptable;
    private static Date vCurrentDate;
    private static Integer vCurrentYear;

    @BeforeAll
    static void initAll() {
        dao = new ComptabiliteDaoImpl();
        manager = new ComptabiliteManagerImpl();
        managerIntegration = getBusinessProxy().getComptabiliteManager();
        vCurrentDate = new Date();
        vCurrentYear = LocalDateTime.ofInstant(vCurrentDate.toInstant(), ZoneId.systemDefault()).toLocalDate().getYear();
    }

    @BeforeEach
    void init() {
        vEcritureComptable = new EcritureComptable();
    }

    @AfterAll
    static void tearDownAll() {
        vEcritureComptable = null;
    }

    // ==================== TESTS DEJA PRESENTS ET A VALIDER ====================
    @Test
    void checkEcritureComptableUnit() throws Exception {
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(vCurrentDate);
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    void checkEcritureComptableUnitNonEquilibree() throws Exception {
//        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
//        vEcritureComptable.setDate(vCurrentDate);
//        vEcritureComptable.setLibelle("Libelle");
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
//                null, new BigDecimal(123),
//                null));
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
//                null, null,
//                new BigDecimal(1234)));
//        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    void checkEcritureComptableUnitNbLignes() throws Exception {
//        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
//        vEcritureComptable.setDate(vCurrentDate);
//        vEcritureComptable.setLibelle("Libelle");
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
//                null,null,
//                null));
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
//                null, null,
//                null));
//        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    void checkEcritureComptableUnitViolation() {
        assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit(vEcritureComptable));
    }

    /**
     * Tester qu'une ecriture est équilibrée
     * @throws FunctionalException
     */
    @Test
    void checkEcritureComptableUnitRG2() throws FunctionalException {
//        EcritureComptable vEcritureComptable;
//        vEcritureComptable = new EcritureComptable();
//        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
//        vEcritureComptable.setDate(new Date());
//        vEcritureComptable.setLibelle("Libelle");
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
//                null, new BigDecimal(123),
//                null));
//        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
//                null, null,
//                new BigDecimal(1234)));
//        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /**
     * Une écriture comptable doit contenir au moins deux lignes d'écriture :
     * une au débit et une au crédit
     */
    @Test
     void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(50),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(50)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    // ==================== TESTS AJOUTES ====================
    /*
        On test que le addRefence fonctionne correctement
     */
    @Test
    void testAddReference() throws Exception {
        vEcritureComptable.setId(-1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/12/31"));
        vEcritureComptable.setLibelle("Cartouches d’imprimante");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(706),
                "Prestations de services", null, new BigDecimal(2500)
        ));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4456),
                "TVA 20%", new BigDecimal(2000),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Facture F110001",null,
                new BigDecimal(500)));

        managerIntegration.addReference(vEcritureComptable);
    }

    @Test
    void checkEcritureComptableContext() throws Exception {
        vEcritureComptable.setReference("AC-2016/00001");
        manager.checkEcritureComptableContext(vEcritureComptable);
    }
    /**
     *  On vérifie l'unicité de la référence.
     */
    @Test
    public void testCheckEcritureComptable_RG6() throws FunctionalException{
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        vEcritureComptable.setReference("AC-2016/00001");
        manager.checkEcritureComptableContext(vEcritureComptable);
    }
}
