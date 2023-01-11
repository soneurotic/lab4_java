package org.example.contoller;

import org.example.repository.DivisionResultReader;
import org.example.service.DivisionService;
import org.example.service.PolynomialLongDivision;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    private static Scanner scanner;
    public Controller(InputStream input){
        scanner = new Scanner(input);
    }
    public JSONArray DivideAndSaveResult(){
        boolean switcher = true;
        List<Long> polynomial = new ArrayList<>();
        List<Long> divider = new ArrayList<>();
        System.out.println("Формат - коефіцієнт, степінь, коефіцієнт, степінь, (і так далі попарно) . . .");
        System.out.println("\n!!!    Для зупинки введіть значення '404'\n");
        System.out.println("Введіть поліном (ділене):");
        while(switcher){
            System.out.print("Коеф.: ");
            long coefficient = scanner.nextInt();
            if(coefficient == 404){
                break;
            }
            polynomial.add(coefficient);
            System.out.print("Степінь.: ");
            long exponent = scanner.nextInt();
            polynomial.add(exponent);
        }
        System.out.println("Формат - коефіцієнт, степінь, коефіцієнт, степінь, (і так далі попарно) . . .");
        System.out.println("\n!!!    Для зупинки введіть значення '404'\n");
        System.out.println("Введіть поліном (дільник)");
        while(switcher){
            System.out.print("Коеф.: ");
            long coefficient = scanner.nextInt();
            if(coefficient == 404){
                break;
            }
            divider.add(coefficient);
            System.out.print("Степінь.: ");
            long exponent = scanner.nextInt();
            divider.add(exponent);
        }

        DivisionService divisionService = new DivisionService(new JSONObject(), System.in, new DivisionResultReader());
        PolynomialLongDivision.Polynomial polynomial1 = new PolynomialLongDivision.Polynomial((ArrayList<Long>)polynomial);
        PolynomialLongDivision.Polynomial divider1 = new PolynomialLongDivision.Polynomial((ArrayList<Long>)divider);

        System.out.println("Оберіть вигляд отриманої відповіді:");
        System.out.println("1 - з поясненнями");
        System.out.println("2 - без пояснень");
        int input = scanner.nextInt();
        if(input == 1){
            PolynomialLongDivision.DivideWithExplanation(polynomial1, divider1);
        } else if (input == 2) {
            PolynomialLongDivision.DivideClear(polynomial1, divider1);
        }else{
            throw new RuntimeException();
        }
        PolynomialLongDivision.Polynomial[] result = divisionService.getResult(polynomial1, divider1);
        divisionService.SaveResult(polynomial1,divider1, result[0], result[1]);
        return divisionService.getDivisionComponentsFromFile();
    }
}