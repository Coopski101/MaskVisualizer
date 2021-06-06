package cooperzuranski.finalproject;

import java.io.Serializable;

public class Test implements Serializable{
    private double avgPart03um, avgPart05um, avgPart10um, avgPart25um, avgPart50um, avgPart100um;
    private double avgTempOut, avgTempIn, avgPresOut, avgPresIn, avgHumdIn, avgHumdOut;
    private double deltaPres, infectAeroSum;
    
    /*public static void main(String[] args){//testharness
        String input = "536.40,166.60,11.20,1.60,0.00,0.00,22.248001,22.123999,98227.281000,98233.906000,37.287304,37.518555,6.625000,715";
        Test joe = new Test(input);
        System.out.println(joe.getInfectAeroSum());
    }*/
    
    public Test(String csvIn){//constructor uses string argument and parses
        String csv = csvIn;
        //loop
        avgPart03um = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgPart05um = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgPart10um = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgPart25um = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgPart50um = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgPart100um = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        
        avgTempOut = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgTempIn = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgPresOut = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgPresIn = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgHumdOut = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        avgHumdIn = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        deltaPres = Double.valueOf(csv.substring(0, csv.indexOf(',')));
        csv = csv.substring(csv.indexOf(',')+1);
        infectAeroSum = Double.valueOf(csv);  
    }
    
    public double getAvgPart03um(){//All the getters
        return this.avgPart03um;
    }
    public double getAvgPart05um(){
        return this.avgPart05um;
    }
    public double getAvgPart10um(){
        return this.avgPart10um;
    }
    public double getAvgPart25um(){
        return this.avgPart25um;
    }
    public double getAvgPart50um(){
        return this.avgPart50um;
    }
    public double getAvgPart100um(){
        return this.avgPart100um;
    }
    public double getAvgTempOut(){
        return this.avgTempOut;
    }
    public double getAvgTempIn(){
        return this.avgTempIn;
    }
    public double getAvgPresOut(){
        return this.avgPresOut;
    }
    public double getAvgPresIn(){
        return this.avgPresIn;
    }
    public double getAvgHumdOut(){
        return this.avgHumdOut;
    }
    public double getAvgHumdIn(){
          return this.avgHumdIn;
    }
    public double getDeltaPres(){
        return this.deltaPres;
    }
    public double getInfectAeroSum(){
        return this.infectAeroSum;
    }

}//classend
