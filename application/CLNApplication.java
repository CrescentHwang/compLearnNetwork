package pers.crescent.compLearnNetwork.application;

import pers.crescent.compLearnNetwork.pers.crescent.compLearnNetwork.compLN.CLData;
import pers.crescent.compLearnNetwork.pers.crescent.compLearnNetwork.compLN.CLN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CLNApplication {

    public static void main(String args[]) {
        List<double[]> datas = new ArrayList<double[]>();
        CLData[] trainData;
        CLData[] testData;

        // 读取数据
        try {
            String pathname = "//Users//crescent//Documents//project//artificial_intelligence//bp_neure_network//src//pers//crescent//bpNeureNetwork//application//data.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                double[] data_new = new double[data.length];
                switch(data[data.length-1]) {
                    case "Iris-setosa": data[data.length-1] = "1";break;
                    case "Iris-versicolor": data[data.length-1] = "2";break;
                    case "Iris-virginica": data[data.length-1] = "3";break;
                }
                // 注：iris.txt 数据集第一列不是分类，最后一列才是分类，需要进行列交换。
                //     wine.txt 数据集第一列是类别属性，所以不需要交换，则把swap(data)备注。
                swap(data);
                for(int i=0; i< data.length; i++) {
                    data_new[i] = Double.parseDouble(data[i]);
                }
                datas.add(data_new);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        trainData = new CLData[datas.size()*2/3];
        testData = new CLData[datas.size()-trainData.length];

        // 最大最小标准化
        int attNum = datas.get(1).length;
        double[] maxs = new double[attNum];
        double[] mins = new double[attNum];

        // 找出最大最小值
        for(int i=0;i<datas.size();i++) {
            for(int j=0; j<attNum; j++) {
                if(datas.get(i)[j] > maxs[j]) {
                    maxs[j] = datas.get(i)[j];
                } else if(datas.get(i)[j] < mins[j]) {
                    mins[j] = datas.get(i)[j];
                }
            }
        }

        // 规范化
        int train_count = 0;
        int test_count = 0;
        for(int i=0;i<datas.size();i++) {
            for(int j=1; j<attNum; j++) {
                datas.get(i)[j] = (datas.get(i)[j] - mins[j]) / (maxs[j] - mins[j]);
            }
            switch(i % 3) {
                case 0:
                    if(test_count < testData.length) {
                        testData[test_count] = new CLData();
                        testData[test_count].setAttributes(datas.get(i));
                        test_count++;
                    } break;
                default:
                    if(train_count < trainData.length) {
                        trainData[train_count] = new CLData();
                        trainData[train_count].setAttributes(datas.get(i));
                        train_count++;
                    } break;
            }
        }


        CLN cln = new CLN(trainData, testData, 0.5, attNum-1, 3, 5000);
        cln.train();
        cln.predict();
        System.out.println("iris 测试集结果测试如上所示。");
    }

    // 交换
    private static void swap(String[] data) {
        String swpTemp = "";
        swpTemp = data[0];
        data[0] = data[data.length-1];
        data[data.length-1] = swpTemp;
    }
}

