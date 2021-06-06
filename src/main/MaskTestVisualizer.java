/**
 * @author Cooper Zuranski, 2021
 */
package cooperzuranski.finalproject;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class MaskTestVisualizer {
    protected BSTMask masks;
    public JFrame frame;
    private double aEffVal, bEffVal;//to be sneaky with inner classes
    public JPanel  window, mainPanel, mainContainer, upperPanel, sidePanelContainer, sidePanel;//to access via inner classes for state changes 
    public JScrollPane sideScroll;
    protected JButton goButton;//controlls states
    public Mask compA, compB;//set for compare panels
    public boolean compChg, compSearch;//set for compare panels
    private JTextField presin, humdin, tempin;
    public int numSelected;//for check boxes
    
    public static void main(String[] args){
        new MaskTestVisualizer();
    }
    
    public MaskTestVisualizer(){//psuedo main menu
        this.aEffVal = this.bEffVal = 0.0;
        this.compChg = false;
        this.compSearch = false;
        this.masks = new BSTMask();
        this.loadSerial();
        MaskTestVisualizer sneaky = this;//sneaky way around internal class locals
        this.frame = new JFrame();
            this.frame.addWindowListener(new WindowAdapter() {//saves on close
                public void windowClosing(WindowEvent e) {
                  sneaky.saveSerial();
                }
            });
        
        this.window = new JPanel(new BorderLayout());//l, center, top 
            this.window.setPreferredSize(new Dimension(1000,800));
        this.mainContainer = new JPanel(new BorderLayout());
            this.mainPanel = new JPanel(new BorderLayout());
            this.mainContainer.add(this.mainPanel);
            this.mainContainer.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(25.0f)));
        this.upperPanel = this.upPanel();//upper panel
            this.upperPanel.setPreferredSize(new Dimension(1000,50));
        this.sidePanelContainer = new JPanel(new BorderLayout());//scrolly list
            this.sideScroll = new JScrollPane(this.sidePanelContainer,VERTICAL_SCROLLBAR_AS_NEEDED,HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.sidePanel = new JPanel(new GridLayout(0,1));
            if(this.masks.count > 0)//if any loaded in
               this.sidePanel.add(this.sidePanelMaker());
            this.sidePanelContainer.add(this.sidePanel);
                 
        window.add(this.mainContainer);
        window.add(this.upperPanel, BorderLayout.NORTH);
        window.add(this.sidePanelContainer, BorderLayout.WEST);
        frame.setPreferredSize(new Dimension(907,650));
        frame.add(window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    private JPanel upPanel(){
        MaskTestVisualizer sneaky = this;//sneaky way around internal class locals
        JPanel top = new JPanel(new GridLayout(1,6));

        this.goButton = new JButton("GO");//button for process
            goButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//start state machine
                    sneaky.mainHandler();
                }//new action performed 
            });//inner class action listner
            goButton.setPreferredSize( new Dimension(1,20) ); 
        top.add(goButton);
        top.add(new JLabel());//emptyspace
        
        JButton fileIn = new JButton("Add Mask Test file");//button
        fileIn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//does stuff when button pressed
                    sneaky.fileIn();
                }//new action performed 
            });//inner class action listner
        top.add(fileIn);
        
        top.add(new JLabel());//emptyspace
        JTextField search = new JTextField("search", JTextField.CENTER);//text for search
        top.add(search,4);
        
        JButton searchBut = new JButton("Search");//button for search
            searchBut.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//does stuff when button pressed
                    try{
                        sneaky.compSearch = true;//for state machine
                        search.setBackground(Color.white);//reset color
                        Mask mainAlone = sneaky.masks.BSTnameSearch(search.getText());
                        sneaky.mainContainer.remove(sneaky.mainPanel);//remove
                        sneaky.mainPanel.removeAll();
                        sneaky.mainPanel.add( mainAlone.maskMenu());//throws if null
                        sneaky.mainContainer.add(sneaky.mainPanel);
                        sneaky.mainContainer.revalidate();
                        sneaky.mainContainer.repaint();//refresh
                    } catch(Exception e){//if invalid search
                        search.setBackground(Color.red);//invalid 
                    }
                }//new action performed 
            });//inner class action listner
            searchBut.setPreferredSize( new Dimension(150,20) ); 
        top.add(searchBut,5);
        
        return top;
    }//uppper panel
    
    public void fileIn(){
        MaskTestVisualizer sneaky = this;
        JFrame frame = new JFrame();
        JPanel main = new JPanel( new GridLayout(2,2));
        JButton append = new JButton("Append");
        JButton addNew = new JButton("Add New ");//double click
        JTextField name = new JTextField("Mask Name");
        JTextField file = new JTextField("Filename (w/ extension)");
        
        addNew.addActionListener(new ActionListener(){//add a new mask from file
            public void actionPerformed(ActionEvent a){
                name.setBackground(Color.WHITE);//reset
                
                if(!sneaky.masks.BSTinsert(file.getText(), name.getText()))
                    name.setBackground(Color.RED);//insert fail
                else{//refresh on success
                    sneaky.compChg = true;
                    sneaky.sidePanelContainer.remove(sneaky.sidePanel);
                    sneaky.sidePanel.removeAll();
                    sneaky.sidePanel.add(sneaky.sidePanelMaker());
                    sneaky.sidePanelContainer.add(sneaky.sidePanel);
                    sneaky.sidePanelContainer.revalidate();
                    sneaky.sidePanelContainer.repaint();
                }
            }
        });
        
        append.addActionListener(new ActionListener(){//update files and side panel order
            public void actionPerformed(ActionEvent a){
                sneaky.masks.BSTappend(sneaky.masks.BSTnameSearch(name.getText()), file.getText());
                sneaky.sidePanelContainer.remove(sneaky.sidePanel);
                sneaky.sidePanel.removeAll();
                sneaky.sidePanel.add(sidePanelMaker());
                sneaky.sidePanelContainer.add(sneaky.sidePanel);
                sneaky.sidePanelContainer.revalidate();
                sneaky.sidePanelContainer.repaint();  
            }
        });
        
        main.add(append);//build panels
        main.add(addNew);
        main.add(name);
        main.add(file);
        main.setPreferredSize(new Dimension(300,150));
        frame.add(main);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }//file input pop up
    
    public void mainHandler(){//state machine 
        MaskTestVisualizer sneaky = this;
        
        if(this.compSearch && !this.compChg){//if search was done
            this.compSearch = false;
            this.aEffVal = this.bEffVal =0.0;
            this.mainContainer.remove(this.mainPanel);
            this.mainPanel.removeAll();
            this.mainPanel.setLayout(new BorderLayout());
            this.mainPanel.add(compareMasksUp(this.compA, this.compB), BorderLayout.NORTH);
            this.mainContainer.add(this.mainPanel);
            this.mainContainer.revalidate();//update
            this.mainContainer.repaint();          
        }else if(this.compChg){//mask selection changed
            if(this.compSearch){
                this.compSearch = false;
            }
            
            if(this.compChg && !(this.compA != null && this.compB != null) ){//not when chg to two
                if(this.goButton.getActionListeners().length == 2)//2 listeners (one is calc)
                    this.goButton.removeActionListener(this.goButton.getActionListeners()[1]);//remove calculation feature
            }
            
            this.compChg = false;//toggle back
            
            if(this.compA == null ^ this.compB == null){//one selection, show one only like search
                this.mainContainer.remove(this.mainPanel);
                this.mainPanel.removeAll();
                if(this.compA != null){//which is it
                    this.mainPanel.add(this.masks.BSTsearch(this.compA.getEffectiveness(), this.compA.getName()).maskMenu());
                    this.bEffVal = 0;//reset
                }else{
                    this.mainPanel.add(this.masks.BSTsearch(this.compB.getEffectiveness(), this.compB.getName()).maskMenu());
                    this.aEffVal = 0;//reset
                }
                this.mainContainer.add(this.mainPanel);
                this.mainContainer.revalidate();//update
                this.mainContainer.repaint();
            }else if((this.compA != null && this.compB != null) && (this.aEffVal == 0 || this.bEffVal ==0)){//masks exsist, no vals
                this.mainContainer.remove(this.mainPanel);
                this.mainPanel.removeAll();
                this.mainPanel.add(compareMasksUp(this.compA, this.compB), BorderLayout.NORTH);
                this.mainContainer.add(this.mainPanel);
                this.mainContainer.revalidate();//update
                this.mainContainer.repaint();
            }else{//empty
                this.bEffVal = this.aEffVal = 0;//reset
                this.mainContainer.remove(this.mainPanel);//remove
                this.mainPanel.removeAll();
                this.mainContainer.add(this.mainPanel);
                this.mainContainer.revalidate();
                this.mainContainer.repaint();//refresh
            }
        }else if((this.compA != null && this.compB != null) && !(this.aEffVal == 0 && this.bEffVal ==0)){//ready for lower panel - triggered by go button
            this.mainContainer.remove(this.mainPanel);
            if(this.mainPanel.getComponentCount()>1)//remove and add
                for(int i = 1; i <= this.mainPanel.getComponentCount(); i++)
                    this.mainPanel.remove(i);
            this.mainPanel.add(compareMasksDown(this.compA, this.compB), BorderLayout.CENTER);   
             this.goButton.addActionListener(new ActionListener(){//idk why OF ALL THINGS THIS IS WHAT FIXED THE BUG BUT OK
                public void actionPerformed(ActionEvent a){//calculates values
                    if((sneaky.compA!=null) && (sneaky.compB != null)) 
                        sneaky.setVals(estimatedVal(sneaky.compA,Double.valueOf(sneaky.tempin.getText()),Double.valueOf(sneaky.presin.getText()),Double.valueOf(sneaky.humdin.getText()))
                            ,estimatedVal(sneaky.compB,Double.valueOf(sneaky.tempin.getText()),Double.valueOf(sneaky.presin.getText()),Double.valueOf(sneaky.humdin.getText())));                            
                }//new action performed 
            });//inner class action listner
            this.mainContainer.add(this.mainPanel);
            this.mainContainer.revalidate();//update
            this.mainContainer.repaint();
        }else{//reset state
            this.bEffVal = this.aEffVal = 0;//reset
            this.mainContainer.remove(this.mainPanel);//remove
            this.mainPanel.removeAll();
            this.mainContainer.add(this.mainPanel);
            this.mainContainer.revalidate();
            this.mainContainer.repaint();//refresh
        }
    }//main state machine
    
    public JPanel sidePanelMaker(){
        MaskTestVisualizer sneaky = this;
        JPanel side = new JPanel(new GridLayout(0,1));//rows masks, cols 0-name, 1-effect, 2- ceacknbox
        side.setPreferredSize(new Dimension(150,25));
        Queue<Mask> retQueue = new LinkedList<Mask>();//retrieve queue
        this.masks.inOrderQueue(retQueue, this.masks.treeRoot);//order to display
        JPanel head = new JPanel(new FlowLayout());//header
        head.add(new JLabel("NAME"));//headers
        head.add(new JLabel("RATING"));
        head.add(new JLabel("----"));
        head.setPreferredSize(new Dimension(150,25));
        side.add(head);
        
        for(int i = 0; i<= retQueue.size(); i++){//add all, each own panel
            JPanel each = new JPanel(new FlowLayout());
            Mask curMask = retQueue.remove();
            each.add(new JLabel(curMask.getName()));
            each.add(new JLabel(String.format("%.2f",curMask.getEffectiveness())));
            JCheckBox curBox = new JCheckBox();
                curMask.myBox = curBox;//a trick to remotely toggle for correct states
            curBox.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//handles states of checkboxes seperate form main panel
                    if(curBox.isSelected()){//just turned on
                        if((sneaky.compA != null) && (sneaky.compB !=null))//only 2 max
                            curBox.doClick();
                        else if((sneaky.compA == null) && (sneaky.compB != null)){
                            sneaky.compA = curMask;
                            sneaky.compChg = true;//set flag
                        }else if((sneaky.compA != null) && (sneaky.compB == null)){
                            sneaky.compB = curMask;
                            sneaky.compChg = true;
                        }else{
                            sneaky.compA = curMask;
                            sneaky.compChg = true;
                        }
                        sneaky.mainHandler();//state machine
                    }else{//was just turned off
                        sneaky.compChg = true;
                        if(curMask.compare(sneaky.compA)==0 ){//reset placeholders
                            sneaky.compA = null;
                        }else
                            sneaky.compB = null;
                        sneaky.mainHandler();//state machine
                    }//else turned off
                }//new action performed 
            });//inner class action listner
            
            each.add(curBox);//build panel
            each.setPreferredSize(new Dimension(150,25));
            side.add(each);
        }//for all
        return side;
    }//side panel
    
    private JPanel compareMasksUp(Mask A, Mask B){//panel for mask overlay
        MaskTestVisualizer sneaky = this;//sneaky way around internal class locals w refrence
        JPanel input = new JPanel(new FlowLayout());//textboxes and labels
            JLabel forl = new JLabel("For ", JLabel.LEFT);//labels inbetwwen textboxes
            JLabel templ = new JLabel("C     ", JLabel.LEFT);
            JLabel presl = new JLabel("Pa    ", JLabel.LEFT);
            JLabel humdl = new JLabel("% humd ", JLabel.LEFT);
            this.tempin = new JTextField("");
                this.tempin.setPreferredSize( new Dimension(100,20) );
            this.presin = new JTextField("");
                this.presin.setPreferredSize( new Dimension(100,20) );
            this.humdin = new JTextField("");
                this.humdin.setPreferredSize( new Dimension(100,20) );
        
        input.add(forl);
        input.add(this.tempin);
        input.add(templ);
        input.add(this.presin);
        input.add(presl);
        input.add(this.humdin);
        input.add(humdl);
        
        if(goButton.getActionListeners().length==1){
            this.goButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent a){//calculates values
                    try{ 
                        tempin.setBackground(Color.white);//reset color
                        humdin.setBackground(Color.white);
                        presin.setBackground(Color.white);
                        sneaky.setVals(estimatedVal(A,Double.valueOf(tempin.getText()),Double.valueOf(presin.getText()),Double.valueOf(humdin.getText()))
                            ,estimatedVal(B,Double.valueOf(tempin.getText()),Double.valueOf(presin.getText()),Double.valueOf(humdin.getText())));                            
                    }catch(Exception e){//bad input or none
                        tempin.setBackground(Color.red);//error state
                        humdin.setBackground(Color.red);
                        presin.setBackground(Color.red);
                    }
                }//new action performed 
            });//inner class action listner
        }//if only orig listener
        return input;
    }//compare mask upper panel
    
    private JPanel compareMasksDown(Mask A, Mask B){//bottom of compare table. not used when no entry
        JPanel myPanel = new JPanel(new BorderLayout());
        JPanel myTable = new JPanel(new GridLayout(0,3));
        JLabel aName = new JLabel(A.getName(), JLabel.CENTER);
        JLabel bName = new JLabel(B.getName(), JLabel.CENTER);
        JLabel aEff = new JLabel(Double.toString(this.aEffVal), JLabel.CENTER );// note that a lowe effectiveness score is better ironically   
        JLabel aCom = new JLabel(Double.toString(A.getComfort()) , JLabel.CENTER);
        JLabel bEff = new JLabel(Double.toString(this.bEffVal), JLabel.CENTER );
        JLabel bCom = new JLabel(Double.toString(B.getComfort()) , JLabel.CENTER);
        
        if(this.aEffVal< this.bEffVal){//tables background colors
            aEff.setBackground(Color.GREEN);// better one becomes Green
            bEff.setBackground(Color.RED);
        }else{
            aEff.setBackground(Color.RED);// better one becomes Green
            bEff.setBackground(Color.GREEN);        
        }
        if(A.getComfort() < B.getComfort()){// smaller/\ pressure is better
            aCom.setBackground(Color.GREEN);// better one becomes Green
            bCom.setBackground(Color.RED);
        }else{
            aCom.setBackground(Color.RED);// better one becomes Green
            bCom.setBackground(Color.GREEN);        
        }
            aName.setOpaque(true);//visual stuffs
            bName.setOpaque(true);
            aEff.setOpaque(true);
            bEff.setOpaque(true);
            aCom.setOpaque(true);
            bCom.setOpaque(true);
            aName.setFont(new Font("Verdana", Font.PLAIN, 18));//font
            bName.setFont(new Font("Verdana", Font.PLAIN, 18));
            aEff.setFont(new Font("Verdana", Font.PLAIN, 18));
            bEff.setFont(new Font("Verdana", Font.PLAIN, 18));
            aCom.setFont(new Font("Verdana", Font.PLAIN, 18));
            bCom.setFont(new Font("Verdana", Font.PLAIN, 18));
            aName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            bName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            aEff.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            bEff.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            aCom.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            bCom.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            myTable.add(new JLabel("Name", JLabel.CENTER));
            myTable.add(new JLabel("Effectiveness Value (#particles <=5um)", JLabel.CENTER));
            myTable.add(new JLabel("Comfort (Pa)", JLabel.CENTER));
            myTable.add(aName);
            myTable.add(aEff);
            myTable.add(aCom);
            myTable.add(bName);
            myTable.add(bEff);
            myTable.add(bCom);
        
        myPanel.add(myTable); //set up panel    
            JLabel title = new JLabel("<html>Where smaller estimated effectiveness val is better, as it represents # particles."
                    + "<br>-------------------Where Comfort is /\\ pressure in vs outside of mask.-------------------</html>", JLabel.CENTER);
            title.setFont(new Font("Verdana", Font.PLAIN, 12));
            myPanel.add(title, BorderLayout.NORTH);//description and table
        return myPanel;
    }//lower panel
    
    private double estimatedVal(Mask A, double temp, double pres, double humd){//just encapsulation for compare
        double APP = A.getPPslope()*(pres) + A.getPPyInt();//estimation based on user inputs
        double APT = A.getPTslope()*(temp) + A.getPTyInt();
        double APH = A.getPHslope()*(humd) + A.getPHyInt();
        return (APH + APT + APP)/3.0;// average of all 3 predictions at ideal conditions
    }//estimated val
 
    private void setVals(double A, double B){//sneaky way around nested class final
        this.aEffVal = A;//inconsistent with rest of style of this class
        this.bEffVal = B;
    }//set values for comparison
    
    private void loadSerial(){//load serialized data
        try{
            FileInputStream fileIn = new FileInputStream("data.dat");
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            this.masks = (BSTMask)objIn.readObject();
            objIn.close();
            fileIn.close();
        }catch(Exception e){
            System.out.println( e.getMessage());//stock message
        }//trycatch
    }//loadserial
    
    private void saveSerial(){//saves serialized data
        try{//must be in try catch
            FileOutputStream fileOut = new FileOutputStream("data.dat");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);        
            objOut.writeObject(this.masks);
            objOut.close();
            fileOut.close();
        }catch(Exception e){
            System.out.println( e.getMessage());//stock message
        }//trycatch
  }//saveserial
 
}//class end
