package org.example.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.example.repository.DivisionResultReader;
import org.example.repository.DivisionResultWriter;

import java.io.InputStream;

public class DivisionService {
    private final DivisionResultReader divisionResultReader;
    private final DivisionResultWriter divisionResultWriter;
    public DivisionService(JSONObject jsonObject, InputStream input, DivisionResultReader reader){
        this.divisionResultReader = reader;
        this.divisionResultWriter = new DivisionResultWriter(jsonObject, input, reader);
    }

    public PolynomialLongDivision.Polynomial[] getResult(PolynomialLongDivision.Polynomial p1, PolynomialLongDivision.Polynomial divider){
        PolynomialLongDivision.Polynomial[] result = p1.divide(divider);
        return result;
    };

    public void SaveResult(PolynomialLongDivision.Polynomial polynomial1, PolynomialLongDivision.Polynomial divider,
                           PolynomialLongDivision.Polynomial polynomial2, PolynomialLongDivision.Polynomial remainder){
        divisionResultWriter.saveDivisionResult(polynomial1, divider, polynomial2, remainder);
    }

    public JSONArray getDivisionComponentsFromFile(){
        return divisionResultReader.readAll();
    }

}
