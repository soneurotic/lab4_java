package org.example.repository;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DivisionResultReader {
    public JSONArray readAll() {

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = new JSONArray();

        File f = new File("DivisionResult.json");
        if(!f.exists() || f.length() == 0) {
            return jsonArray;
        }

        try (FileReader reader = new FileReader("DivisionResult.json"))
        {
            Object obj = jsonParser.parse(reader);
            jsonArray = (JSONArray) obj;

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
}
