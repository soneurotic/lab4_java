package org.example.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PolynomialLongDivision {
    public static void DivideClear(Polynomial p1, Polynomial p2){
        Polynomial[] result = p1.divide(p2);
        System.out.printf("Обчислення: (%s) / (%s) = %s остача %s%n", p1, p2, result[0], result[1]);
        System.out.printf("Перевірка: (%s) * (%s) + (%s) = %s%n%n", result[0], p2, result[1], result[0].multiply(p2).add(result[1]));
    }
    public static void DivideWithExplanation(Polynomial p1, Polynomial p2){
        String tab = " ";
        String line = "—";
        System.out.printf(" %s┃ %s%n", tab.repeat(p1.toString().length()), p2);
        Polynomial[] division_result = p1.divide(p2);
        System.out.printf(" %s┠%s%n", p1, line.repeat(division_result[0].toString().length()));
        System.out.printf("—%s┃%s%n", tab.repeat(p1.toString().length()), division_result[0]);
        Polynomial[] result = p1.divideWithExplanation(p2);
        System.out.printf("Обчислення: (%s) / (%s) = %s остача %s%n", p1, p2, result[0], result[1]);
        System.out.printf("Перевірка: (%s) * (%s) + (%s) = %s%n%n", result[0], p2, result[1], result[0].multiply(p2).add(result[1]));
    }

    public static final class Polynomial{
        private List<Term> polynomialTerms;
        //  Формат - коефіцієнт, степінь, коефіцієнт, степінь, (і так далі попарно) . . .
        public Polynomial(ArrayList<Long> values){
            if(values.size()%2 != 0){
                throw new IllegalArgumentException("Помилка. Кількість елементів має бути парна. Кільк.елем. = " + values.size());
            }
            polynomialTerms = new ArrayList<>();
            for(int i = 0; i<values.size(); i += 2){
                polynomialTerms.add(new Term(BigInteger.valueOf(values.get(i)), values.get(i+1)));
            }
            Collections.sort(polynomialTerms, new TermSorter());
        }

        public Polynomial(){
            //  нуль
            polynomialTerms = new ArrayList<>();
            polynomialTerms.add(new Term(BigInteger.ZERO, 0));
        }

        private Polynomial(List<Term> termList){
            if(termList.size() != 0){
                //  Видалення нульових елементів, коли у цьому є потреба
                for(int i = 0; i < termList.size(); i++){
                    if(termList.get(i).coefficient.compareTo(Integer.ZERO_INT) == 0){
                        termList.remove(i);
                    }
                }
            }
            if(termList.size() == 0){
                //  нуль
                termList.add(new Term(BigInteger.ZERO,0));
            }
            polynomialTerms = termList;
            Collections.sort(polynomialTerms, new TermSorter());
        }

        public Polynomial[] divide(Polynomial v){
            Polynomial q = new Polynomial();
            Polynomial r = this;
            Number lcv = v.leadingCoefficient();
            long dv = v.degree();
            while(r.degree() >= dv){
                Number lcr = r.leadingCoefficient();
                Number s = lcr.divide(lcv);
                Term term = new Term(s, r.degree() - dv);
                q = q.add(term);
                r = r.add(v.multiply(term.negate()));
            }
            return new Polynomial[] {q, r};
        }
        public Polynomial[] divideWithExplanation(Polynomial v){
            boolean separator = true;
            Polynomial q = new Polynomial();
            Polynomial r = this;
            String tab = " ";
            String line = "─";
            Number lcv = v.leadingCoefficient();
            long dv = v.degree();
            while(r.degree() >= dv){
                Number lcr = r.leadingCoefficient();
                Number s = lcr.divide(lcv);
                Term term = new Term(s, r.degree() - dv);
                Polynomial a = v.multiply(term);
                q = q.add(term);
                r = r.add(v.multiply(term.negate()));
                if(separator) {
                    System.out.printf("");
                    separator = false;
                }else{
                    System.out.printf("%s─%n", " ".repeat(tab.length() - 1));
                }
                System.out.printf("%s%s%n", tab, a);
                System.out.printf("%s%s%n", tab, line.repeat(a.toString().length()));
                tab = tab + "      ";
                System.out.println(tab+r);
            }
            return new Polynomial[] {q, r};
        }

        public Polynomial add(Polynomial polynomial){
            List<Term> termList = new ArrayList<>();
            int thisCount = polynomialTerms.size();
            int polyCount = polynomial.polynomialTerms.size();
            while(thisCount > 0 || polyCount > 0){
                Term thisTerm = thisCount == 0 ? null : polynomialTerms.get(thisCount-1);
                Term polyTerm = polyCount == 0 ? null : polynomial.polynomialTerms.get(polyCount-1);
                if(thisTerm == null){
                    termList.add(polyTerm);
                    polyCount--;
                }
                else if(polyTerm == null){
                    termList.add(thisTerm);
                    thisCount--;
                }
                else if(thisTerm.degree() == polyTerm.degree()){
                    Term t = thisTerm.add(polyTerm);
                    if(t.coefficient.compareTo(Integer.ZERO_INT) != 0){
                        termList.add(t);
                    }
                    thisCount--;
                    polyCount--;
                }
                else if(thisTerm.degree() < polyTerm.degree()){
                    termList.add(thisTerm);
                    thisCount--;
                }
                else{
                    termList.add(polyTerm);
                    polyCount--;
                }
            }
            return new Polynomial(termList);
        }

        public Polynomial add(Term term){
            List<Term> termList = new ArrayList<>();
            boolean added = false;
            for(int index = 0; index < polynomialTerms.size(); index++){
                Term currentTerm = polynomialTerms.get(index);
                if(currentTerm.exponent == term.exponent){
                    added = true;
                    if(currentTerm.coefficient.add(term.coefficient).compareTo(Integer.ZERO_INT) != 0){
                        termList.add(currentTerm.add(term));
                    }
                }
                else{
                    termList.add(currentTerm);
                }
            }
            if(!added){
                termList.add(term);
            }
            return new Polynomial(termList);
        }

        public Polynomial multiply(Polynomial polynomial){
            List<Term> termList = new ArrayList<>();
            for(int i = 0; i < polynomialTerms.size(); i++){
                Term ci = polynomialTerms.get(i);
                for(int j = 0; j < polynomial.polynomialTerms.size(); j++){
                    Term cj = polynomial.polynomialTerms.get(j);
                    Term currentTerm = ci.multiply(cj);
                    boolean added = false;
                    for(int k = 0; k < termList.size(); k++){
                        if(currentTerm.exponent == termList.get(k).exponent){
                            added = true;
                            Term t = termList.remove(k).add(currentTerm);
                            if(t.coefficient.compareTo(Integer.ZERO_INT) != 0){
                                termList.add(t);
                            }
                            break;
                        }
                    }
                    if(!added){
                        termList.add(currentTerm);
                    }
                }
            }
            return new Polynomial(termList);
        }

        public Polynomial multiply(Term term){
            List<Term> termList = new ArrayList<>();
            for(int index = 0; index < polynomialTerms.size(); index++){
                Term currentTerm = polynomialTerms.get(index);
                termList.add(currentTerm.multiply(term));
            }
            return new Polynomial(termList);
        }

        public Number leadingCoefficient(){
            return polynomialTerms.get(0).coefficient;
        }

        public long degree(){
            return polynomialTerms.get(0).exponent;
        }

        @Override
        public String toString(){
            StringBuilder build = new StringBuilder();
            boolean switcher = true;
            for(Term term: polynomialTerms){
                if(switcher){
                    build.append(term);
                    switcher = false;
                }
                else{
                    build.append(" ");
                    if(term.coefficient.compareTo(Integer.ZERO_INT) > 0){
                        build.append("+ ");
                        build.append(term);
                    }
                    else{
                        build.append("- ");
                        build.append(term.negate());
                    }
                }
            }
            return build.toString();
        }
    }

    private static final class TermSorter implements Comparator<Term> {
        @Override
        public int compare(Term o1, Term o2) {
            return (int)(o2.exponent - o1.exponent);
        }
    }

    private static final class Term {
        //  число(коефіцієнт)
        Number coefficient;
        //  степінь числа(коефіцієнта)
        long exponent;

        public Term(BigInteger c, long e){
            coefficient = new Integer(c);
            exponent = e;
        }

        public Term(Number c, long e){
            coefficient = c;
            exponent = e;
        }

        public Term multiply(Term term){
            return new Term(coefficient.multiply(term.coefficient), exponent + term.exponent);
        }

        public Term add(Term term){
            if(exponent != term.exponent){
                throw new RuntimeException("Помилка: Степені не є рівними.");
            }
            return new Term(coefficient.add(term.coefficient), exponent);
        }

        public Term negate(){
            return new Term(coefficient.negate(), exponent);
        }

        public long degree(){
            return exponent;
        }

        @Override
        public String toString(){
            if(coefficient.compareTo(Integer.ZERO_INT) == 0){
                return "0";
            }
            if(exponent == 0){
                return "" + coefficient;
            }
            if(coefficient.compareTo(Integer.ONE_INT) == 0){
                if(exponent == 1){
                    return "x";
                }
                else{
                    return "x^" + exponent;
                }
            }
            if(exponent == 1){
                return coefficient + "x";
            }
            return coefficient + "x^" + exponent;
        }
    }

    private static abstract class Number{
        public abstract int compareTo(Number input);
        public abstract Number negate();
        public abstract Number add(Number input);
        public abstract Number multiply(Number input);
        public abstract Number inverse();
        public abstract boolean isInteger();
        public abstract boolean isFraction();

        public Number subtract(Number input){
            return add(input.negate());
        }

        public Number divide(Number input){
            return multiply(input.inverse());
        }
    }

    //дроби
    public static class Fraction extends Number {

        //чисельник
        private final Integer numerator;

        //знаменник
        private final Integer denominator;

        //конструктор, що приймає на вхід чисельник і знаменник
        public Fraction(Integer n, Integer d) {
            //чисельник
            numerator = n;

            //знаменник
            denominator = d;
        }

        @Override
        public int compareTo(Number input){
            if(input.isInteger()){
                Integer result = ((Integer) input).multiply(denominator);
                return numerator.compareTo(result);
            }
            else if(input.isFraction()){
                Fraction inFraction = (Fraction) input;
                Integer left = numerator.multiply(inFraction.denominator);
                Integer right = denominator.multiply(inFraction.numerator);
                return left.compareTo(right);
            }
            throw new RuntimeException("Помилка:  Невідомий числовий тип у Fraction.compareTo");
        }

        //  заперечення, тобто дріб*(-1), або те саме що чисельник*(-1)
        @Override
        public Number negate(){
            if(denominator.integer.signum() < 0){
                return new Fraction(numerator, (Integer) denominator.negate());
            }
            return new Fraction((Integer) numerator.negate(), denominator);
        }

        @Override
        public Number add(Number input){
            if(input.isInteger()){
                //  x/y+z = (x+yz)/y
                return new Fraction((Integer) ((Integer) input).multiply(denominator).add(numerator), denominator);
            }
            else if(input.isFraction()){
                Fraction inFrac = (Fraction) input;
                //  обчислення a/b + x/y
                //  Нехай q = gcd(b,y)
                //  Результат = ((a*y + x*b)/q) / (b*y/q)
                Integer x = inFrac.numerator;
                Integer y = inFrac.denominator;
                Integer q = y.gcd(denominator);
                Integer temp1 = numerator.multiply(y);
                Integer temp2 = denominator.multiply(x);
                Integer newDenom = denominator.multiply(y).divide(q);
                if(newDenom.compareTo(Integer.ONE_INT) == 0){
                    return temp1.add(temp2);
                }
                Integer newNum = (Integer)temp1.add(temp2).divide(q);
                Integer gcd2 = newDenom.gcd(newNum);
                if(gcd2.compareTo(Integer.ONE_INT) == 0){
                    return new Fraction(newNum, newDenom);
                }
                newNum = newNum.divide(gcd2);
                newDenom = newDenom.divide(gcd2);
                if(newDenom.compareTo(Integer.ONE_INT) == 0){
                    return newNum;
                }
                else if(newDenom.compareTo(Integer.MINUS_ONE_INT) == 0){
                    return newNum.negate();
                }
                return new Fraction(newNum, newDenom);
            }
            throw new RuntimeException("Помилка: невідомий числовий тип у Fraction.compareTo");
        }

        @Override
        public Number multiply(Number input){
            //  якщо вхідний параметр - ціле число
            if(input.isInteger()){
                //  x/y*z = x*z/y
                Integer temp = numerator.multiply((Integer) input);
                Integer gcd = temp.gcd(denominator);
                if(gcd.compareTo(Integer.ONE_INT) == 0 || gcd.compareTo(Integer.MINUS_ONE_INT) == 0){
                    return new Fraction(temp, denominator);
                }
                Integer newTop = temp.divide(gcd);
                Integer newBot = denominator.divide(gcd);
                if(newBot.compareTo(Integer.ONE_INT) == 0){
                    return newTop;
                }
                if(newBot.compareTo(Integer.MINUS_ONE_INT) == 0){
                    return newTop.negate();
                }
                return new Fraction(newTop, newBot);
            }
            //  якщо вхідний параметр - дріб
            else if(input.isFraction()){
                Fraction inFrac = (Fraction) input;
                //  обчислення a/b * x/y
                Integer tempTop = numerator.multiply(inFrac.numerator);
                Integer tempBot = denominator.multiply(inFrac.denominator);
                Integer gcd = tempTop.gcd(tempBot);
                if(gcd.compareTo(Integer.ONE_INT) == 0 || gcd.compareTo(Integer.MINUS_ONE_INT) == 0){
                    return new Fraction(tempTop, tempBot);
                }
                Integer newTop = tempTop.divide(gcd);
                Integer newBot = tempBot.divide(gcd);
                if(newBot.compareTo(Integer.ONE_INT) == 0){
                    return newTop;
                }
                if(newBot.compareTo(Integer.MINUS_ONE_INT) == 0){
                    return newTop.negate();
                }
                return new Fraction(newTop, newBot);
            }
            throw new RuntimeException("Помилка: невідомий числовий тип у Fraction.compareTo");
        }

        @Override
        public boolean isInteger(){
            return false;
        }

        @Override
        public boolean isFraction(){
            return true;
        }

        @Override
        public String toString(){
            return numerator.toString() + "/" + denominator.toString();
        }

        @Override
        public Number inverse(){
            if(numerator.equals(Integer.ONE_INT)){
                return denominator;
            }
            else if(numerator.equals(Integer.MINUS_ONE_INT)){
                return denominator.negate();
            }
            else if(numerator.integer.signum() < 0){
                return new Fraction((Integer) denominator.negate(), (Integer) numerator.negate());
            }
            return new Fraction(denominator, numerator);
        }
    }

    public static class Integer extends Number {
        private BigInteger integer;
        public static final Integer MINUS_ONE_INT = new Integer(new BigInteger("-1"));
        public static final Integer ONE_INT = new Integer(new BigInteger("1"));
        public static final Integer ZERO_INT = new Integer(new BigInteger("0"));

        public Integer(BigInteger number){
            this.integer = number;
        }

        public int compareTo(Integer value){
            return integer.compareTo(value.integer);
        }

        @Override
        public int compareTo(Number input){
            if(input.isInteger()){
                return compareTo((Integer) input);
            }
            else if(input.isFraction()){
                Fraction fraction = (Fraction) input;
                BigInteger result = integer.multiply(fraction.denominator.integer);
                return result.compareTo(fraction.numerator.integer);
            }
            throw new RuntimeException("Помилка: Невідомий числовий тип у Integer.compareTo");
        }

        //  заперечення числа, тобто число*(-1)
        @Override
        public Number negate(){
            return new Integer(integer.negate());
        }

        public Integer add(Integer input){
            return new Integer(integer.add(input.integer));
        }

        @Override
        public Number add(Number input){
            if(input.isInteger()){
                return add((Integer) input);
            }
            else if(input.isFraction()){
                Fraction fraction = (Fraction) input;
                Integer top = fraction.numerator;
                Integer bot = fraction.denominator;
                return new Fraction((Integer) multiply(bot).add(top), bot);
            }
            throw new RuntimeException("Помилка: Невідомий числовий тип у Integer.add");
        }

        @Override
        public Number multiply(Number input){
            if(input.isInteger()){
                return multiply((Integer) input);
            }
            else if(input.isFraction()){
                //  a * x/y = ax/y
                Integer x = ((Fraction) input).numerator;
                Integer y = ((Fraction) input).denominator;
                Integer temp = (Integer) multiply(x);
                Integer gcd = temp.gcd(y);

                //  якщо НСД = (1 або -1)
                if(gcd.compareTo(Integer.ONE_INT) == 0 || gcd.compareTo(Integer.MINUS_ONE_INT) == 0){
                    return new Fraction(temp, y);
                }
                Integer newTop = temp.divide(gcd);
                Integer newBot = y.divide(gcd);
                if(newBot.compareTo(Integer.ONE_INT) == 0 ){
                    return newTop;
                }
                if(newBot.compareTo(Integer.MINUS_ONE_INT) == 0){
                    return newTop.negate();
                }
                return new Fraction(newTop, newBot);
            }
            throw new RuntimeException("Помилка: Невідомий числовий тип у Integer.add");
        }

        //  gcd - Greatest Common Divisor, тобто НСД
        public Integer gcd(Integer input){
            return new Integer(integer.gcd(input.integer));
        }

        public Integer divide(Integer input){
            return new Integer(integer.divide(input.integer));
        }

        public Integer multiply(Integer input){
            return new Integer(integer.multiply(input.integer));
        }

        @Override
        public boolean isInteger(){
            return true;
        }

        //  дріб (не інтеджер) = false (очевидно тому що у нас клас Integer)
        @Override
        public boolean isFraction(){
            return false;
        }

        @Override
        public String toString(){
            return integer.toString();
        }

        @Override
        public Number inverse(){
            if(equals(ZERO_INT)){
                throw new RuntimeException("Спроба обернення числа 'нуль' у IntegerExpression");
            }
            else if(this.compareTo(ONE_INT) == 0){
                return ONE_INT;
            }
            else if(this.compareTo(MINUS_ONE_INT) == 0) {
                return MINUS_ONE_INT;
            }
            return new Fraction(ONE_INT, this);
        }

    }
}
