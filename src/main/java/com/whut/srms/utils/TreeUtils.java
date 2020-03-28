package com.whut.srms.utils;

import com.whut.srms.pojo.fileTreeNode;

import java.util.ArrayList;
import java.util.List;

public class TreeUtils {

    /**
     * List转换成tree
     * @param listOr 待整合成树的节点列表
     * @param orderId  一级pid
     * @return
     */
    public static List<fileTreeNode> initTree(List<fileTreeNode> listOr, Long orderId){
        List<fileTreeNode> arrayList=new ArrayList<fileTreeNode>();
        for (int i = 0; i < listOr.size(); i++) {
            fileTreeNode order=listOr.get(i);
            //如果有父级id与id相同，则证明当前类为orderId的子类
            if (order.getPid() == orderId) {
                //如果是文件，则直接加入
                if (order.getIsfile() == 1) {
                    arrayList.add(order);
                } else {
                    //否则判断该子类下是否还有子类
                    List<fileTreeNode> listOrs = initTree(listOr, order.getId());
                    //如果有子类，则判断children中是否已经赋值
                    if (listOrs.size() > 0) {
                        //如果已经children已经赋值，则使用for循环将子类加入到children中
                        if (null != order.getChildren() && order.getChildren().size() > 0) {
                            List<fileTreeNode> chilOrder = order.getChildren();
                            for (int j = 0; j < listOrs.size(); j++) {
                                chilOrder.add(listOrs.get(j));
                            }
                            order.setChildren(chilOrder);
                        } else {//如果没有赋值，则将数据直接加入到children中
                            order.setChildren(listOrs);
                        }
                        arrayList.add(order);
                    } else { //如果没有值，直接加入到arrayList
                        arrayList.add(order);
                    }
                }
            }
        }
        return arrayList;
    }


    /**
     * 查询没有子类的实体类是否已赋值
     * @param arrayList
     * @param order
     * @return
     */
    public static boolean getOrder(List<fileTreeNode> arrayList,fileTreeNode order){
        boolean resu=true;
        for (int i = 0; i < arrayList.size(); i++) {
            fileTreeNode entity=arrayList.get(i);
            //如果和当前实体类重复，返回false
            if(entity.getId()==order.getId()){
                resu=false;
                break;
            }else{ //查询子类是否重复
                if(null!=entity.getChildren() && entity.getChildren().size()>0){
                    resu=getOrder(entity.getChildren(),order);
                    if(!resu){
                        break;
                    }
                }
            }
        }
        return resu;
    }

}
