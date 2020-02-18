package pers.crescent.compLearnNetwork.compLN;


import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

// 竞争学习神经网络
public class CLN {
    // 默认第一列为类别
    private CLData[] trainData;
    private CLData[] testData;
    // 输出层
    private Neure outputN[];
    // 欧几里得距离
    double distance[];
    // 学习速率
    private double yita;
    private int inputNumber;
    private int outputNumber;
    // 权重矩阵
    private double weights[][];
    // 神经元标记
    private int[] outputNTags;
    // 训练次数
    private int trainTimes;
    // 实际训练次数
    private int actTrainTimes = 0;

    // 输入参数为训练集数据train data，测试集 test data, 初始学习速率yita，输入层、输出层神经元个数、最大训练次数
    public CLN(CLData[] trainData, CLData[] testData, double yita,
               int inputNumber, int outputNumber, int trainTimes) {
        this.trainData = trainData;
        this.testData = testData;
        this.inputNumber = inputNumber;
        this.outputNumber = outputNumber;
        this.outputNumber = outputNumber;
        outputN = new Neure[outputNumber];
        weights = new double[inputNumber][outputNumber];
        this.yita = yita;
        outputNTags = new int[outputNumber];
        this.trainTimes = trainTimes;
        init();
    }

    // 初始化神经网络
    private void init() {
        for(int i=0; i<outputNumber; i++) {
            outputN[i] = new Neure();
        }
        // 初始化权重矩阵
        for(int i=0; i<inputNumber; i++) {
            for(int j=0; j<outputNumber; j++) {
                weights[i][j] = 0.5;
            }
        }
        // 初始化距离向量
        distance = new double[outputNumber];
    }


    // 训练网络
    public void train() {
        boolean flag = true;
        int count = 0;
        // tagCount用于记录输出神经元的对应标签的胜出次数，以方便对神经元进行类别对应。
        while(flag) {
            actTrainTimes++;
            double deltaWeightSum = 0;
            for(int i=0; i<trainData.length; i++) {
                int attrsLength = trainData[i].getAttributes().length-1;
                // attrs为输入向量，即属性向量
                double attrs[] = new double[attrsLength];
                // tag为该数据的类别标签
                double tag = trainData[i].getAttributes()[0];
                System.arraycopy(trainData[i].getAttributes(), 1, attrs,
                        0, attrsLength);

                // 找出胜出神经元
                int winnerId = findWinner(attrs);

                double deltaWeight = 0;
                for(int j=0; j<inputNumber; j++) {
                    // 计算权重变化量
                    deltaWeight = yita * (attrs[j] - weights[j][winnerId]);
                    deltaWeightSum += deltaWeight;
                    // 更新权重
                    weights[j][winnerId] += deltaWeight;
                }

                // 设置输出
                for(int j=0; j<outputNumber; j++) {
                    outputN[j].setOutput(0);
                }
                outputN[winnerId].setOutput(1);

            }
            // 计算权重不再发生改变的次数
            if(deltaWeightSum == 0) {
                count++;
            } else {
                count = 0;
            }

            // 后期降低学习速率
            if(actTrainTimes == trainTimes / 4) {
                yita = 0.01;
            }

            // 次数超过3次即认为训练完成，或者达到10000次循环即结束
            if(count == 3 || actTrainTimes >= trainTimes) {
                // 结束训练的同时，给输出神经元打上标签
                tagOutputN();
                flag = false;
            }
        }
    }


    // 标记输出神经元
    private void tagOutputN() {
        long[][] tagCount = new long[outputNumber][outputNumber];
        for(int i=0; i<trainData.length; i++) {
            int attrsLength = trainData[i].getAttributes().length-1;
            // attrs为输入向量，即属性向量
            double attrs[] = new double[attrsLength];
            // tag为该数据的类别标签
            double tag = trainData[i].getAttributes()[0];
            System.arraycopy(trainData[i].getAttributes(), 1, attrs,
                    0, attrsLength);

            // 找出胜出神经元
            int winnerId = findWinner(attrs);
            tagCount[(int)tag-1][winnerId]++;

            for(int row=0; row<outputNumber; row++) {
                double tempMax = tagCount[row][0];
                int maxCol = 0;
                for(int col=0; col<outputNumber; col++) {
                    if(tagCount[row][col] > tempMax) {
                        tempMax = tagCount[row][col];
                        maxCol = col;
                    }
                }
                // 找到最多胜出次数的id，第k个神经元即为此类。
                outputNTags[maxCol] = row+1;
            }
        }
    }


    // 根据输出向量找出胜出神经元，返回其id
    private int findWinner(double attrs[]) {

        // 计算欧几里得距离
        for(int j=0; j<distance.length; j++) {
            double temp = 0;
            for(int k=0; k<inputNumber; k++) {
                temp += pow(attrs[k] - weights[k][j], 2);
            }
            distance[j] = sqrt(temp);
        }

        // 找出胜出神经元
        double min = distance[0];
        int winnerId = 0;
        for(int j=0; j<outputN.length; j++) {
            if(distance[j] < min) {
                min = distance[j];
                winnerId = j;
            }
        }

        return winnerId;
    }


    // 猜测结果
    public void predict() {
        int errorCount = 0;
        for(int i=0; i<testData.length; i++) {
            int attrsLength = testData[i].getAttributes().length-1;
            // attrs为输入向量，即属性向量
            double attrs[] = new double[attrsLength];
            // tag为该数据的类别标签
            double tag = testData[i].getAttributes()[0];
            System.arraycopy(testData[i].getAttributes(), 1, attrs,
                    0, attrsLength);
            int winnerID = findWinner(attrs);
            System.out.println("__________________________");
            System.out.println("第" + (i+1) + "个数据：");
            System.out.println("数据原类别：" + (int)tag);
            System.out.println("预测类别 ：" + outputNTags[winnerID]);
            System.out.println();
            if((int)tag - outputNTags[winnerID] != 0) {
                errorCount++;
            }
        }
        System.out.println("---------------------------");
        System.out.println("实际循环次数：" + actTrainTimes);
        System.out.println("预测错误次数：" + errorCount);
        System.out.println("测试集数据量：" + testData.length);
        System.out.println("---------------------------");
    }
}




















