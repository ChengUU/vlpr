package com.sgc.service;

import com.sgc.bean.CharTemplate;
import com.sgc.bean.RecogTemplate;
import com.sgc.dao.CharTemplateDaoProxy;
import com.sgc.main.LicensePR;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/21.
 */
public abstract class Discriminator {
    private final RecogTemplate[] recgTemplates;
    private final double[] recgRate;

    public Discriminator(RecogTemplate[] recgTemplates) {
        this.recgTemplates = recgTemplates;
        this.recgRate = new double[recgTemplates.length];
    }

    public String recg(BufferedImage image){
        int len=recgTemplates.length;
        for(int i=0;i<len;i++){
           try{ recgRate[i]=recgTemplates[i].recg(image);}catch (Exception e){System.out.println("参数异常......");}
        }
        String str=null;
        double max=Double.MIN_VALUE;
        for(int i=0;i<len;i++){
            if(max<recgRate[i]){
                max=recgRate[i];
                str=recgTemplates[i].getRepChar();
            }
        }
        return str;
    }

    protected static RecogTemplate[] getRecgTemplates(int type){
        CharTemplateDaoProxy dao=new CharTemplateDaoProxy();
        List<CharTemplate> list=dao.queryByType(type);
        list=null!=list?list:new ArrayList<>();
        int i=0;
        int len=list.size();
        RecogTemplate[] recgTemplates=new RecogTemplate[len];
        Iterator<CharTemplate> ite=list.iterator();
        while(ite.hasNext()){
            CharTemplate charTemplate=ite.next();
            String path=charTemplate.getPath();
            String repChar=charTemplate.getRepChar();
//            System.out.println(path);
//            System.out.println( LicensePR.class.getResource("/templates/"+path));

            path= LicensePR.class.getResource("/templates/"+path).getPath();
            File file=new File(path);
            try{
                BufferedImage image= ImageIO.read(file);
                recgTemplates[i++]=new RecogTemplate(image,repChar);
            }catch(IOException e){}
        }
        return recgTemplates;
    }

}
