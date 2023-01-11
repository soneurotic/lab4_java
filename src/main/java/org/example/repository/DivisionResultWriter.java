package org.example.repository;

import org.example.service.PolynomialLongDivision;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class DivisionResultWriter {
    private final JSONObject json;
    private final DivisionResultReader divisionResultReader;
    public DivisionResultWriter(JSONObject json, InputStream input, DivisionResultReader reader){
        this.json = json;
        this.divisionResultReader = reader;
    }

    @SuppressWarnings("unchecked")
    public void saveDivisionResult(PolynomialLongDivision.Polynomial polynomial1, PolynomialLongDivision.Polynomial divider,
                                   PolynomialLongDivision.Polynomial polynomial2, PolynomialLongDivision.Polynomial remainder){
        json.put("Многочлен", polynomial1.toString());
        json.put("Дільник многочлена", divider.toString());
        json.put("Результат ділення", polynomial2.toString());
        json.put("Остача", remainder.toString());

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(json);

        try(FileWriter fileWriter = new FileWriter("DivisionResult.json")){
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.flush();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
