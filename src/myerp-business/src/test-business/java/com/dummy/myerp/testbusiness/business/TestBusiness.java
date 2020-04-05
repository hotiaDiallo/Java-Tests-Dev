package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

public class TestBusiness {

    /*
        On test que le addRefence fonctionne correctement lorsqu'une référence n'existe pas.
     */
    @Test
    public void testAddReference() throws NotFoundException, FunctionalException {
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("BQ", "News"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        SpringRegistry.getBusinessProxy().getComptabiliteManager().insertEcritureComptable(vEcritureComptable);

        SpringRegistry.getBusinessProxy().getComptabiliteManager().addReference(vEcritureComptable);
        Assert.assertEquals("BQ-2019/00001", vEcritureComptable.getReference());
    }

     /*
    RG_Compta_5 : vérifier que l'année dans la référence
    correspond bien à la date de l'écriture
     */

    @Test(expected = FunctionalException.class)
    public void testCheckEcritureComptable_RG_5_date() throws FunctionalException{
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("BQ", "News"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        vEcritureComptable.setReference("BQ-2018/00001");
        SpringRegistry.getBusinessProxy().getComptabiliteManager().checkEcritureComptable(vEcritureComptable);
    }

    /*
   RG_Compta_5 : vérifier que le code dans la référence correspond bien à la date dans le journal
    */
    @Test
    public void testCheckEcritureComptable_RG_5_code() throws FunctionalException{
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("BQ", "News"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        vEcritureComptable.setReference("BQ-2020/00001");
        SpringRegistry.getBusinessProxy().getComptabiliteManager().checkEcritureComptable(vEcritureComptable);
    }

    /*
        RG_Compta_6	: Vérification de l'unicité de la référence
     */

    @Test(expected = FunctionalException.class)
    public void testCheckEcritureComptable_RG_6() throws FunctionalException{
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("BQ", "News"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, null,
                new BigDecimal(123)));

        vEcritureComptable.setReference("BQ-2016/00001");
        SpringRegistry.getBusinessProxy().getComptabiliteManager().checkEcritureComptable(vEcritureComptable);
    }
}
