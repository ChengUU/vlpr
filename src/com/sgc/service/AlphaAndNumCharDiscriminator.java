package com.sgc.service;

import com.sgc.bean.RecogTemplate;

/**
 * Created by ChengXX on 2017/4/21.
 */
public class AlphaAndNumCharDiscriminator extends Discriminator {
    private static volatile Discriminator discriminator=null;

    private AlphaAndNumCharDiscriminator(RecogTemplate[] recgTemplates) {
        super(recgTemplates);
    }

    // 定义一个类属性方法返回该类型实例
    public static Discriminator getInstance(){
        // 对象实例化与否判断(不适用同步代码块,discriminator不等于null时,直接返回对象)
        if(null==discriminator){
            // 同步代码块(对象未初始化,使用同步代码块,保证多线程访问时实例对象在第一次创建后,不再重复创建)
            synchronized (Discriminator.class){
                // 未初始化则进行初始化
                RecogTemplate[] recgTmp1=getRecgTemplates(2);
                RecogTemplate[] recgTmp2=getRecgTemplates(3);
                int len=recgTmp1.length+recgTmp2.length;
                RecogTemplate[] recgTemplates=new RecogTemplate[len];
                int i=0;
                for(int j=0;j<recgTmp1.length;j++) recgTemplates[i++]=recgTmp1[j];
                for(int j=0;j<recgTmp2.length;j++) recgTemplates[i++]=recgTmp2[j];
                if(null==discriminator)
                    discriminator=new AlphaAndNumCharDiscriminator(recgTemplates);
            }
        }
        return  discriminator;
    }
}
