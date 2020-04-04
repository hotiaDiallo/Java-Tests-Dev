package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester : DONE
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) {

        String[] regex = new String[]{"/", "-"}; //séparateurs

        String reference_ecriture = pEcritureComptable.getReference();
        if(reference_ecriture == null){
            reference_ecriture = this.createReference(pEcritureComptable.getJournal().getCode(), pEcritureComptable.getDate(), regex);
        }

        String[] referncesValues = extractReferenceValues(reference_ecriture, regex); // [0] --> XX, [1] -> annee de l'écriture, [2] -> Numéro
        SequenceEcritureComptable sequenceEcritureComptable = null;
        if( referncesValues[1] != null ){
            int annee = Integer.parseInt(referncesValues[1]);
            try {
                //Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture (table sequence_ecriture_comptable)
                sequenceEcritureComptable = getDaoProxy().getComptabiliteDao().selectSequenceEcritureComptable(annee, referncesValues[0]);
                int numero = sequenceEcritureComptable.getDerniereValeur();
                // Utiliser la dernière valeur + 1
                numero++;
                referncesValues[2] = createNumberForReference(numero); // Recréer le #####
                getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(Integer.parseInt(referncesValues[1]), numero, referncesValues[0]);
            } catch (NotFoundException nfe){
                referncesValues[2] = createNumberForReference(1); // On recrée le #####
                getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(annee, Integer.parseInt(referncesValues[2]),referncesValues[0] );
            }

            //Recréer la nouvelle référence.
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(referncesValues[0]).append(regex[1]).append(referncesValues[1]).append(regex[0]).append(referncesValues[2]);
            String rebuiltReference = stringBuilder.toString();
            // rajouter la référence à l'écriture comptable
            pEcritureComptable.setReference(rebuiltReference);
        }
    }

    /**
     *
     * @param code
     * @param date
     * @param regex
     * @return
     */
    // Format Référence : XX-AAAA/#####
    private String createReference(String code, Date date, String[] regex) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String annee = Integer.toString(cal.get(Calendar.YEAR));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(code).append(regex[1]).append(annee).append(regex[0]).append("00000");

        return stringBuilder.toString();
    }

    // Format à extraire XX-AAAA/#####
    private String[] extractReferenceValues(String libelle,String[] regex){

        String[] resultList = new String[]{null, null, null};
        // séparer XX-AAA et #####
        String[] delim1 = libelle.split(regex[0]);
        if(delim1.length == 3){
            resultList[2] = delim1[1];  // Récupèrer #####
            String[] second_delim = delim1[0].split(regex[1]); // Sépare XX et AAAA
            if(second_delim.length == 2){
                resultList[1] = second_delim[1]; // récupèrer AAAA
                resultList[0] = second_delim[0]; // récupérer XX
            }
        }
        return resultList;
    }

    private String createNumberForReference(int numero){
        String result = Integer.toString(numero);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =   5 - result.length(); i > 0; i--){
            stringBuilder.append("0");
        }
        stringBuilder.append(result);
        result = stringBuilder.toString();
        return result;
    }


    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
            || vNbrCredit < 1
            || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                    pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}
