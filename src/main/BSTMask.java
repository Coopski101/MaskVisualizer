package cooperzuranski.finalproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class BSTMask implements Serializable{//stored in inOrder effectiveness
    Mask treeRoot;
    int count;
    
    /*public static void main(String[] args){//testharness
        BSTMask joe = new BSTMask("M_1.TXT","cloth");
        joe.BSTinsert("M_2.TXT", "none");
        joe.BSTinsert("M_3.TXT", "n95");
        joe.BSTinsert("M_4.TXT", "bandana");
        joe.inOrder(joe.treeRoot);
        System.out.println(joe.BSTsearch(38652.6915, "n95").name);
        joe.BSTdelete(38652.6915, "n95");
        joe.inOrder(joe.treeRoot);
        //System.out.println(joe.BSTsearch(38652.6915, "n95").name);//hopefully makes it crash!
        joe.BSTsearch(32561.257758333333, "none").append("M_1.TXT");//bc the og files have the wrong axis, recalculation of lin reg needed, append
        joe.BSTsearch(-23554.375605328667, "none").showTable(true);
        joe.BSTsearch(-23554.375605328667, "none").humdGraph.showGraph(true);
        joe.BSTsearch(-23554.375605328667, "none").presGraph.showGraph(true);
        joe.BSTsearch(-23554.375605328667, "none").tempGraph.showGraph(true);
        
        //joe.BSTsearch(38652.6915, "n95").append("M_4.TXT");
        //joe.inOrder(joe.treeRoot);
    }*/
    
    public BSTMask(){//used in final version when empty
        this.count = 0;
        this.treeRoot = null;
    }
    
    public BSTMask(String filename, String name){//constructor
        this.count = 1;
        this.treeRoot = new Mask(filename, name);
    }//constructor
    
    public void BSTappend(Mask thisMask, String filename){//interface to append since values change, so must BST
        thisMask.append(filename);
        Mask temp = thisMask;//in case this was the only refrence after dlt
        this.BSTdelete(thisMask.effective, thisMask.getName());// fix BST after!
        this.BSTreInsert(temp);
    }//user append
    
    public void inOrder(Mask currentNode){//inorderTraversal for testing if works
        if (currentNode == null)//exit
            return;
        else {
          inOrder(currentNode.getLeft());
          System.out.print(currentNode.effective);//for testing
          System.out.println(currentNode.name);
          inOrder(currentNode.getRight());
        } 
    } //inorder
    
    public void inOrderQueue(Queue<Mask> retQueue, Mask currentNode){//used to display later on in order
        if(currentNode == null){
            return;
        }else{
            inOrderQueue(retQueue,currentNode.getLeft());
            retQueue.add(currentNode);
            inOrderQueue(retQueue, currentNode.getRight());
        }
    }//inorderqueue
    
    public boolean BSTinsert(String filename, String name){//user interface bst insertion
        int beforeCount = this.count;
        Mask toInsert = new Mask( filename,  name);
                
        if(this.count == 0){
            this.treeRoot = toInsert;
            this.count++;
        }else
            insert(this.treeRoot, toInsert);
        
        if(beforeCount != this.count)
            return true;//should always be true unless theres a bajillion masks
        return false;
    }//bst insert
    
    private Mask insert(Mask root, Mask toInsert){//internal recursive fn
        if(root == null){//exit found correct spot
            this.count++;
            return toInsert;
        }else if(root.compare(toInsert) == 1 ){//insert before currentbefore 
            root.left = insert(root.left, toInsert);
            return root;
        } else if(root.compare(toInsert) == -1 || root.compare(toInsert) == 0){//insert after this root or same
            root.right = insert(root.right, toInsert);
            return root;
        }
        return root;
    }//insert
    
    public boolean BSTdelete(double effect2Dlt, String name2Dlt){//user end for delete, utilized in re organizaion in this program
        int beforeCount = this.count;
        Mask newRoot = delete(this.treeRoot,effect2Dlt, name2Dlt);
        
        if(this.count != beforeCount){//if successfully deleted
            this.treeRoot = newRoot;
            if(this.count == 0)//empty
                this.treeRoot = null;
            return true;
        }else
            return false;
    }//BSTdelete
    
    private Mask delete(Mask root, double effect2Dlt, String name2Dlt){//recursive guts of delete
        Mask toDelete, toSwap, newRoot;
        String nameTemp;//tempdata
        ArrayList<Test> testsTemp;
        double effectiveTemp, comfortTemp, aeroTemp;
        double PPyIntTemp, PPslopeTemp, PPrSqTemp;
        double PHyIntTemp, PHslopeTemp, PHrSqTemp;
        double PTyIntTemp, PTslopeTemp, PTrSqTemp;
        TempGraph tempGraphTemp;
        HumdGraph humdGraphTemp;
        PresGraph presGraphTemp;
        
        if(root == null)
            return null;
        else if(root.compare(effect2Dlt) == -1)//this root precedes to delete
            root.right = delete(root.right, effect2Dlt, name2Dlt);
        else if(root.compare(effect2Dlt) == 1)//after
            root.left = delete(root.left, effect2Dlt, name2Dlt);
        else if((root.compare(effect2Dlt) == 0) && (name2Dlt.equals(root.getName()))){//protects against duplicate values by also checking name
            toDelete = root;
            if(root.left == null){//no left
                newRoot = root.right; //re link
                root.right = null;//rid refrence
                this.count--;//determines success and always hit
                return newRoot;
            }else if(root.right == null){//only left
                newRoot = root.left;
                root.left = null;
                this.count--;
                return newRoot;
            }else{//two children
                toSwap = root.left;
                while(toSwap.right != null)//find immediate predecessor
                    toSwap = toSwap.right;
                
                //painful data swapping bc no deep copying - w/ class relationships/scope I may have made this harder than need be...
                nameTemp = toDelete.getName();
                testsTemp = toDelete.getTests();//refrence
                effectiveTemp = toDelete.getEffectiveness();
                aeroTemp = toDelete.getInfectAerosol();
                comfortTemp = toDelete.getComfort();
                PPyIntTemp = toDelete.getPPyInt(); 
                PPslopeTemp = toDelete.getPPslope();
                PPrSqTemp = toDelete.getPPrSq();
                PHyIntTemp = toDelete.getPHyInt();
                PHslopeTemp = toDelete.getPHslope();
                PHrSqTemp = toDelete.getPHrSq();
                PTyIntTemp = toDelete.getPTyInt();
                PTslopeTemp = toDelete.getPTslope();
                PTrSqTemp = toDelete.getPTrSq();
                tempGraphTemp = toDelete.getTempGraph();
                humdGraphTemp = toDelete.getHumdGraph();
                presGraphTemp = toDelete.getPresGraph();
                
                root.name = toSwap.getName();
                root.tests = toSwap.getTests();//refrence
                root.effective = toSwap.getEffectiveness();
                root.infectAerosol = toSwap.getInfectAerosol();
                root.comfort = toSwap.getComfort();
                root.PHrSq = toSwap.getPHrSq();                
                root.PHslope = toSwap.getPHslope();
                root.PHyInt = toSwap.getPHyInt();
                root.PPrSq = toSwap.getPPrSq();
                root.PPslope = toSwap.getPPslope();
                root.PPyInt = toSwap.getPPyInt();
                root.PTrSq = toSwap.getPTrSq();
                root.PTslope = toSwap.getPTslope();
                root.PTyInt = toSwap.getPTyInt();
                root.tempGraph = toSwap.getTempGraph(); 
                root.humdGraph = toSwap.getHumdGraph();
                root.presGraph = toSwap.getPresGraph();
                
                toSwap.name = nameTemp;
                toSwap.tests = testsTemp;
                toSwap.effective = effectiveTemp;
                toSwap.comfort = comfortTemp;
                toSwap.infectAerosol = aeroTemp;
                toSwap.PHrSq = PHrSqTemp;                
                toSwap.PHslope = PHslopeTemp;
                toSwap.PHyInt = PHyIntTemp;
                toSwap.PPrSq = PPrSqTemp;
                toSwap.PPslope = PPslopeTemp;
                toSwap.PPyInt = PPyIntTemp;
                toSwap.PTrSq = PTrSqTemp;
                toSwap.PTslope = PTslopeTemp;
                toSwap.PTyInt = PTyIntTemp;
                toSwap.tempGraph = tempGraphTemp; 
                toSwap.humdGraph = humdGraphTemp;
                toSwap.presGraph = presGraphTemp;
                
                root.left = delete(root.left, toSwap.getEffectiveness(), toSwap.getName());//one more call deletes leaf just swapped
            }
        }else
            return null;//duplicate effectiveness
        return root;
    }//internal delete
    
    public Mask BSTnameSearch(String search){//user use, needed because LH side list I want to display best to worst, still need rational search forother stuff
        Stack<Mask> theStack = new Stack<Mask>();
        nameStack(theStack, this.treeRoot);
        
        while(!theStack.isEmpty()){//keep popping until one matches
            Mask thisMask = theStack.pop();
            if(thisMask.name.equals(search))
                return thisMask;
        }
        return null;//if reaches here, not found
    }//name search
    
    private void nameStack(Stack<Mask> theStack, Mask currentNode){//inorder traversal to build a stack of masks, used to search by name w/o L/R choice
        if(currentNode == null){
            return;
        } else{
            nameStack(theStack,currentNode.getLeft());
            theStack.push(currentNode);
            nameStack(theStack,currentNode.getRight());
        }
    }//name stack
            
    public Mask BSTsearch(double effect2find, String name2find){//for user
        return search(this.treeRoot, effect2find, name2find);//recursive internals
    }//bst search
    
    private Mask search(Mask root, double effect2find, String name2find){//recursive guts
        if(root == null)
            return null;
        else if(root.compare(effect2find) == -1)//root precedes find
            return search(root.right, effect2find, name2find);
        else if(root.compare(effect2find) == 1)//root is after find
            return search(root.left, effect2find, name2find);
        else if((root.compare(effect2find) == 0) && (name2find.equals(root.getName())))//protects against duplicate values by also checking name
            return root;
        else//duplicate
            return null;
    }//search

    private boolean BSTreInsert(Mask toInsert){//overloaded to insert pre exsisting mask obj - used in append
        int beforeCount = this.count;
                
        if(this.count == 0){
            this.treeRoot = toInsert;
            this.count++;
        }else
            insert(this.treeRoot, toInsert);
        
        if(beforeCount != this.count)
            return true;//should always be true unless theres a bajillion masks
        return false;
    }//bst insert
}//endclass
    
