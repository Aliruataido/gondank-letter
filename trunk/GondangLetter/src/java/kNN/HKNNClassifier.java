package kNN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

public class HKNNClassifier extends IBk {                    
    
    public HKNNClassifier(int k) {
        super(k);
    }

//    @Override
//    public void buildClassifier(Instances instances) throws Exception {
//        //super.buildClassifier(instances);
//        
//        // can classifier handle the data?
//        getCapabilities().testWithFail(instances);
//
//        // remove instances with missing class
//        instances = new Instances(instances);
//        instances.deleteWithMissingClass();
//
//        
//    }        
    
    public static void main(String[] args) {
        try {
            HKNNClassifier hknn  = new HKNNClassifier(1);
            
            //parse file input
            BufferedReader reader = new BufferedReader(new FileReader("contact-lenses.arff"));
            Instances trainingSet = new Instances(reader);            
            reader.close();                        
            
            //set class attribute           
            trainingSet.setClass(trainingSet.attribute("contact-lenses"));                                                            
            
            //cross validation (gak boleh ditrain classifier sebelumnya!) 
            Evaluation crosseval = new Evaluation(trainingSet);
            crosseval.crossValidateModel(hknn, trainingSet, 10, new Random(1));            
            System.out.println(crosseval.toSummaryString("\nResults\n=========\n",false));                                                                                            
            
            //train model                                    
            hknn.buildClassifier(trainingSet);                                    
            
            //buat instances dari dataset
            Instances dataset = new Instances(new BufferedReader(new FileReader("contact-lenses-testset.arff")));
            dataset.setClass(dataset.attribute("contact-lenses"));
            
            //buat copy dari dataset yang menyimpan hasil klasifikasi
            Instances labeled = new Instances(dataset);                 
            
            //iterasi
            System.out.println(dataset.numInstances());                                        
            for(int i=0;i<dataset.numInstances();i++) {                         
                double classLabel = hknn.classifyInstance(dataset.instance(i));
                labeled.instance(i).setClassValue(classLabel);
                System.out.println("klasifikasi instance " + i + " : " + labeled.classAttribute().value((int) classLabel));
            }                        
            
            Evaluation eval = new Evaluation(trainingSet);
            eval.evaluateModel(hknn, dataset);
            System.out.println(eval.toSummaryString("\nResults\n=========\n",false));                                                                                            
            
        } catch (Exception ex) {
            Logger.getLogger(HKNNClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
