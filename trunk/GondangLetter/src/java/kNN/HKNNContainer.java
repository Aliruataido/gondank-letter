package kNN;

import evaluation.EvalUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.FileUtil;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class HKNNContainer {

    private HKNNClassifier mHKNNClassifier;
    
    private int[] removedAttributes;
    
    private int classIndex;
    
    private Instances trainingSet;   
    
    private int testOption;
    
    private float testOptionVal;
    
    public static final int NONE = 0;
    
    public static final int CROSS_VALIDATION = 1;
    
    public static final int PERCENTAGE_SPLIT = 2;     
    
    public static final int TRAINING_SET = 3;
    
    //sementara pake file
    public HKNNContainer(int k, Instances trainingSet, int[] removedAttributes, int classIndex) {                        
        try {
            mHKNNClassifier = new HKNNClassifier(k);

            this.trainingSet = trainingSet;                        

            //set class attribute           
            this.trainingSet.setClass(this.trainingSet.attribute(classIndex));                                                                                    

            //remove attribute
            Remove r = new Remove();
            r.setAttributeIndicesArray(removedAttributes);
            r.setInputFormat(this.trainingSet);
            this.trainingSet = Filter.useFilter(this.trainingSet, r);                        

            System.out.println("jumlah attribute training set : " + this.trainingSet.numAttributes());
        } catch (Exception ex) {
            Logger.getLogger(HKNNContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
    
    //latih HKNNClassifier dengan trainingSet
    public void trainModel() {
        try {
            mHKNNClassifier.buildClassifier(trainingSet);
        } catch (Exception ex) {
            System.err.println("err : " + ex.getMessage());                
        }
    }        
    
    public String outputModel() {
        return mHKNNClassifier.toString(); 
    }        
    
    //sebelumnya classifier harus di-train terlebih dahulu
    //jika learn true, maka test set tersebut akan dimasukkan ke training set
    public Instances classifyData(Instances data, boolean learn) throws Exception {
        // create copy
        Instances labeled = new Instances(data);

        // label instances
        for (int i = 0; i < data.numInstances(); i++) {            
            double clsLabel = mHKNNClassifier.classifyInstance(data.instance(i));
            labeled.instance(i).setClassValue(clsLabel);            
            if(learn) {
                mHKNNClassifier.updateClassifier(labeled.instance(i));
            }
        }               
        
        return labeled;
    }
    
    public HKNNClassifier getClassifier() {        
        return mHKNNClassifier;
    }
    
    public Instances getTrainingSet() {
        return trainingSet;
    }       
    
    public static void main(String[] args) {
        try {
            int[] removedAttributes = new int[] {};    
            Instances trainingSet = FileUtil.loadInstances("contact-lenses.arff");            
            
            System.out.println("=====TES PERCENTAGE SPLIT======");
            HKNNContainer hc = new HKNNContainer(1, trainingSet, removedAttributes, 4);
            System.out.println(EvalUtil.percentageSplit(hc.getClassifier(), hc.getTrainingSet(), 66));

            System.out.println("=====TES USE TRAINING SET=======");
            HKNNContainer hct = new HKNNContainer(3, trainingSet, removedAttributes, 4);
            System.out.println(EvalUtil.useTrainingSet(hct.getClassifier(), hct.getTrainingSet()));

            System.out.println("====OUTPUT MODEL=======");
            HKNNContainer hc2 = new HKNNContainer(3, trainingSet, removedAttributes, 4);
            hc2.trainModel();
            hc2.outputModel();

            System.out.println("====TES DISCRETIZE======");
    //        HKNNContainer hc3 = new HKNNContainer(1, trainingSet, removedAttributes, 0);
    //        Instances discdata = PrepUtil.unsupervisedDiscretize(hc3.getTrainingSet());        
    //        try {
    //            FileUtil.saveInstances(discdata, "test-discretize.arff");            
    //        } catch (Exception ex) {            
    //        }

            System.out.println("====TES NORMALIZE======");
    //        HKNNContainer hc4 = new HKNNContainer(1, trainingSet, removedAttributes, 0);
    //        Instances normdata = PrepUtil.unsupervisedNormalize(hc3.getTrainingSet());
    //        try {
    //            FileUtil.saveInstances(normdata, "test-normalize.arff");            
    //        } catch (Exception ex) {            
    //        }

            System.out.println("====TES classify=======");
            HKNNContainer hcc = new HKNNContainer(1, trainingSet, removedAttributes, 4);        
            hcc.trainModel();
            Instances testdata=null;
            try {
                testdata = FileUtil.loadInstances("contact-lenses-testset.arff");
                testdata.setClassIndex(4);
            } catch (Exception ex) {
                System.out.println("err : " + ex.getMessage());
            }                                                 

            Instances result = null;          
            try {
                result = hcc.classifyData(testdata, false);
            } catch (Exception ex) {
                Logger.getLogger(HKNNContainer.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("result : ");
            System.out.println(result.toString());
        } catch (Exception ex) {
            Logger.getLogger(HKNNContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
