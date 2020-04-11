package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TestBusiness extends BusinessTestCase{

    private static ComptabiliteManagerImpl manager;
    private static ComptabiliteManager managerIntegration;
    private static EcritureComptable vEcritureComptable;
    private static Date vCurrentDate;
    private static Integer vCurrentYear;

    @BeforeAll
    static void initAll() {
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
        vEcritureComptable.setId(-1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2020/04/06"));
        vEcritureComptable.setLibelle("Cartouches d’imprimante");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Facture C110002", new BigDecimal(3000),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Fournisseurs", new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(706),
                "TMA Appli Xxx", null,
                new BigDecimal(2500)));

        managerIntegration.addReference(vEcritureComptable);
    }


     /*
    RG_Compta_5 : vérifier que l'année dans la référence
    correspond bien à la date de l'écriture
     */

    @Test
    public void testCheckEcritureComptable_RG5_date() throws FunctionalException{
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Fournisseurs");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        vEcritureComptable.setReference("AC-2020/00001");
        SpringRegistry.getBusinessProxy().getComptabiliteManager().checkEcritureComptable(vEcritureComptable);
    }

    /*
   RG_Compta_5 : vérifier que le code dans la référence correspond bien au dans le journal
    */
    @Test
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

        vEcritureComptable.setReference("AC-2020/00001");
        SpringRegistry.getBusinessProxy().getComptabiliteManager().checkEcritureComptable(vEcritureComptable);
    }

    /*
        RG_Compta_6	: Vérification de l'unicité de la référence
     */

    @Test
    void checkEcritureComptableContextRG6() {
        assertThrows(FunctionalException.class, () -> {
            vEcritureComptable.setReference("VE-2016/00002");
            manager.checkEcritureComptableContext(vEcritureComptable);
        });
        assertThrows(FunctionalException.class, () -> {
            vEcritureComptable.setId(0);
            vEcritureComptable.setReference("VE-2016/00002");
            manager.checkEcritureComptableContext(vEcritureComptable);
        });
    }
}
