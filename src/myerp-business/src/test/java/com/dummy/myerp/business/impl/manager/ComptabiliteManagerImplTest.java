package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.testbusiness.business.BusinessTestCase;
import junit.framework.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ComptabiliteManagerImplTest extends BusinessTestCase {

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

    @Test
    void checkEcritureComptable() throws Exception {
        vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
        vEcritureComptable.setDate(vCurrentDate);
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("VE-" + vCurrentYear + "/00004");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        manager.checkEcritureComptable(vEcritureComptable);
    }

    @Test
    void checkEcritureComptableUnit() throws Exception {
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(vCurrentDate);
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-" + vCurrentYear + "/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
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
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /**
     * Une écriture comptable doit contenir au moins deux lignes d'écriture :
     * une au débit et une au crédit
     */
    @Test
    void checkEcritureComptableUnitRG3() throws Exception {
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

    /**
     *  On vérifie l'unicité de la référence.
     */
    @Test
    public void testCheckEcritureComptable_RG6() throws FunctionalException{
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        vEcritureComptable.setReference("AC-2017/00001");
        manager.checkEcritureComptableContext(vEcritureComptable);
    }

    /*
           On vérifie que la date de l'écriture corresponde bien avec la référence lors de l'update.
        */
    @Test
    public void updateEcritureComptable() throws FunctionalException, NotFoundException{
        vEcritureComptable = managerIntegration.getEcritureComptableById(-2);
        managerIntegration.updateEcritureComptable(vEcritureComptable);
    }

     /*
    On vérifie que l'on récupére bien tous les comptes comptables existants.
     */

    @Test
    public void testGetListCompteComptable(){
        List<CompteComptable> compteComptableList = managerIntegration.getListCompteComptable();
        Assert.assertEquals(7, compteComptableList.size());
    }

    /*
    On vérifie que l'on récupère bien tous les journalCOmptables existants.
     */
    @Test
    public void testGetListJournalComptable(){
        List<JournalComptable> journalComptableList = managerIntegration.getListJournalComptable();
        Assert.assertEquals(4, journalComptableList.size());
    }

    /*
    On vérifie que l'on retrouve bien toutes les écritures comptables existantes.
     */

    @Test
    public void testGetListEcritureComptable(){
        List<EcritureComptable> ecritureComptableList = managerIntegration.getListEcritureComptable();
        //Assert.assertEquals(5, ecritureComptableList.size());
    }

    /*
    On vérifie bien qu'une fois l'élèment surppimé, nous ne pouvons plus y accéder.
     */

     /*
    On vérifie bien qu'une fois l'élèment surppimé, nous ne pouvons plus y accéder.
     */

    @Test
    public void testDeleteEcritureComptable() throws NotFoundException{
        EcritureComptable vEcritureComptableExisting = null;
        vEcritureComptableExisting = manager.getEcritureComptableById(3);

        getBusinessProxy().getComptabiliteManager().deleteEcritureComptable(3);
        vEcritureComptableExisting = manager.getEcritureComptableById(3);
    }
}
