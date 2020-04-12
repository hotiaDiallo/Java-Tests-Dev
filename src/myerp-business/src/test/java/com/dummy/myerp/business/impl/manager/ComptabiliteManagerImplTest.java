package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.dummy.myerp.testbusiness.business.BusinessTestCase.getBusinessProxy;
import static org.junit.jupiter.api.Assertions.*;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;


public class ComptabiliteManagerImplTest {

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

    /**
     * Pour qu'une écriture comptable soit valide, elle doit être équilibrée
     */
    @Test
    void checkEcritureComptableUnitRG2() {
        assertThrows(FunctionalException.class, () -> {
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new Date());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                    null, new BigDecimal(123),
                    null));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                    null, null,
                    new BigDecimal(1234)));
            manager.checkEcritureComptableUnit(vEcritureComptable);
        });
    }

    /**
     * une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
     */
    @Test
    void checkEcritureComptableUnitRG3() {
        assertThrows(FunctionalException.class, () -> {
            vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
            vEcritureComptable.setDate(new Date());
            vEcritureComptable.setLibelle("Libelle");
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                    null, new BigDecimal(123),
                    new BigDecimal(123)));
            vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                    null, new BigDecimal(123),
                    new BigDecimal(123)));

            manager.checkEcritureComptableUnit(vEcritureComptable);
        });
    }

    /**
     * vérifier que l'année dans la référence correspond bien à la date de l'écriture,
     * idem pour le code journal...
     */
    @Test
    void checkEcritureComptableUnitRG5() {
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(vCurrentDate);
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        assertThrows(FunctionalException.class, () -> {
            vEcritureComptable.setReference("AC-" + (vCurrentYear - 1) + "/00001");
            manager.checkEcritureComptableUnit(vEcritureComptable);
        });

        assertThrows(FunctionalException.class, () -> {
            vEcritureComptable.setReference("DC-" + vCurrentYear + "/00001");
            manager.checkEcritureComptable(vEcritureComptable);
        });
    }

    /**
     * La référence d'une écriture comptable doit être unique
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

    // TODO : DONE
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

    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     *  (unicité de la référence, année comptable non cloturé...)
     * @throws Exception
     */
    @Test
    void checkEcritureComptableContext() throws Exception {
        vEcritureComptable.setReference("VE-2016/00001");
        manager.checkEcritureComptableContext(vEcritureComptable);
    }

    // TODO : DONE
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
    void checkEcritureComptableUnitViolation() {
        assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit(vEcritureComptable));
    }

    @Test
    void addReference() throws Exception {
        vEcritureComptable.setId(-1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2020/12/31"));
        vEcritureComptable.setLibelle("Cartouches");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4456),
                "TVA 20%", new BigDecimal(8),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Facture F110001", null,
                new BigDecimal(51)));

        managerIntegration.addReference(vEcritureComptable);
    }

}
