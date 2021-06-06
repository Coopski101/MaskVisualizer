package cooperzuranski.finalproject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class Mask implements Serializable{
    String name;
    ArrayList<Test> tests;
    double effective, infectAerosol, comfort;
    double PPyInt, PPslope, PPrSq;
    double PHyInt, PHslope, PHrSq;
    double PTyInt, PTslope, PTrSq;
    TempGraph tempGraph;
    HumdGraph humdGraph;
    PresGraph presGraph;
    JCheckBox myBox;
    private enum selection{PT,PP,PH};
    Mask left, right;

    /*public static void main(String[] args){//testharness
        String fileName = "M_1.TXT";
        Mask joe = new Mask(fileName, "joe");
        joe.name = "joe's cloth mask";
        System.out.println(joe.name + " Temp R squared: " + joe.PTrSq);
        System.out.println("effectiveness: " + joe.effective);
        System.out.println("Num tests: " + joe.tests.size());
        System.out.println("AFTER APPEND of M_2.TXT, recalculations");
        joe.append("M_2.TXT");
        System.out.println("Temp R squared: " + joe.PTrSq);
        System.out.println("effectiveness: " + joe.effective);
        System.out.println("Num tests: " + joe.tests.size());
        joe.tempGraph.showGraph(true);
        joe.presGraph.showGraph(true);
        joe.humdGraph.showGraph(false);
    }*/
    public static File getResourceAsFile(String resourcePath) {
    try {
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            return null;
        }

        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            //copy stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}
    public Mask(String filename, String name){//takes infile and parses, constructor
        tests = new ArrayList<Test>();
        this.name = name;
        
        String ahh = "/"+filename;
   
        try{
        File theFile = new File(filename);//ide version
        //File theFile = this.getResourceAsFile(filename);
        //InputStream theFile = this.getClass().getResourceAsStream(ahh);
        Scanner input = new Scanner(theFile);
        
        input.useDelimiter(",");//needed to check for end of tests
        while(!input.hasNext("PT")){//while in tests of file
            input.reset();//regular delimiter
            Test holder = new Test(input.nextLine());
            this.tests.add(holder);
            input.useDelimiter(",");
        }
        
        input.next();//move past linear regression csv data token
        input.reset();
        String temp = input.nextLine();//parsing strings is easier
        input.useDelimiter(",");
        this.PTyInt = Double.valueOf(temp.substring(1, temp.indexOf(',',1)));
        temp = temp.substring(temp.indexOf(',',1)+1);
        this.PTslope = Double.valueOf(temp.substring(0, temp.indexOf(',')));
        temp = temp.substring(temp.indexOf(',')+1);
        this.PTrSq = Double.valueOf(temp);
       
        input.next();//move past linear regression csv data token
        input.reset();
         temp = input.nextLine();//parsing strings is easier
        input.useDelimiter(",");
        this.PHyInt = Double.valueOf(temp.substring(1, temp.indexOf(',',1)));
        temp = temp.substring(temp.indexOf(',',1)+1);
        this.PHslope = Double.valueOf(temp.substring(0, temp.indexOf(',')));
        temp = temp.substring(temp.indexOf(',')+1);
        this.PHrSq = Double.valueOf(temp);
        
        input.next();//move past linear regression csv data token
        input.reset();
         temp = input.nextLine();//parsing strings is easier
        input.useDelimiter(",");
        this.PPyInt = Double.valueOf(temp.substring(1, temp.indexOf(',',1)));
        temp = temp.substring(temp.indexOf(',',1)+1);
        this.PPslope = Double.valueOf(temp.substring(0, temp.indexOf(',')));
        temp = temp.substring(temp.indexOf(',')+1);
        this.PPrSq = Double.valueOf(temp);
        
        input.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        this.calcEffective();
        this.calcComfort();
        
        this.tempGraph = new TempGraph(this.PTslope, this.PTyInt, this.PTrSq, coordinateMaker(selection.PT));
        this.humdGraph = new HumdGraph(this.PHslope, this.PHyInt, this.PHrSq, coordinateMaker(selection.PH));
        this.presGraph = new PresGraph(this.PPslope, this.PPyInt, this.PPrSq, coordinateMaker(selection.PP));
    
        this.left = this.right = null;
    }
    
    private double[][] coordinateMaker(selection select){//make list of coordinates for graph
        double[][] outArray = new double[this.tests.size()][2];
        
        for(int i = 0; i < this.tests.size(); i++){
            outArray[i][1] = tests.get(i).getInfectAeroSum();
            switch(select){//no default needed bc enum
                case PT:
                    outArray[i][0] = tests.get(i).getAvgTempOut();
                    break;
                case PP:    
                    outArray[i][0] = tests.get(i).getAvgPresOut();
                    break;
                case PH:    
                    outArray[i][0] = tests.get(i).getAvgHumdOut();
                    break;    
            }//switch
        }//for
        return outArray;
    }//coordmaker
    
    private void calcEffective(){//calc effectiveness of of avginfectaerosum
        double sum= 0;
        for(Test t : this.tests)//https://stackoverflow.com/questions/16635398/java-8-iterable-foreach-vs-foreach-loop
            sum+= t.getInfectAeroSum();
        this.infectAerosol = sum/(double)tests.size();
        double dailyPP = this.PPslope*(101325) + this.PPyInt;//new y value at avg presssure
        double dailyPT = this.PTslope*(20) + this.PTyInt;//at avg 20 C room temp
        double dailyPH = this.PHslope*(40) + this.PHyInt;//at avg 40% humidity
        this.effective = (dailyPT + dailyPH + dailyPP)/3.0;// average of all 3 predictions at ideal conditions
    }//calc effectiveness 
    
    private void calcComfort(){//used to calculate comfort via difference in inside vs outside pressure
        double sum = 0;
        for(Test t : this.tests)
            sum+= t.getDeltaPres();
        this.comfort = sum/(double)tests.size();
    }//calc comfort
    
    protected void append(String filename){//adds tests of another file, ignores pre-calculated statistics
        try{
            File theFile = new File(filename);
            Scanner input = new Scanner(theFile);

            input.useDelimiter(",");//needed to check for end of tests
            while(!input.hasNext("PT")){//while in tests of file
                input.reset();//regular delimiter
                Test holder = new Test(input.nextLine());
                this.tests.add(holder);
                input.useDelimiter(",");
            }
            input.close();
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        this.reStatistic();
        this.calcEffective();//recalculate values
        this.calcComfort();
        //UPDATE/REPLACE GRAPHS - stored for optimization and easy updating
        this.tempGraph = new TempGraph(this.PTslope, this.PTyInt, this.PTrSq, coordinateMaker(selection.PT));
        this.humdGraph = new HumdGraph(this.PHslope, this.PHyInt, this.PHrSq, coordinateMaker(selection.PH));
        this.presGraph = new PresGraph(this.PPslope, this.PPyInt, this.PPrSq, coordinateMaker(selection.PP));
    }//append
    
    private void reStatistic(){//recalcs all statistic vals. used on append , ect
        double avgY, delY, dyP, dyH, dyT, denRSq, numRSqH, numRSqT, 
                numRSqP, delXP, delXH, delXT, avgXP, avgXH, avgXT, dxP, dxT, dxH;
        avgY= dyT= dyH= dyP= avgXP= avgXT= avgXH= dxP= dxH= dxT= avgXP= 
                denRSq= numRSqH= numRSqT= numRSqP= 0.0;
        
        //all 3 linear regressions at once and r squared vals
        
        for(Test t: this.tests){//find mean
            avgY += t.getInfectAeroSum();
            avgXP += t.getAvgPresOut();
            avgXH += t.getAvgHumdOut();
            avgXT += t.getAvgTempOut();
        }
        avgY /= this.tests.size();
        avgXH /= this.tests.size();
        avgXT /= this.tests.size();
        avgXP /= this.tests.size();
        
        for(Test t: this.tests){// sum dx and dy
            dxP += Math.pow( (delXP = (t.getAvgPresOut() - avgXP)), 2.0);//(x - x_ )^2 
            dxH += Math.pow( (delXH = (t.getAvgHumdOut() - avgXH)), 2.0);
            dxT += Math.pow( (delXT = (t.getAvgTempOut() - avgXT)), 2.0);
            dyP += delXP * (delY = (t.getInfectAeroSum() - avgY)); //(x-x_)*(y-y_)
            dyH += delXH * delY;
            dyT += delXT * delY;
            denRSq += Math.pow(delY,2.0);//denominators for r squared
        }
        this.PTslope = dyT/dxT;
        this.PHslope = dyH/dxH;
        this.PPslope = dyP/dxP;
        
        this.PTyInt = avgY - avgXT*this.PTslope;//y interceptZs
        this.PHyInt = avgY - avgXH*this.PHslope;
        this.PPyInt = avgY - avgXP*this.PPslope;
        
        for(Test t: this.tests){
            numRSqT += Math.pow((this.PTyInt + this.PTslope*t.getAvgTempOut()) - avgY , 2.0);// (yRegress - avgY)^2
            numRSqP += Math.pow((this.PPyInt + this.PPslope*t.getAvgPresOut()) - avgY , 2.0);
            numRSqH += Math.pow((this.PHyInt + this.PHslope*t.getAvgHumdOut()) - avgY , 2.0);
        }
        
        try{
            this.PPrSq = numRSqP/denRSq;
            this.PTrSq = numRSqT/denRSq;
            this.PHrSq = numRSqH/denRSq;
        }catch(ArithmeticException e){
            System.out.println("Divide by zero in reStatistic, bad import");
        }
    }//re statistic
       
    public JScrollPane showTable(boolean popout){//can be returned to be inside of main panel or own JFrame by toggle!
        JPanel myPanel = new JPanel();//created as needed not stored
        myPanel.setLayout(new GridLayout(0,14));
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new BorderLayout());
        
        myPanel.add(new JLabel("Temp-in: C"));//column headers
            myPanel.add(new JLabel("Temp-out: C"));
            myPanel.add(new JLabel("Humd-in: %"));
            myPanel.add(new JLabel("Humd-out: %"));
            myPanel.add(new JLabel("Pres-in: Pa   "));
            myPanel.add(new JLabel("Pres-out: Pa   "));
            myPanel.add(new JLabel("/\\ Pres: Pa"));
            myPanel.add(new JLabel("# ~ 0.3um"));
            myPanel.add(new JLabel("# ~ 0.5um"));
            myPanel.add(new JLabel("# ~ 1.0um"));
            myPanel.add(new JLabel("# ~ 2.5um"));
            myPanel.add(new JLabel( "# ~ 5.0um"));
            myPanel.add(new JLabel("# ~ 10.0um"));
            myPanel.add(new JLabel(">= 5.0um"));
        for(Test t: this.tests){// data
            myPanel.add(new JLabel(Double.toString(t.getAvgTempIn())));
            myPanel.add(new JLabel(Double.toString(t.getAvgTempOut())));
            myPanel.add(new JLabel(Double.toString(t.getAvgHumdIn())));
            myPanel.add(new JLabel(Double.toString(t.getAvgHumdOut())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPresIn())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPresOut())));
            myPanel.add(new JLabel(Double.toString(t.getDeltaPres())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPart03um())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPart05um())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPart10um())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPart25um())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPart50um())));
            myPanel.add(new JLabel(Double.toString(t.getAvgPart100um())));
            myPanel.add(new JLabel(Double.toString(t.getInfectAeroSum())));
        }
        bigPanel.add(myPanel);
        Label myLabel = new Label("Each row is a \"Test\", the average of 5 consecutive trials. Viruses usually travel on aerosols >= 5um", Label.CENTER);
        myLabel.setBackground(Color.GREEN);
        myLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        bigPanel.add(myLabel, BorderLayout.NORTH);
        
        JScrollPane scroll = new JScrollPane(bigPanel,VERTICAL_SCROLLBAR_AS_NEEDED,HORIZONTAL_SCROLLBAR_AS_NEEDED);
        if(popout){
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(scroll);
            frame.pack();
            frame.setVisible(true);
        }
        return scroll;   
    }//table
    
    public JPanel maskMenu(){//shown for one individual mask
        Mask sneaky = this;
        JPanel menu = new JPanel(new GridLayout(4,1));
        
        JButton collected = new JButton("Show Collected Data");//button
            collected.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//does stuff when button pressed
                    sneaky.showTable(true);
                }//new action performed 
            });//inner class action listner
            collected.setPreferredSize( new Dimension(140,20) ); 
        menu.add(collected);
        
        JButton tempB = new JButton("Show Temperature VS # Particle regression");//button
            tempB.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//does stuff when button pressed
                    sneaky.tempGraph.showGraph(true);
                }//new action performed 
            });//inner class action listner
            tempB.setPreferredSize( new Dimension(140,20) ); 
        menu.add(tempB);
        
        JButton presB = new JButton("Show Pressure VS # Particle regression");//button
            presB.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//does stuff when button pressed
                    sneaky.presGraph.showGraph(true);
                }//new action performed 
            });//inner class action listner
            presB.setPreferredSize( new Dimension(140,20) ); 
        menu.add(presB);
        
        JButton humdB = new JButton("Show Humidity VS # Particle regression");//button
            humdB.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//does stuff when button pressed
                    sneaky.humdGraph.showGraph(true);
                }//new action performed 
            });//inner class action listner
            humdB.setPreferredSize( new Dimension(140,20) ); 
        menu.add(humdB);
        
        return menu;
    }//mask menu
    
    //beginning of BST node features - in hindsight, a node container for each would've been cleaner.
    public double getEffectiveness(){//getter
        return this.effective;
    }
    public double getInfectAerosol(){//getter
        return this.infectAerosol;
    }
    public double getComfort(){
        return this.comfort;
    }
    
    public int compare(Mask ob2){//overloaded
        if(this.effective > ob2.getEffectiveness())
            return 1;
        else if(this.effective < ob2.getEffectiveness())
            return -1;
        else//equal
            return 0;
    }
    public int compare(double otherEffect){
        if(this.getEffectiveness() > otherEffect)
            return 1;
        else if(this.getEffectiveness() < otherEffect)
            return -1;
        else//equal
            return 0;
    }
    
    public Mask getLeft(){
        return this.left;
    }
    public Mask getRight(){
        return this.right;
    }
    public void setLeft(Mask left){
        this.left = left;
    }
    public void setRight(Mask right){
        this.right = right;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
     double getPPslope() {
        return this.PPslope;
    }
     double getPHslope() {
        return this.PHslope;
    }
     double getPTslope() {
        return this.PTslope;
    }
     double getPPyInt(){
         return this.PPyInt;
     }
     double getPHyInt(){
         return this.PHyInt;
     }
     double getPTyInt(){
         return this.PTyInt;
     }
     double getPPrSq(){
         return this.PPrSq;
     }
     double getPTrSq(){
         return this.PTrSq;
     }
     double getPHrSq(){
         return this.PHrSq;
     }
     ArrayList<Test> getTests(){
        return this.tests;
     }
     TempGraph getTempGraph() {
        return this.tempGraph;
     }
     PresGraph getPresGraph(){
         return this.presGraph;
     }
     HumdGraph getHumdGraph(){
         return this.humdGraph;
     }
}//endclass
