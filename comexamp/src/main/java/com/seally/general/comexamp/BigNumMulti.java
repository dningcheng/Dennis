package com.seally.general.comexamp;

/**
 * 一、超出java常规数字类型的大数相乘解决方式：
 * 1、使用数组存储大数
 * 2、进行逐位运算
 * 3、进位和留位
 * 
 * 二、演示例子，计算100！
 */
public class BigNumMulti {
    public static void main( String[] args ){
    	//30414093201713378043612608166064768844377641568960512000000000000
        System.out.println(calcFactorial(50));
    }
    
    //计算阶乘
    public static String calcFactorial(int num){
    	int[] bigNum=new int[1000];
        bigNum[bigNum.length-1]=1;
        for(int i=1;i<=num;i++){
        	everyCalc(bigNum,i);
        }
        //处理计算结果int[]为字符串显示
        StringBuilder result = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<bigNum.length;i++){
        	if(bigNum[i]!=0){
        		begin=true;
        	}
        	if(begin){
        		result.append(bigNum[i]);
        	}
        }
        return result.toString();
    }
    
    //计算单次大数相乘结果
    private static int[] everyCalc(int[] bigNum,int smallNum){
    	//逐位运算
    	for(int i=bigNum.length-1;i>0;i--){
    		bigNum[i]*=smallNum;
    	}
    	//进位和留位
    	for(int i=bigNum.length-1;i>0;i--){
    		bigNum[i-1]+=bigNum[i]/10;
    		bigNum[i]%=10;
    	}
    	//此次运算结果
    	return bigNum;
    }
    
}
