package com.sgc.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by ChengXX on 2017/3/25.
 */
public class CountWords {
    public static void main(String[] args)throws Exception{
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        String str=reader.readLine();
        System.out.println(countWords(str));
    }

    public static int countWords(String str){
        char[] words=str.toCharArray();
        int len=words.length;
        // 标记出发点为空格
        boolean flag=true;
        int count=0;
        for(int i=0;i<len;i++){

            // 分割符首次出现
            if(!flag&&words[i]==' '||words[i]==','||words[i]=='.') {
                flag=true;
            }
            else if(flag&&words[i]!=' '&&words[i]!=','&&words[i]!='.'){//如果当前不是分割字符且已经出现过分割字符
                flag=false;
                count++;
            }else ;// 分割字符未出现
        }
        return count;
    }
}
