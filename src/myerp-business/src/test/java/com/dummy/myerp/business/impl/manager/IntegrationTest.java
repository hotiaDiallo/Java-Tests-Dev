package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.testbusiness.business.BusinessTestCase;
import junit.framework.Assert;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

class IntegrationTest extends BusinessTestCase {

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

    /*
        On test que le addRefence fonctionne correctement lorsqu'une référence n'existe pas.
     */
    @Test
    void addReference() throws Exception {
        vEcritureComptable.setId(-4);
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
        Assert.assertEquals("AC-2016/00001", vEcritureComptable.getReference());
    }

    /*
        On test qu'il y a bien deux lignes comptables.
     */
    @Test
    void addReference_DeuxLignesComptables() throws Exception {
        vEcritureComptable.setId(-4);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/12/31"));
        vEcritureComptable.setLibelle("Cartouches d’imprimante");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(706),
                "Prestations de services", null, new BigDecimal(2500)
        ));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4456),
                "TVA 20%", new BigDecimal(2500),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Facture F110001",null,
                new BigDecimal(500)));

        managerIntegration.addReference(vEcritureComptable);
        manager.checkEcritureComptable(vEcritureComptable);
    }

    /*
        On test que le doublon de référence est bien détecté.
     */
    public void testCheckWithExistingReference() throws FunctionalException, NotFoundException{
        EcritureComptable vEcritureComptableExisting = null;
        vEcritureComptableExisting = manager.getEcritureComptableById(3);
        vEcritureComptableExisting.setReference("BQ-2016/00002");
        manager.checkEcritureComptable(vEcritureComptableExisting);
    }

    /*
    On test la RG5 avec le code.
     */
    public void testCheckEcritureComptable_RG_5_code() throws FunctionalException{
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        vEcritureComptable.setReference("AC-2019/00001");
        manager.checkEcritureComptable(vEcritureComptable);
    }


    @Test
    void checkEcritureComptableUnitNbLignes() throws Exception {
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(vCurrentDate);
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, new BigDecimal(123),
                null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    void checkEcritureComptableUnitViolation() {
        Assertions.assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit(vEcritureComptable));
    }


    @Test
    void checkEcritureComptableContext() throws Exception {
        vEcritureComptable.setReference("AZ-2016/00001");
        manager.checkEcritureComptableContext(vEcritureComptable);
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
    public void updateEcritureComptable() throws FunctionalException, NotFoundException {
        vEcritureComptable = managerIntegration.getEcritureComptableById(-2);
        managerIntegration.updateEcritureComptable(vEcritureComptable);
    }




}
