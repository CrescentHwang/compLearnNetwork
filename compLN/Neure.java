package pers.crescent.compLearnNetwork.compLN;

public class Neure {

    // 神经元的输入值
    private double input;
    // 神经元的输出值
    private double output;

    // 设置该神经元的输入值
    public void setInput(double input) {
        this.input = input;
    }

    // 设置该神经元的输出
    public void setOutput(double output) {
        this.output = output;
    }

    // 获取该神经元的输出值
    public double getOutput() {
        return this.output;
    }
}
