package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SequenceEcritureComptableRM implements RowMapper<SequenceEcritureComptable> {

    @Override
    public SequenceEcritureComptable mapRow(ResultSet resultSet, int pRowNum) throws SQLException

    {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setAnnee(resultSet.getInt("annee"));
        sequenceEcritureComptable.setJournalCode(resultSet.getString("journal_code"));
        sequenceEcritureComptable.setDerniereValeur(resultSet.getInt("derniere_valeur"));
        return sequenceEcritureComptable;
    }

}
